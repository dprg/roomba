Quick Start for Windows
-----------------------
This procedure has been tested on Windows XP and Vista.

Prerequisites
-------------
- You must have java installed, and the path to java must be in your environment.
To test, open a command prompt window and type "java". You should get a help menu 
printed. 

Roombacomm Installation
-----------------------
- unzip roombacomm somewhere

rxtxSerial.dll Installation
---------------------------
- You need to copy the rxtxSerial.dll file into the folder containing java.exe
- Identify your Java Runtime Environment's folder. For version 1.6.0, this usually 
is c:\Program Files\Java\jre1.6.0_??\. java.exe is usually in the bin folder under the
runtime folder.
- Note that the java.exe program can live anywhere (even in \Windows\System32!), and rxtxSerial.dll
needs to be copied to the same directory as the java.exe version that will be run from the
command prompt. If you have several versions of Java installed, you need to find the right
directory containing the version of Java which will be run when you type java from the command
prompt. One way to do this is open up a command prompt window and type java -version. Then
look in suitable directories for that version. The version of java.exe which runs is determined
by the order of directories in the PATH environment variable. Test that you found the right version of java
by typing \<path>\java -version and looking for a match to the version when you don't explicitly
type the path. For example:
  > \windows\system32\java.exe -version
will print the version of java in the windows system32 folder.
- Copy rxtxSerial.dll from roombacomm\rxtxlib folder to the folder with java.exe. For
example: c:\Program Files\Java\jre1.6.0_01\bin\
- Further help is available on the Wiki at http://www.rxtx.org
- If you don't get rxtxSerial.dll installed in the right place, RoombacommTest will hang when you
press the connect button, command-line apps will hang at startup, and the Windows bluetooth device 
icon will flash to connected then back to not connected.

Bluetooth
---------
- It may be that bluetooth is problematic when you've been using the device from one computer,
which is still turned on, and you try to use it from another computer. You may have to turn the
other computer off, and/or delete the bluetooth device and re-add it in the Bluetooth devices
window before it will allow opening it. 
- Open up the bluetooth Devices window (in the control panel). You can monitor the connected state
there, as well as identify which COM ports are assigned to the bluetooth 1SPP board. The device
should show connected once you've hit the connect button on RoombacommTest, or when any of the
sample programs are active.
- Bluetooth on Windows may not work too well if you select no passcode. Try creating the device
with a passcode of 0000, even if the bluetooth device is set up for no passcode.
- A Philips BGB203 chip in automatic server mode as a bluetooth transceiver, and 
Windows XP and Vista, and RXTXlib does not seem reliable - YMMV! Please help if you can.

Identifying which COM port Roomba is connected to
-------------------------------------------------
- Find which COM port your Roomba is connected to.  
- If you've hard-wired Roomba to a PC serial port, this should be in your computer's documentation. 
- If you're using a USB to serial adapter, you can find the COM port by going to device manager 
(right click on My Computer, click on Manage, and click on Device Manger - expand the 
"Ports (COM & LPT)" item) and looking for a COM port that comes and goes as you plug and unplug
the USB to serial adapter.
- If you're using bluetooth with the Windows bluetooth stack (default), go to control panel 
and open up "bluetooth Devices" and look on the COM ports tab
- The SimpleTest program helps you by listing which COM ports it found. You can try them all.

Testing with SimpleTest
-----------------------
- Before you jump to running SimpleTest (which actually relies on a lot of SW underneath it)
check that you can actually connect to the COM port you intend to use. Run a terminal emulator
program such as PuTTY, and open up the serial port with the name COMx (whichever port your Roomba
is connected to). Jumper the Tx port to the Rx port. If your USB to Serial adapter has data LEDs, 
try hitting keys, and make sure the LEDs blink, and the key you pressed is echoed back. Similarly
for bluetooth. If you can't get a terminal emulator to talk to your interface, SimpleTest has
no hope.
- SimpleTest is a java application which connects to Roomba through a serial port you specify
and sends commands to make it do a few basic things.
- Open a command prompt window and cd to the directory where you unzipped roombacomm
- run the runit.bat file with the name of your com port, e.g.
  > runit COM5. 
  Note that COMx is case sensitive - "COM" in Windows COM ports is capitalized
- If you get the dreaded "Couldn't connect to com4" message, you likely either mistyped
the COM port (e.g. didn't capitalize COM), or you have not installed rxtxSerial.dll in the
same directory as the java.exe that you're running.
- If it connects to the COM port, and you have lights on your USB to serial port, they should
start flashing as the program prints the commands it's sending to Roomba. If you have Roomba
connected and powered on, it will move around a little.
- If you're using bluetooth on Windows, you may have to give the -hwhandshake option on
the command line. e.g. 
  > runit roombacomm.DriveRealTime COM4 -hwhandshake

Modifying & Rebuilding
----------------------
- The "Hacking Roomba" book describes the makeit.sh script, which builds all the java code.
For windows, use the makeit.bat batch file from the command prompt to rebuild all java code
after modifications.
- For windows, the analog to the makedoc.sh script is the makedoc.bat batch file. This rebuilds
the javadoc. Please keep the javadoc up to date if you're going to submit changes back to Tod.

Processing
----------
- If you change any RoombaComm functions, you'll need to re-make the roombacomm jar file. Use
the build-jar.bat batch file for this, then run the build-processing-library.bat batch file.

