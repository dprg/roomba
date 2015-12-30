
/*******************************************************************************
#             uvccapture: USB UVC Video Class Snapshot Software                #
#This package work with the Logitech UVC based webcams with the mjpeg feature  #
#.                                                                             #
# 	Orginally Copyright (C) 2005 2006 Laurent Pinchart &&  Michel Xhaard   #
#       Modifications Copyright (C) 2006  Gabriel A. Devenyi                   #
#                                                                              #
# This program is free software; you can redistribute it and/or modify         #
# it under the terms of the GNU General Public License as published by         #
# the Free Software Foundation; either version 2 of the License, or            #
# (at your option) any later version.                                          #
#                                                                              #
# This program is distributed in the hope that it will be useful,              #
# but WITHOUT ANY WARRANTY; without even the implied warranty of               #
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                #
# GNU General Public License for more details.                                 #
#                                                                              #
# You should have received a copy of the GNU General Public License            #
# along with this program; if not, write to the Free Software                  #
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA    #
#                                                                              #
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <jpeglib.h>
#include <time.h>
#include <linux/videodev2.h>
#include <pthread.h>

#include "v4l2uvc.h"

static const char version[] = VERSION;
int run = 1;
int logSocket = 0;
int verbose = 0;

char *videodevice = "/dev/video0";
int format = V4L2_PIX_FMT_MJPEG;
int grabmethod = 1;
int width = 640;
int height = 480;
int brightness = 0, contrast = 0, saturation = 0, gain = 0;
int delay = 0;
int quality = 95;
int post_capture_command_wait = 0;
struct vdIn *videoIn;
int port = 5005;
int outputType = 0;

typedef struct {
  int doCapture;	// set to 1 by cmdHandler, cleared by main when capture is complete
  unsigned char *imgArray;
  int imgLength;
  int imgWidth;
  int imgHeight;
  int videoSocket;
  int pixelCnt;
} ctrlStruct;

int yuyv2Y (struct vdIn *vd,  ctrlStruct *ctrl)
{
  unsigned char *yuyv, *iA;
  unsigned char *end;
  
  iA = ctrl->imgArray;
  ctrl->imgWidth = vd->width;
  ctrl->imgHeight = vd->height;
  yuyv = vd->framebuffer;
  end = yuyv + 2*(ctrl->imgWidth * ctrl->imgHeight); // 2 bytes/pixel
  for (; yuyv<end; yuyv+=2, iA++) {
	  *iA = *yuyv;
  }
  ctrl->imgLength = (int)iA - (int)(ctrl->imgArray);
  ctrl->pixelCnt = ctrl->imgLength;
}

int yuyv2rgb (struct vdIn *vd, ctrlStruct *ctrl)
{
  unsigned char *yuyv, *iA;
  unsigned char *end;
  int r, g, b;
  int y0, y2, u, v;
  int z=0;
  int pixelCnt = 0;

  iA = ctrl->imgArray;
  ctrl->imgWidth = vd->width;
  ctrl->imgHeight = vd->height;
  
  yuyv = vd->framebuffer;
  end = yuyv + 2*(ctrl->imgWidth * ctrl->imgHeight); // 2 bytes/pixel

  for (; yuyv<end; yuyv+=4) {
    y0 = yuyv[0] << 8;
    u = yuyv[1] - 128;
    v = yuyv[3] - 128;

    r = (y0 + (359 * v)) >> 8;
    g = (y0 - (88 * u) - (183 * v)) >> 8;
    b = (y0 + (454 * u)) >> 8;

    *(iA++) = (r > 255) ? 255 : ((r < 0) ? 0 : r);
    *(iA++) = (g > 255) ? 255 : ((g < 0) ? 0 : g);
    *(iA++) = (b > 255) ? 255 : ((b < 0) ? 0 : b);

    y2 = yuyv[2] << 8;

    r = (y2 + (359 * v)) >> 8;
    g = (y2 - (88 * u) - (183 * v)) >> 8;
    b = (y2 + (454 * u)) >> 8;

    *(iA++) = (r > 255) ? 255 : ((r < 0) ? 0 : r);
    *(iA++) = (g > 255) ? 255 : ((g < 0) ? 0 : g);
    *(iA++) = (b > 255) ? 255 : ((b < 0) ? 0 : b);
    
    pixelCnt += 2;
  }
  ctrl->imgLength = (int)iA - (int)ctrl->imgArray;
  ctrl->pixelCnt = pixelCnt;
}

void
sigcatch (int sig)
{
  fprintf (stderr, "Exiting...\n");
  run = 0;
  sleep(2);
  exit(0);
}

void *
openLogSocket(void * logport)
{
  int port = *(int*)logport;
  char buf[256];
  fprintf (stderr, "waiting for log client connection on port %d\n", port);
  if ((logSocket = wait4client(port)) <= 0) {
    fprintf (stderr, "error connecting to client: %d\n", logSocket);
    exit(-1);
  }
  fprintf (stderr, "log socket connected\n");
  sprintf (buf, "uvcslicesrvr version %s logging port\n", version);
  write(logSocket, buf, strlen(buf));
  pthread_exit((void *)0);
}

/* handle commands from RoombaComm client as long as the socket stays open */
void *
cmdHandler(ctrlStruct * ctrl)
{
  unsigned char clientCmd[256];
  ctrlStruct *pc;
  int rv;
  int netInt;

  while (run) {
    //fprintf (stderr, "waiting for command from client\n");
    rv = read(ctrl->videoSocket, &clientCmd, 10);
    if (rv < 0) {
	    perror("socket read: ");
	    exit(-1);
    }
    if (verbose > 1) fprintf (stderr, "received command %d\n", clientCmd[0]);

    if (clientCmd[0] == 200)
      outputType = 0;		// grayscale output
    else if (clientCmd[0] == 201)
      outputType = 1;		// rgb output

    ctrl->doCapture = 1;	// tell startvideoserver() to capture a snapshot
    if (verbose > 1) fprintf (stderr, "waiting for capture to complete");

    while (ctrl->doCapture == 1) {
      fprintf (stderr, ".");
      usleep(50000);
    }
    
    if (verbose > 2) fprintf (stderr, "writing %d image bytes to socket, width %d height %d, pixelCnt %d\n", ctrl->imgLength, ctrl->imgWidth, ctrl->imgHeight, ctrl->pixelCnt);

    // convert size variables to network byte order
    netInt = htonl(ctrl->imgWidth);
    write (ctrl->videoSocket, &netInt, 4);
    netInt = htonl(ctrl->imgHeight);
    write (ctrl->videoSocket, &netInt, 4);
    netInt = htonl(ctrl->imgLength);
    write (ctrl->videoSocket, &netInt, 4);
    write (ctrl->videoSocket, ctrl->imgArray, ctrl->imgLength);
    if (verbose > 2) fprintf (stderr, "wrote image & params\n");
  }
}

