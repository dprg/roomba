 /********************************************************************************
 * Written by Rich Ketcham February 25, 2008                                     *
 * Modified from serialdaemon.c by the FlockBots                                 *
 * http://cs.gmu.edu/~eclab/projects/robots/flockbots/pmwiki.php?n=Main.Gumstix  *
 *                                                                               *
 * This program is free software; you can redistribute it and/or modify          *
 * it under the terms of the GNU General Public License as published by          *
 * the Free Software Foundation; either version 2 of the License, or             *
 * (at your option) any later version.                                           *
 *                                                                               *
 * This program is distributed in the hope that it will be useful,               *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                 *
 * GNU General Public License for more details.                                  *
 *                                                                               *
 * You should have received a copy of the GNU General Public License             *
 * along with this program; if not, write to the Free Software                   *
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                     *
 ********************************************************************************/
/*
This program allows the user to connect a serial port (ttyS*) to a socket (http://localhost:port) and/or to a pseudo terminal (pty). Communication should be bidirectional but hasn't been exhaustively tested.

Downloaded frrom http://docwiki.gumstix.org/index.php/Sample_code/C/Serial_Daemon

Possible connections:
/dev/ttyS1 < = > 127.0.0.0:5000
/dev/ttyS1 < = > /dev/pts/0

Compiling with GCC:

gcc -c -fPIC serialdaemon.c
gcc -o serialdaemon -L. -lutil serialdaemon.o

Giving the help option produces the following output:

# ./serialdaemon -help
--------------------------------------
------------ SerialDaemon ------------
--------------------------------------
Usage:  ./serialdaemon [options] [arguments]
        -serial [Use to indicate which serial port to connect to. E.G. /dev/ttyS1]
        -port   [Use to indicate which TCP/IP port of the local host to connect to. E.G. 5000]
        -pty    [Create a pseudo terminal for the serial port to connect to.]
        -baud   [Serial port baudrate.]
                115200
                38400
                19200
                9600
        -strip  [Strip the endline character and replace with a space.]
        -debug  [Set the verbose debug mode for help.]
        -help   [For this help screen.]

Example Usage:  ./serialdaemon -serial /dev/ttyS1 -baud 115200 -pty -port 5000
This will link ttyS1 to localhost:5000 and ttyS1 to a pseudo terminal.  The connection to ttyS1 will have a baudrate of 115200.
*/
#include <fcntl.h> 
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <termios.h> // baudrate settings are defined in <asm/termbits.h>, which is included by <termios.h> 
#include <sys/time.h>
#include <sys/stat.h> 
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <signal.h>

//pseudoTY()
#include <stdlib.h>
#include <unistd.h>
#include <pty.h>

//For getIP
#include <sys/ioctl.h>
#include <net/if.h>
#include <arpa/inet.h>


#define BUFFER 1024

int sd1 = -1; //Socket File Descriptor
int sd2 = -1; //Serial Port File Descriptor

//------------getIP()--------------------//
//Returns the IP address of the localhost//
//---------------------------------------//

char *getIP(){
	
	int i;
	int s = socket (PF_INET, SOCK_STREAM, 0);
	char *IP;
	
	for (i=1;;i++)
	{
		struct ifreq ifr;
		struct sockaddr_in *sin = (struct sockaddr_in *) &ifr.ifr_addr;
		char *ip;
	
		ifr.ifr_ifindex = i;
		if (ioctl (s, SIOCGIFNAME, &ifr) < 0)
			break;
	
		/* now ifr.ifr_name is set */
		if (ioctl (s, SIOCGIFADDR, &ifr) < 0)
			continue;
	
		ip = inet_ntoa (sin->sin_addr);
		IP =ip;
	}
	
	close (s);
	return IP;
			
}

