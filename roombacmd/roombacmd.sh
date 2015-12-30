#!/bin/sh
#
# roombacmd.sh -- Simple shell-based Roomba command-line too
#
# http://roombahacking.com/
#
# Copyright (C) 2006, Tod E. Kurt, tod@todbot.com
#
#

# in case we have stty in the current directory
PATH=${PATH}:.

usage() {
        echo "Usage: $0 {serialport} {init|forward|backward|spinleft|spinright|stop|beep|poweroff}" >&2
        echo "Create a link: "roombacmd500.sh" to control 500-series roombas" >&2
        exit 1
}

roomba_init() {
    os=`uname -s`
    if [ "$os" == "Linux" ]; then
        stty -F $PORT $BAUD raw -parenb -parodd cs8 -hupcl -cstopb clocal
    elif [ "$os" == "Darwin" ]; then
        stty -f $PORT $BAUD raw -parenb -parodd cs8 -hupcl -cstopb clocal
    fi
    printf "\x80" > $PORT;   sleep 1
    printf "\x82" > $PORT;   sleep 1
}
roomba_forward() {
    vel="\x00\xc8"
    rad="\x80\x00"
    printf "\x89$vel$rad" > $PORT
}
roomba_backward() {
    vel="\xff\x38"
    rad="\x80\x00"
    printf "\x89$vel$rad" > $PORT
}
roomba_spinleft() {
    vel="\x00\xc8"
    rad="\x00\x01"
    printf "\x89$vel$rad" > $PORT
}
roomba_spinright() {
    vel="\x00\xc8"
    rad="\xff\xff"
    printf "\x89$vel$rad" > $PORT
}
roomba_stop() {
    vel="\x00\x00"
    rad="\x00\x00"
    printf "\x89$vel$rad" > $PORT
}
roomba_poweroff() {
    printf "\x85" > $PORT
}

roomba_beep() {
    echo "beep communications test" 
# GSTQ: (caps = crochet, lower case = quaver)
# GGAFgA BBCBaG AGFG
    printf "\x8c\x1\x6\x43\x20\x43\x20\x45\x20\x41\x30\x43\x10\x45\x20" > $PORT
    printf "\x8d\x1" > $PORT
    sleep 3
    printf "\x8c\x1\x6\x47\x20\x47\x20\x48\x20\x47\x30\x45\x10\x43\x20" > $PORT
    printf "\x8d\x1" > $PORT
    sleep 3
    printf "\x8c\x1\x4\x45\x20\x43\x20\x41\x20\x43\x20" > $PORT
    printf "\x8d\x1" > $PORT
    sleep 2
    printf "\x8a\x7" > $PORT
    sleep 1
    printf "\x8a\x0" > $PORT
}

# If not enough arguments were passed, return
[ -z "$2" ] && usage

# Set the baud rate
BAUD=57600
    if [ "$0" == "./roombacmd500.sh" ]; then
        BAUD=115200
    fi

PORT=$1

case $2 in
    init)
        roomba_init
        ;;
    forward)
        roomba_forward
        ;;
    backward)
        roomba_backward
        ;;
    spinleft)
        roomba_spinleft
        ;;
    spinright)
        roomba_spinright
        ;;
    stop)
        roomba_stop
        ;;
    beep)
        roomba_init
        roomba_beep
        ;;
    poweroff)
        roomba_poweroff
        ;;
    *)
        usage
        ;;
esac
#exit 