void startVideoSrvr()
{
  pthread_t videoSocketThread;

  /* alloc mameory for the videoIn struct & initialize */
  videoIn = (struct vdIn *) calloc (1, sizeof (struct vdIn));
  if (init_videoIn
      (videoIn, (char *) videodevice, width, height, format, grabmethod) < 0)
    exit (1);

  /* alloc memory for the control struct & video out array & initialize */
  ctrlStruct ctrl, *pc;
  if ((ctrl.imgArray = malloc(3 * width * height)) < 0) // enough space for rgb
    exit(-1);
  ctrl.doCapture = 0;

  //Reset all camera controls
  if (verbose >= 1)
    fprintf (stderr, "Resetting camera settings\n");
  v4l2ResetControl (videoIn, V4L2_CID_BRIGHTNESS);
  v4l2ResetControl (videoIn, V4L2_CID_CONTRAST);
  v4l2ResetControl (videoIn, V4L2_CID_SATURATION);
  v4l2ResetControl (videoIn, V4L2_CID_GAIN);

  //Setup Camera Parameters
  if (brightness != 0) {
    if (verbose >= 1)
      fprintf (stderr, "Setting camera brightness to %d\n", brightness);
    v4l2SetControl (videoIn, V4L2_CID_BRIGHTNESS, brightness);
  } else if (verbose >= 1) {
    fprintf (stderr, "Camera brightness level is %d\n",
	     v4l2GetControl (videoIn, V4L2_CID_BRIGHTNESS));
  }
  if (contrast != 0) {
    if (verbose >= 1)
      fprintf (stderr, "Setting camera contrast to %d\n", contrast);
    v4l2SetControl (videoIn, V4L2_CID_CONTRAST, contrast);
  } else if (verbose >= 1) {
    fprintf (stderr, "Camera contrast level is %d\n",
	     v4l2GetControl (videoIn, V4L2_CID_CONTRAST));
  }
  if (saturation != 0) {
    if (verbose >= 1)
      fprintf (stderr, "Setting camera saturation to %d\n", saturation);
    v4l2SetControl (videoIn, V4L2_CID_SATURATION, saturation);
  } else if (verbose >= 1) {
    fprintf (stderr, "Camera saturation level is %d\n",
	     v4l2GetControl (videoIn, V4L2_CID_SATURATION));
  }
  if (gain != 0) {
    if (verbose >= 1)
      fprintf (stderr, "Setting camera gain to %d\n", gain);
    v4l2SetControl (videoIn, V4L2_CID_GAIN, gain);
  } else if (verbose >= 1) {
    fprintf (stderr, "Camera gain level is %d\n",
	     v4l2GetControl (videoIn, V4L2_CID_GAIN));
  }

  // wait for a video client to connect before proceeding
  fprintf (stderr, "waiting for video client connection on port %d\n", port);
  if ((ctrl.videoSocket = wait4client(port)) <= 0) {
    fprintf (stderr, "error connecting to client: %d\n", ctrl.videoSocket);
    exit(-1);
  }
  // start the thread that handles the video client requests
  pthread_create(&videoSocketThread, NULL, (void *)cmdHandler, (void *)&ctrl);

  while (run) {
    if (verbose >= 2)
      fprintf (stderr, ".");

    if (uvcGrab (videoIn) < 0) {
      fprintf (stderr, "Error grabbing\n");
      close_v4l2 (videoIn);
      free (videoIn);
      exit (1);
    }

    if (ctrl.doCapture == 1) {
      if (verbose >= 1) {
	fprintf (stderr, "captured %d byte image at 0x%x %dx%d\n", videoIn->framesizeIn, 
		videoIn->framebuffer, videoIn->width, videoIn->height);
      } else {
        fprintf (stderr, ".");
      }

      if (outputType == 0)
	yuyv2Y(videoIn, &ctrl);
      else
        yuyv2rgb(videoIn, &ctrl);

      if (verbose >=1)
	      fprintf (stderr, "converted image to luminance in buffer at 0x%x\n", ctrl.imgArray);

      videoIn->getPict = 0;
      ctrl.doCapture = 0;
     }
  }
  close_v4l2 (videoIn);
  free (videoIn);

  return;
}

