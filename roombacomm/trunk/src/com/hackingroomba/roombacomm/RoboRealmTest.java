package com.hackingroomba.roombacomm;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;

public class RoboRealmTest
{
  public static void sendImage(RoboRealmAPI rr, Image img)
  {
    int width = img.getWidth(null);
    int height = img.getHeight(null);

    // create your own copy of the data in case the parent goes away ...
    int pixelInt[] = new int[width*height];
    byte pixelByte[] = new byte[width*height*3];

    // Get the image data
    PixelGrabber grabber = new PixelGrabber(img, 0, 0, width, height, pixelInt, 0, width);

    try
    {
      grabber.grabPixels();

      int i,j;

      for (j=i=0;i<width*height;)
      {
        int num = pixelInt[i++];
        pixelByte[j++] = (byte)(num&255);
        pixelByte[j++] = (byte)((num>>8)&255);
        pixelByte[j++] = (byte)((num>>16)&255);
      }

      // send image to RR
      rr.setImage(pixelByte, width, height);
    }
    catch (Exception e)
    {
    };
  }


  // This is where the program first starts
  public static void main(String[] args)
  {
    byte image[] = new byte[1280*960];
    RoboRealmAPI rr = new RoboRealmAPI();

    if (!rr.connect("localhost"))
    {
      System.out.println("Could not connect to RoboRealm on localhost!");
      return;
    }

    // load an image using Java awt

    Dimension d = rr.getDimension();
    if (d!=null)
    {
      System.out.println("Dimension "+d.width+"x"+d.height);
    }

    // VARIABLES

    // set a custom variable to test
    rr.setVariable("custom_var", "test");

    // read back our custom variable ... should be equal to 'test'
    String res = rr.getVariable("custom_var");
    if (!res.equals("test"))
    {
      System.out.println("Error in custom_var");
      return;
    }

    // delete our custom variable
    if (!rr.deleteVariable("custom_var"))
    {
      System.out.println("Error in delete variable");
      return;
    }

    // try to get it back again ... should be empty
    res = rr.getVariable("custom_var");
    if (res!=null)
    {
      System.out.println("Error in delete custom_var");
      return;
    }

    // set multiple variables
    String names[] = new String[2];
    String values[] = new String[2];
    names[0]="custom_var_1";
    names[1]="custom_var_2";
    values[0] = "test1";
    values[1] = "test2";
    rr.setVariables(names, values, 2);

    // get multiple variables
    Vector v = rr.getVariables("custom_var_1, custom_var_2");
    if (v==null)
    {
      System.out.println("Error in GetVariables, did not return any results");
      return;
    }
    else
    {
      if (!((String)v.elementAt(0)).equals("test1"))
      {
        System.out.println("Error in get/set multiple variables. Got "+(String)v.elementAt(0));
        return;
      }
      if (!((String)v.elementAt(1)).equals("test2"))
      {
        System.out.println("Error in get/set multiple variables. Got "+(String)v.elementAt(1));
        return;
      }
    }

    // IMAGES

    // ensure that the camera is on and processing images
    rr.setCamera("on");
    rr.run("on");

    // execute a RGB filter on the loaded image
    rr.execute("<head><version>1.50</version></head><RGB_Filter><min_value>40</min_value><channel>3</channel></RGB_Filter>");

    // get the current processed image from RoboRealm and save as a PPM
    d = rr.getImage(image, 1280*960);
    if (d!=null)
    {
      rr.savePPM("c:\\temp\\test.ppm", image, d.width, d.height);
    }

    // get the current source image from RoboRealm and save as a PPM
    d = rr.getImage("source", image, 1280*960);
    if (d!=null)
    {
      rr.savePPM("c:\\temp\\test2.ppm", image, d.width, d.height);
    }

    // turn off live camera
    rr.setCamera("off");

    // load an image for experimentation
    d = rr.loadPPM("c:\\Program Files (x86)\\RoboRealm\\remo.ppm", image, 320*240*3);

    // change the current image
    rr.setImage(image, d.width, d.height);

    // load an image using Java awt
    try
    {
      Image img = Toolkit.getDefaultToolkit().getImage(new URL("http://www.google.com/intl/en_ALL/images/logo.gif"));
      sendImage(rr, img);
    }
    catch (Exception e)
    {
    };

    // add a marker image called my_new_image
    rr.setImage("my_new_image", image, d.width, d.height);

    // run a .robo program
    //rr.loadProgram("c:\\Program Files\\RoboRealm\\scripts\\red.robo");

    // load an image from disk
    rr.loadImage(null, "c:\\Program Files (x86)\\RoboRealm\\remo.gif");

    // save that image back to disk .. note that we can switch extensions
    rr.saveImage(null, "c:\\temp\\remo.jpg");

    rr.setCamera("on");
    // change the camera to another one
    //rr.setCamera("CompUSA PC Camera");
    try
    {
      Thread.sleep(2000);
    }
    catch (Exception e)
    {
    };
    // now set it back
    //rr.setCamera("Logitech");

    // turn off processing
    rr.run("off");
    try
    {
      Thread.sleep(2000);
    }
    catch (Exception e)
    {
    };
    // run once
    rr.run("once");
    try
    {
      Thread.sleep(2000);
    }
    catch (Exception e)
    {
    };
    // run for 100 frames (~3.3 seconds) .. note that if your frame rate is different this
    // may be longer than 4 seconds
    System.out.println("running for 100 frames");
    rr.run("100");
    try
    {
      Thread.sleep(4000);
    }
    catch (Exception e)
    {
    };
    // turn processing back on
    rr.run("on");

    // wait for the image count to exceed 1000 (assuming a 30 fps here)
    System.out.println("waiting for image count to exceed 1000");
    rr.waitVariable("image_count", "500", 100000);

    // wait for a new image
    System.out.println("waiting for a new image");
    rr.waitImage(5000);

    // close the RoboRealm application .. if you want too ... otherwise leave it running
    //rr.close();

    // disconnect from API Server
    rr.disconnect();
    
    System.out.println("Finished RoboRealmTest");
  }
}