//------------makeSocket()----------------------//
//Creates a socket for com over ethernet	//
//Returns a file descriptor for the local socket//
//----------------------------------------------//
int makeSocket(int port)  //Make the socket
{
	int    sockfd,sd,childpid;
	struct sockaddr_in serv_addr;
	int v = 1;
           
	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) { //Create the socket file descriptor
		fprintf(stderr,"Server Error:  Can't open stream socket.\n");
		return -1;
	}
	
	if (setsockopt(sockfd,IPPROTO_TCP, TCP_NODELAY, &v, sizeof(v))<0) {
		perror("Setsockopt failed\n");
		return -1;
	}
        
	bzero((char*) &serv_addr, sizeof(serv_addr)); //Zeros... 
	serv_addr.sin_family        =AF_INET; //Address Family Internet Sockets
	serv_addr.sin_addr.s_addr   =htonl(INADDR_ANY);  //Address
	serv_addr.sin_port          =htons(port);  //Port
    
	if (bind(sockfd, (struct sockaddr*) &serv_addr, sizeof(serv_addr))<0) { //Bind a name to a socket (socket, address, length)
		fprintf(stderr,"Server Error:  Can't bind to local address.\n");
		return -1;
	}

	listen(sockfd,5); //Listen for socket connections and limit the queue to 5

        //Make socket non-blocking (will return immediately from accept())
	if (fcntl(sockfd, F_SETFL, O_NDELAY) < 0) {
		perror("Can't set socket to non-blocking");
		return -1;
	}
	return sockfd;
}

//------------connectSerial()-----------------------------//
//Create a connection the serial port located on port     //
//Returns a file descriptor for the serial port		  //
//--------------------------------------------------------//

int connectSerial(char port[],int baud) { //Opens serial port
	int fd;   //File Descriptor
	struct termios newtio; 
	
	fd = open(port,O_RDWR | O_NONBLOCK | O_NOCTTY); // open up the port on read / write mode  and nonblocking
	
	if (fd == -1){
		perror("ERROR: Couldn't open serial port!\nOPEN RETURNED");
		return fd; // Opps. We just had an error
	}
	
	/* Save the current serial port settings */
	tcgetattr(fd, &newtio);
	
	/* Set the input/output baud rates for this device */
	cfsetospeed(&newtio, baud); //change the baud
	cfsetispeed(&newtio, baud); //Change the baud
	
	/* CLOCAL:      Local connection (no modem control) */
	/* CREAD:       Enable the receiver */
	newtio.c_cflag |= (CLOCAL | CREAD);
	
	/* PARENB:      Use NO parity */
	/* CSTOPB:      Use 1 stop bit */
	/* CSIZE:       Next two constants: */
	/* CS8:         Use 8 data bits */
	newtio.c_cflag &= ~PARENB;
	newtio.c_cflag &= ~CSTOPB;
	newtio.c_cflag &= ~CSIZE;
	newtio.c_cflag |= CS8;
	
	/* Disable hardware flow control */
	// BAD:  newtio.c_cflag &= ~(CRTSCTS);
	
	/* ICANON:      Disable Canonical mode */
	/* ECHO:        Disable echoing of input characters */
	/* ECHOE:       Echo erase characters as BS-SP-BS */
	/* ISIG:        Disable status signals */
	// BAD: newtio.c_lflag = (ECHOK);
	
	/* IGNPAR:      Ignore bytes with parity errors */
	/* ICRNL:       Map CR to NL (otherwise a CR input on the other computer will not terminate input) */
	// BAD:  newtio.c_iflag |= (IGNPAR | ICRNL);
	newtio.c_iflag |= (IGNPAR | IGNBRK); 
	
	/* NO FLAGS AT ALL FOR OUTPUT CONTROL  -- Sean */
	newtio.c_oflag = 0;
        newtio.c_oflag &= ~(ONLCR);
	
	/* IXON:        Disable software flow control (incoming) */
	/* IXOFF:       Disable software flow control (outgoing) */
	/* IXANY:       Disable software flow control (any character can start flow control */
	newtio.c_iflag &= ~(IXON | IXOFF | IXANY);
	
	/* NO FLAGS AT ALL FOR LFLAGS  -- Sean*/
	newtio.c_lflag = 0;
	
	/*** The following settings are deprecated and we are no longer using them (~Peter) ****/
	// cam_data.newtio.c_lflag &= ~(ICANON && ECHO && ECHOE && ISIG); 
	// cam_data.newtio.c_lflag = (ECHO);
	// cam_data.newtio.c_iflag = (IXON | IXOFF);
	/* Raw output */
	// cam_data.newtio.c_oflag &= ~OPOST;
	
	//Timeouts
	//newtio.c_cc[VMIN]=0; //If x = 0, it is non-blocking
	//newtio.c_cc[VTIME]=20; //Inter-Character Timer -- i.e. timeout=x*.1 s
	
	/* Clean the modem line and activate new port settings */
	tcflush(fd, TCIOFLUSH);
	tcsetattr(fd, TCSANOW, &newtio);
	
	return fd;
}

