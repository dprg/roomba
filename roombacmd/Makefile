
NAME = roombacmd
VERSION = 1.1
DISTNAME = $(NAME)-$(VERSION)


CFLAGS = -Wall -I.
CC = mipsel-linux-gcc

BINARIES = roombacmd simpletest
#BINARIES = simpletest

all: $(BINARIES)

clean:
	rm -f $(BINARIES)
	rm -f $(DISTNAME).tar.gz

.c.o:
	$(CC) $(CFLAGS) -o $@ $@.c 

simpletest: simpletest.c roombalib.c
	$(CC) $(CFLAGS) $^ -o $@ 

roombacmd: roombacmd.c roombalib.c
	$(CC) $(CFLAGS) $^ -o $@ 


dist:
	echo "Building dist $(DISTNAME).tar.gz"
	tar -C .. --exclude=*.tar.gz --exclude=*.svn --exclude=*~ \
        -cvzf $(DISTNAME).tar.gz $(NAME)

help:
	@echo "This Makefile has the following targets:"
	@echo "make all ......... build the roombalib library and the test programs"
	@echo "make roombacmd ... build just the roombacmd cmdline tool"
	@echo "make dist ........ build the roombalib tarball"
	@echo "make clean ....... clean up directory for packaging"