void usage (void)
{
  fprintf (stderr, "uvcslicesrvr version %s\n", version);
  fprintf (stderr, "Usage is: uvcslicesrvr [options]\n");
  fprintf (stderr, "Options:\n");
  fprintf (stderr, "-v\t\tVerbose (repeat for more verbosity)\n");
//  fprintf (stderr, "-o<filename>\tOutput filename(default: snap.jpg)\n");
  fprintf (stderr, "-d<device>\tV4L2 Device(default: /dev/video0)\n");
  fprintf (stderr, "-l<logging_port>\tLogging port (default: 5006)\n");
  fprintf (stderr, "-p<port>\tServer port (default: 5005)\n");
  fprintf (stderr,
	   "-x<width>\tImage Width(must be supported by device)(>960 activates YUYV capture)\n");
  fprintf (stderr,
	   "-y<height>\tImage Height(must be supported by device)(>720 activates YUYV capture)\n");
  fprintf (stderr,
	   "-c<command>\tCommand to run after each image capture(executed as <command> <output_filename>)\n");
  fprintf (stderr,
	   "-t<integer>\tTake continuous shots with <integer> seconds between them (0 for single shot)\n");
//  fprintf (stderr,
//	   "-q<percentage>\tJPEG Quality Compression Level (activates YUYV capture)\n");
  fprintf (stderr, "-r\t\tUse read instead of mmap for image capture\n");
  fprintf (stderr,
	   "-w\t\tWait for capture command to finish before starting next capture\n");
  fprintf (stderr, "-m\t\tToggles capture mode to YUYV capture\n");
  fprintf (stderr, "Camera Settings:\n");
  fprintf (stderr, "-B<integer>\tBrightness\n");
  fprintf (stderr, "-C<integer>\tContrast\n");
  fprintf (stderr, "-S<integer>\tSaturation\n");
  fprintf (stderr, "-G<integer>\tGain\n");
  exit (8);
}