//------------waitOnSocket()----------------------//
//Check to see if there are connections pending   //
//This will return the handle for the connection  //
//This is set for non-blocking, so the function   //
//should return right away.			  //
//------------------------------------------------//
int waitOnSocket(int sockfd) {
	struct sockaddr_in  cli_addr;
	int clilen = sizeof(cli_addr);
	int sd;

	sd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);  //Accept a connection and return the handle for that connection
	if (sd < -1 ) {
		fprintf(stderr,"Server Error:  Accept error.\n");
		return -1;
	}
	return sd;
}

//------------max()--------------------------//
//Returns the largest value of the two inputs//
//-------------------------------------------//
int max(int a, int b)
{
	return (a > b ? a : b);
}

//------------pseudoPTY()--------------------------//
//Create a psuedo terminal			   //
//Return the path and name of the PTY port         //
//Returns a file descriptor for the pseudo terminal//
//-------------------------------------------------//
int pseudoTY(char** PTY)
{
	//char *letters;
	int master;
	int slave;
	
	pid_t pid = openpty(&master,&slave,NULL,NULL,NULL);//Create a pseudo terminal
	//Use ttyname() on the file descriptor to find the name of the terminal
	//That is:  slave name = ttyname(slave);

	if(pid == -1){//Openpty() failed
		perror("Openpty() Failed!  :");
		return pid;
	} 
	if(pid == 0){//Openpty() successful
		(*PTY) = ttyname(slave);
		// Ensure that the echo is switched off 
		struct termios orig_termios;
		if (tcgetattr (master, &orig_termios) < 0) {
			perror ("ERROR getting current terminal's attributes");
			return -1;
		}
		
		orig_termios.c_lflag &= ~(ECHO | ECHOE | ECHOK | ECHONL);
		orig_termios.c_oflag &= ~(ONLCR);
		
		if (tcsetattr (master, TCSANOW, &orig_termios) < 0) {
			perror ("ERROR setting current terminal's attributes");
			return -1;
		}
		return master; //Return the file descriptor
	}
}

void controlC(int sig) {
	printf("Caught ^C: exiting\n");
	if (sd1 > 0) close(sd1);
	if (sd2>0) close(sd2);
	signal(SIGINT, SIG_DFL);
	exit(0);
}


