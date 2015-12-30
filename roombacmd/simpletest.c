/*
 * simpletest -- simple demo of roombalib
 *
 * http://hackingroomba.com/
 *
 * Copyright (C) 2006, Tod E. Kurt, tod@todbot.com
 *
 * Updates:
 * 14 Dec 2006 - added more functions to roombalib
 */


#include <stdio.h>    /* Standard input/output definitions */
#include <string.h>   /* String function definitions */

#include "roombalib.h"

void usage()
{
    fprintf(stderr,"usage: simpletest[500] -p serialport\n");
    fprintf(stderr,"create a link: simpletest500 if you want to test 500-series\n");
    exit(0);
}

int main(int argc, char *argv[]) 
{
    char* serialport;
    speed_t baud = B57600;

fprintf(stderr, argv[0]);
    if ((!strncmp((argv[0]+strlen(argv[0])-13), "simpletest500",13))) {
        fprintf(stderr,"roomba 500 series\n");
        baud = B115200;	// running as simpletest500 sets baud rate to 115200
    } 

    if( argc>1 && strcmp(argv[1],"-p" )==0 ) {
        serialport = argv[2];
    } else {
        usage();
    }

    roombadebug = 1;

    Roomba* roomba = roomba_init( serialport, baud ); 
    
    roomba_forward( roomba );
    roomba_delay(1000); 

    roomba_backward( roomba );
    roomba_delay(1000); 

    roomba_spinleft( roomba );
    roomba_delay(1000); 

    roomba_spinright( roomba );
    roomba_delay(1000); 

    roomba_stop( roomba );
    
    roomba_close( roomba );
    roomba_free( roomba );

    return 0;
}


