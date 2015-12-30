CC=arm-linux-gcc

all:
	$(CC)  -c -fPIC serialdaemon.c
	$(CC) -o serialdaemon_arm -L. -lutil serialdaemon.o

install:
	scp serialdaemon_arm root@192.168.15.112:/mnt/storage/roombacmd

clean:
	rm *.o serialdaemon_arm