int main(int argc, char *argv[]) {
	fd_set rset;
	struct timeval timeout;
	char * IP; //IP address of socket
	char * PTY; //Pseudo Terminal Name
	
	unsigned char cBuff[BUFFER];
	//char c[BUFFER];
	
	int csize;
	int pty = -1; //Pseudo Terminal File Descriptor
	int sockfd1, sockfd2 = -1;
	int x;
	int args = 0;
	int tmp;
	
	char argSerial[] = "-serial";
	char argHelp[]   = "-help";
	char argPort[]   = "-port";
	char argPTY[]    = "-pty";
	char argStrip[]  = "-strip";
	char argBaud[]   = "-baud";
	char argDebug[]  = "-debug";
	
	int SOCKET_PORT, BAUD, STRIP, DEBUG = 0;
	char SERIAL[100];


	for (x = 0; x < argc; x++){//Cycle through the command line arguments
		if (!strcmp(argSerial,argv[x])) {//Look for the -serial option
			strcpy(SERIAL,argv[x+1]); //Copies the port to SERIAL
			if(BAUD>0){ //If the baud option has been passed
				sockfd2 = connectSerial(SERIAL, BAUD); //Open the serial port and return the file descriptor
				if (sockfd2 < 0) {
					close(sockfd2);
					if(sockfd1>=0)
						close(sockfd1);
					if(pty>=0)
						close(pty);
					return -1;
				}else{
					args+=3;
				}
			}else{ 
				args+=3;
			}
		}
		else if (!strcmp(argPort,argv[x])) {  //Look for -port  option
			SOCKET_PORT = atoi(argv[x+1]); //Convert string address into int
			sockfd1 = makeSocket(SOCKET_PORT);  //Make the socket and return the file descriptor
			if (sockfd1 < 0) {
				close(sockfd1);
				if(sockfd2>=0)
					close(sockfd2);
				if(pty>=0)
					close(pty);
				return -1;
			}else{
				args+=5;
			}
		}
		else if (!strcmp(argPTY,argv[x])) {  //Look for -pty  option
			pty = pseudoTY(&PTY);
			if(pty<0){
				close(pty);
				if(sockfd2>=0)
					close(sockfd2);
				if(sockfd1>=0)
					close(sockfd1);
				return -1;
			}else{
				args+=7;
			}
		}
		else if (!strcmp(argBaud,argv[x])) { //Look for -baud option
			tmp = atoi(argv[x+1]); //Convert string baud rate to int
			switch (tmp) { //Make sure the value is supported
				case 115200:
					BAUD = B115200;
					break;
				case 38400:
					BAUD = B38400;
					break;
				case 19200:
					BAUD = B19200;
					break;
				case 9600:
					BAUD = B9600;
					break;
				default:
					printf("ERROR!: Unknown baud rate.\n");
					return -1;
					break;
			}
			
			if(strlen(SERIAL) != 0) //If we got the tag for a serial port, create the serial port
				sockfd2 = connectSerial(SERIAL, BAUD); //Open the serial port and return the file descriptor
			args+=1;
		}
		else if ( (args != 9 &&  args != 11 && args !=16 && x == argc-1) || (!strcmp(argHelp,argv[x]))) { 
                        //If not enough arguments, output usage directions and exit
			// Serial <=> Socket  w/ baud  = 9
			// Serial <=> PTY  w/ baud = 11
			// Serial <=> Socket, Serial <=> PTY w/ baud = 16
			printf("--------------------------------------\n");
			printf("------------ SerialDaemon ------------\n");
			printf("--------------------------------------\n");
			printf("Usage:");
			printf("\t./serialdaemon [options] [arguments]\n");
			printf("\t-serial [Use to indicate which serial port to connect to. E.G. /dev/ttyS1]\n");
			printf("\t-port   [Use to indicate which TCP/IP port of the local host to connect to. E.G. 5000]\n");
			printf("\t-pty    [Create a pseudo terminal for the serial port to connect to.]\n");
			printf("\t-baud   [Serial port baudrate.]\n");
			printf("\t\t115200\n");
			printf("\t\t38400\n");
			printf("\t\t19200\n");
			printf("\t\t9600\n");
			printf("\t-strip  [Strip the endline character and replace with a space.]\n");
			printf("\t-debug  [Set the verbose debug mode for help.]\n");
			printf("\t-help   [For this help screen.]\n\n");
			printf("Example Usage:\t./serialdaemon -serial /dev/ttyS1 -baud 115200 -pty -port 5000\n");
			printf("This will link ttyS1 to localhost:5000 and ttyS1 to a pseudo terminal.  The connection to ttyS1 will have a baudrate of 115200.\n");
			return -1;
		}
		else if (!strcmp(argStrip,argv[x])){STRIP = 1;}  //Look for the -strip option
		else if (!strcmp(argDebug,argv[x])){DEBUG = 1; printf ("DEBUG: debug mode on!\n");}  //Look for the -debug option
		
	}
	
	signal(SIGINT, controlC);	// catch ^C so we can close sockets & exit cleanly

	IP = getIP(); //Get the local IP address

	if(args == 9)//Serial to Socket
		printf("Connections made: \n\t\t\t%s < = > http://%s:%d\n",SERIAL,IP,SOCKET_PORT); 
	else if(args == 11)//Serial to PTY
		printf("Connections made: \n\t\t\t%s < = > %s\n",SERIAL,PTY); 
	else if(args == 16)//Serial to PTY  &  Serial to Socket
		printf("Connections made: \n\t\t\t%s < = > http://%s:%d\n\t\t\t%s < = > %s\n",SERIAL,IP,SOCKET_PORT,SERIAL,PTY); 

	sd1 = waitOnSocket(sockfd1); //Check for a connection to the socket

	while(1){
		/* Select on sockets */
		if(sd1 > 0){ //If There is a connection to the socket then potentially set up these connections:  tty < = > socket and tty < = > pty
			if (DEBUG)
				printf("DEBUG: New client socket opened.\n");
			if (sd1 < 0) { //Error in creating connection
				close(sd1); 
				return -1;
			}
			sd2 = sockfd2; //File descriptor of the serial port
			if (sd2 < 0) { //If an error
				close(sd1);
				close(sd2); 
				return -1;
			}
			FD_ZERO(&rset); //Clear file descriptors in the rset set
			while(1) {
				FD_SET(sd1,&rset);//Set sd1 in rset
				FD_SET(sd2,&rset);//Set sd2 in rset
				if(pty!=-1) {//If a virtual port is requested
					FD_SET(pty,&rset);
					select(max(max(sd1,sd2),pty)+1,&rset,NULL,NULL,NULL); 
                                       //Select specifies which of the file descriptors is ready for reading or writing
				}else {
					select(max(sd1,sd2)+1,&rset,NULL,NULL,NULL); 
					//Select tests file descriptors in the range of 0 to nfds-1 or in this case 0 to max(sd1,sd2).
				}
				//----------------Check The Socket For Data ------------------
				if (FD_ISSET(sd1,&rset)) {  //Is there stuff to read from the socket
					/* There's stuff to read */
					if ((csize= read(sd1, &cBuff, BUFFER)) >= 1) { //If there's something worth reading
						if (STRIP==1) { //Remove endline characters and replace with space
							for(x = 0 ; x < csize; x++) {
								if (cBuff[x] == '\n' ) {
									cBuff[x] = ' ';
									if (DEBUG)
										printf ("DEBUG: **STRIPPED**\n");
								}
							}
						}
						if (DEBUG) {
							//Replace &cBuff and cBuff with c
							//cBuff[csize] = '\0';
							printf("\nDEBUG: %s <== ",SERIAL);
							for(x=0; x<csize;x++){
								printf("%#.2x ",cBuff[x]);
							}
							printf("\n");
						}
						write(sd2, &cBuff, csize);//Write data from sd1 to sd2
					}else{break;}// Failed  -- port closed
				}
				
				//----------------Check The Serial Port For Data ------------------
				if (FD_ISSET(sd2,&rset)) {//Is there stuff to read from the serial port
					if ((csize = read(sd2, &cBuff, BUFFER)) >= 1) {//If there is something worth reading from the serial port
						write(sd1, &cBuff, csize); //Write this data to the socket
						if(pty != -1){write(pty,&cBuff,csize);} //Write this data to the virtual com port
						if (DEBUG) {
							//Replace &cBuff and cBuff with c
							//cBuff[csize] = '\0';
							printf("DEBUG: http://%s:%d <== ",IP,SOCKET_PORT);
							for(x=0; x<csize;x++){
								printf("%#.2x ",cBuff[x]);
							}
							printf("\n");
							
							if(pty !=-1){
								printf("DEBUG: %s <== ",PTY);
								for(x=0; x<csize;x++){
									printf("%#.2x ",cBuff[x]);
								}
								printf("\n");
							}
						}
					}
					//else break;           /* Failed */
				}
				//----------------Check The PTY Port For Data ------------------
				if (pty != -1 && FD_ISSET(pty,&rset)) {//If there is a virtual port, and data is ready, write data from 
					if ((csize = read(pty, &cBuff, BUFFER)) >= 1) {//If there is something worth reading from the serial port
						write(sd2, &cBuff, csize); //Write this data to the serial port
						if (DEBUG) {
							//Replace &cBuff and cBuff with c
							//cBuff[csize] = '\0';
							printf("\nDEBUG: %s <== ",SERIAL);
							for(x=0; x<csize;x++){
								printf("%#.2x ",cBuff[x]);
							}
							printf("\n");
						}
					}
					//else break;           /* Failed */
				}
			}
			printf("Restarting\n");
			close(sd1);/* clean up */
			sd1 = waitOnSocket(sockfd1); //Check for a connection to the socket

		}else if(pty != -1) {//Else, if there is a virtual port then tty <=> pty
			sd2 = sockfd2; //File descriptor of the serial port
			if (sd2 < 0) { //If an error
				close(sd2); 
				close(pty);
				return -1;
			}
			FD_ZERO(&rset); //Clear file descriptors in the rset set
			while(1) {
				FD_SET(sd2,&rset);//Set sd2 in rset
				FD_SET(pty,&rset);//Set pty in rset
				sd1 = waitOnSocket(sockfd1);
				if(sd1 >= 0){break;} //Check for socket connection, if there is, break out of this loop.
				select(max(sd2,pty)+1,&rset,NULL,NULL,NULL); // Specifies which of the file descriptors is ready for reading or writing
				if (FD_ISSET(pty,&rset)) { //If there is a virtual port, and data is ready, write data from 
					if ((csize = read(pty, &cBuff, BUFFER)) >= 1) {//If there is something worth reading from the serial port
						write(sd2, &cBuff, csize); //Write this data to the serial port
						if (DEBUG) {
							//Replace &cBuff and cBuff with c
							//cBuff[csize] = '\0';
							printf("\nDEBUG: %s <== ",SERIAL);
							for(x=0; x<csize;x++){
								printf("%#.2x ",cBuff[x]);
							}
							printf("\n");
						}
					}
					//else break;           // Failed 
				}


				if (FD_ISSET(sd2,&rset)) {//Is there stuff to read from the serial port				  
					if ((csize = read(sd2, &cBuff, BUFFER)) >= 1) { //If there is something worth reading from the serial port
						write(pty, &cBuff, csize); //Write this data to the virtual com port
						if (DEBUG) {
							//Replace &cBuff and cBuff with c
							//cBuff[csize] = '\0';
							printf("DEBUG: %s <== ",PTY);
							for(x=0; x<csize;x++){
								printf("%#.2x ",cBuff[x]);
							}
							printf("\n");
						}
					}
					//else break;           /* Failed */
				}

			}
		}else {//If there isn't a pty, then check the socket for a connection once a second
			if(DEBUG)
				printf("\rWaiting on the socket connection...\n");
			sd1 = waitOnSocket(sockfd1); //Check for a connection to the socket
			sleep(1);
		}
	}
}

