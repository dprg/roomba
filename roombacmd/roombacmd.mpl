#!/usr/bin/microperl
#
# roombacmd.pl -- Roomba command-line tool,  part of RoombaComm
#
# http://roombahacking.com/
#
# Copyright (C) 2006, Tod E. Kurt, tod@todbot.com
#
#

# in case we have stty in the current directory
$ENV{'PATH'}="$ENV{PATH}:.";


sub usage() {
    printf "Usage: $0 {serialport} {init|forward|backward|spinleft|spinright|stop|poweroff}\n";
    exit(1);
}


sub roomba_init() {
    # this style stty is for linux
#    system("stty -F $PORT 115200 raw -parenb -parodd cs8 -hupcl -cstopb clocal");
#    stty ttyUSB0 115200 raw -parenb cs8 -hupcl -cstopb clocal;
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x80"); sleep 1;
    printf(TTYS0 "\x83"); 
}
sub roomba_forward() {
    $vel="\x00\x40";
    $rad="\x80\x00";
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x89$vel$rad");
}
sub roomba_backward() {
    $vel="\xff\xc0";
    $rad="\x80\x00";
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x89$vel$rad");
}
sub roomba_spinleft() {
    $vel="\x00\x40";
    $rad="\x00\x01";
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x89$vel$rad");
}
sub roomba_spinright() {
    $vel="\x00\x40";
    $rad="\xff\xff";
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x89$vel$rad");
}
sub roomba_stop() {
    $vel="\x00\x00";
    $rad="\x00\x00";
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x89$vel$rad");
}

sub roomba_poweroff() {
    open TTYS0, ">$PORT" or die ("Cannot open $PORT");
    printf(TTYS0 "\x85");
}
    

# If not enough arguments were passed, return
usage() if( @ARGV < 2 );

$PORT = $ARGV[0];
$CMD  = $ARGV[1];

if( $CMD eq 'init' ) {
    roomba_init();
}
elsif( $CMD eq 'forward' ) {
    roomba_forward();
}
elsif( $CMD eq 'backward' ) {
    roomba_backward();
}
elsif( $CMD eq 'spinleft' ) {
    roomba_spinleft();
}
elsif( $CMD eq 'spinright' ) {
    roomba_spinright();
}
elsif( $CMD eq 'stop' ) {
    roomba_stop();
}
elsif( $CMD eq 'poweroff' ) {
    roomba_poweroff();
}
else {
    usage();
}