int main (int argc, char *argv[])
{
  int logport = 5006;
  pthread_t logSocketThread;
  int rv;
  int pchild;

  (void) signal (SIGINT, sigcatch);
  (void) signal (SIGQUIT, sigcatch);
  (void) signal (SIGKILL, sigcatch);
  (void) signal (SIGTERM, sigcatch);
  (void) signal (SIGABRT, sigcatch);
  (void) signal (SIGTRAP, sigcatch);

  //Options Parsing (FIXME)
  while ((argc > 1) && (argv[1][0] == '-')) {
    switch (argv[1][1]) {
    case 'v':
      verbose++;
      break;

    case 'd':
      videodevice = &argv[1][2];
      break;

    case 'x':
      width = atoi (&argv[1][2]);
      break;

    case 'y':
      height = atoi (&argv[1][2]);
      break;

    case 'r':
      grabmethod = 0;
      break;

    case 'm':
      format = V4L2_PIX_FMT_YUYV;
      break;

    case 't':
      delay = atoi (&argv[1][2]);
      break;

    case 'w':
      post_capture_command_wait = 1;
      break;

    case 'B':
      brightness = atoi (&argv[1][2]);
      break;

    case 'C':
      contrast = atoi (&argv[1][2]);
      break;

    case 'S':
      saturation = atoi (&argv[1][2]);
      break;

    case 'G':
      gain = atoi (&argv[1][2]);
      break;

    case 'q':
      quality = atoi (&argv[1][2]);
      break;

    case 'h':
      usage ();
      break;

    case 'p':
      port = atoi (&argv[1][2]);
      break;

    case 'l':
      logport = atoi (&argv[1][2]);
      break;

    default:
      fprintf (stderr, "Unknown option %s \n", argv[1]);
      usage ();
    }
    ++argv;
    --argc;
  }

  // start the thread that waits for a log client to connect
  pthread_create(&logSocketThread, NULL, openLogSocket, (void *)&logport);

  if ((width > 960) || (height > 720) || (quality != 95))
    format = V4L2_PIX_FMT_YUYV;

  if (verbose >= 1) {
    fprintf (stderr, "Using videodevice: %s\n", videodevice);
    //fprintf (stderr, "Saving images to: %s\n", outputfile);
    fprintf (stderr, "Using port %d for image data and commands, port %d for logs\n", port, logport);
    fprintf (stderr, "Image size: %dx%d\n", width, height);
    if (grabmethod == 1)
      fprintf (stderr, "Taking images using mmap\n");
    else
      fprintf (stderr, "Taking images using read\n");
  }
  
  while (1) {	// fork video server forever
    if ( (pchild = fork()) == 0 ) {
      printf("...starting child\n");
      startVideoSrvr();
      exit(500);
    } else if ( pchild > 0 ) {
      int result;
      printf("awaiting child's termination...\n");
      wait(&result);
      printf("Child's result=%d...", result);
    } else {
      perror("Fork failure");
      exit(0);  
    }
  }
}

