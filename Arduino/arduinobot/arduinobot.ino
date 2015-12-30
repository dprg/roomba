#include <Messenger.h>
#include <Metro.h>
#include <EEPROM.h>

#include <Wire.h>

template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; } 
#define endl "\n"

// Robot Types
enum RobotType {
  roomba,
  mobot,
  tankbot,
  unset};
  
RobotType robotType;    // default to unset type

// EEPROM Storage
#define eeRobotType 0;    // save robot type at offset 0

// pin defs
int ledPin = 13;
boolean ledState = false;

// R/C input pins
int channel1 = 54;
int channel2 = 55;
int channel3 = 56;
int rcMotenPin = 11;    // motor enable pin from comparator
int rcMoten;      // motor enable from R/C. False if transmitter is off.
boolean rcEnable = true;    // when true, system polls RC pulse width & translates it to motor motion
boolean rcPrint = false;    // when true, print rc values

// PWM & direction motor output pins for mo'bot
int inhLpin = 4;                           // Left wheel motor
int inLa = 2;
int inLb = 3;
int inhRpin = 5;                           // right wheel motor
int inRa = 7;
int inRb = 8;
int bladeOnpin = 12;                           // Blade
struct MotorPins{
  int inaPin;
  int inbPin;
  int inhPin;
  int lastMotorPwm;
};
struct MotorPins lMotorPins, rMotorPins;
int robotStopped = 1;                // used to disable motor drivers after a while of being stopped

// Instantiate Messenger object with the default separator (the space character)
Messenger message = Messenger(); 
Messenger avrMessage = Messenger();  // message parser for avr responses

// Instantiate the metronome object for flashing the LED and polling R/C rcvr
Metro ledMetro = Metro(500); 
Metro rcMetro = Metro(100);  // poll R/C receiver every 100ms
Metro driveDisableMetro = Metro(5000);  // check every 5 seconds whether speed has been zero for a second

// Compass address and variables
// Shift the device's documented slave address (0x42) 1 bit right
// This compensates for how the TWI library only wants the
// 7 most significant bits (with the high bit padded with 0)
int compassAddress = 0x42>>1;
int heading = 0;  // variable to hold the heading angle
byte responseBytes[6];  // for holding the sensor response bytes

// Roomba emulation variables
#define MAX_ROOMBA_CMD_SIZE 64
byte roombaCmd[MAX_ROOMBA_CMD_SIZE];
int roombaCmdIndx;
boolean rodComplete;
byte responseBuf[MAX_ROOMBA_CMD_SIZE];

// Wheel encoder for Mo'bot
volatile unsigned long rWheelEncoderCount = 0;  // ACHTUNG: don't use this at base level: updated at interrupt level
unsigned long rEncoder;  // must call readOdometry() to update before using these values. Mo'bot only implements rEncoder
int rEncoderIntNum = 4;    // Interrupt number 4 is on digital I/O 19, Right wheel encoder

// Timing measurement
unsigned long startMillis, endMillis, elapsedMillis;

int i;
char c;

void setup()
{
  delay(500);  //Wait at least 500 milli-seconds for device initialization

  Serial.begin(115200);
  Serial2.begin(9600);    // set up connection to tankbot AVR
  Serial3.begin(115200);    // set up logging port
  Serial3.println("Serial3 logging starting");
  
  //robotType = EEPROM.read(eeRobotType);
  Serial3 << "Robot type: ";
  
  switch(robotType) {
    case roomba: Serial3.println("roomba"); break;
    case mobot: Serial3.println("mobot"); break;
    case tankbot: Serial3.println("tankbot"); break;
    case unset: Serial3.println("unset"); break;
    default: Serial3.println("Uninitialized EEPROM"); robotType = unset;
  }
  // set up the CLI handler
  message.attach(doCommand);    
  avrMessage.attach(processAvrOutput);
  roombaCmdIndx = 0;
  
  pinMode(ledPin, OUTPUT);      // Set the LED pin as output
  
  // set up motor control pins
  initMotorPins(inhLpin, inLa, inLb, &lMotorPins, 3);  // init timer 3 for left
  initMotorPins(inhRpin, inRa, inRb, &rMotorPins, 4);  // init timer 4 for right
  pinMode(bladeOnpin, OUTPUT);
  digitalWrite(bladeOnpin, LOW);
  
  // set up the R/C pins
  pinMode (channel1, INPUT);  // set A0 as input
  pinMode (channel2, INPUT);  // set A1 as input
  pinMode (channel3, INPUT);  // set A2 as input
  pinMode (rcMotenPin, INPUT);    // set rcMoten as input
  
  // start the I2C library
  Wire.begin();
  
  // Configure interrupts for wheel encoders
  rWheelEncoderCount = 0;
  attachInterrupt (rEncoderIntNum, countRencPulse, FALLING);
}
void loop()
{
  boolean oldRcMoten;
  
  // Flash the LED on pin 13 just to show that something is happening
  // Also serves as an indication that we're not "stuck" waiting for TWI data
  if (ledMetro.check()) {
    ledState = !ledState;
    if (ledState) digitalWrite(ledPin,HIGH);
    else digitalWrite(ledPin,LOW);
  }
  //Serial3 << "aftr led: " << _DEC(millis()) << endl;
  
  // Detect low to high transition on rcMoten pin & use it to enable rc drive
  oldRcMoten = rcMoten;
  rcMoten = digitalRead(rcMotenPin);
  if ((oldRcMoten == LOW) && (rcMoten == HIGH) && (robotType == mobot)) {
    rcEnable = true;
    Serial3 << "re-enabling R/C control" << endl;
  }
  
  // stop motors if no R/C signal or R/C disabled, indicated by rcMoten false  
  if (rcMoten == LOW) {
    // Serial.println("Stopped by mode");
    setMotorSpeed(0, &lMotorPins);
    setMotorSpeed(0, &rMotorPins);
    digitalWrite(bladeOnpin, LOW);    // turn blade off
    if (robotType == tankbot)
      Serial2.println("XXX");
  } 

  // if we've issued rc on command or flipped enable off then on, check & drive by rc
  if ((rcEnable) && (rcMoten == HIGH) && rcMetro.check()) {
    rcDrive();
  }
    
  // check for commands coming over serial
  while (Serial.available()) {
    if (rcEnable == true) {
      Serial3 << "Disabled R/C control due to computer command. Toggle Motor disable switch to re-enable R/C control" << endl;
      rcEnable = false;      // any command over serial channel disables R/C mode, which prevents hanging to read R/C. 
    }
    message.process(readSerialCmd());
  }
  
  // check for robot stopped periodically & process stopped timeout
  if (driveDisableMetro.check()) {
    checkMotorsStopped();
  }
}

// read characters from Chumby. Must be called with data available
char readSerialCmd ()
{
  int incomingByte = Serial.read();
  roombaCmd[roombaCmdIndx++] = (byte)incomingByte;
  if (roombaCmdIndx == MAX_ROOMBA_CMD_SIZE)  // prevent overflow
    roombaCmdIndx--;
  return incomingByte;
}

/**
  * This routine gets called everytime loop() detects a <CR> on the input serial stream
  */
void doCommand()
{
  byte i2cCommand;
  byte compassOffset;
  byte i2cData;
  int address;
  boolean bret;
  
  roombaCmdIndx = 0;     // set the raw buffer index back to 0
//Read compass
  if (message.checkString("c")) {              // print compass heading
    startMillis = millis();
    Serial3.print("c command: ");
    printHeading();
    endMillis = millis();
    elapsedMillis = endMillis - startMillis;
    Serial3 << " started: " << _DEC(startMillis) << " ended: " << _DEC(endMillis) << " elapsed " << elapsedMillis << endl;
//Print compass continuously
  } else if (message.checkString("p")) {      // print compass data continuously
    printCompass();
//Calibrate compass
  } else if (message.checkString("cal")) {      // calibrate compass
    calibrateCompass();
//Read compass data
  } else if(message.checkString("r")) {        // read a compass address
    compassOffset = (byte)message.readInt();    // get the address to read
    Serial.print("compass offset: ");
    Serial.print(compassOffset, DEC);
    Serial.print(" reads: 0x");
    i2cData = readCompassEeprom(compassOffset);  // read the compass memory
    Serial.println(i2cData, HEX);
//Write compass data
  } else if(message.checkString("w")) {        // write compass eeprom
    compassOffset = (byte)message.readInt();    // get the address to read
    Serial.print("compass offset: ");
    Serial.print(compassOffset, DEC);
    if (compassOffset == 0) {
      Serial.println("Error: compass offset was 0, you really don't want to change the I2C slave address. Did you enter a non-numeric value?");
      return;
    }
    Serial.print(" written with: ");
    i2cData = (byte)message.readInt();          // get the data to write & print it
    Serial.print(i2cData, DEC);
    i2cData = writeCompassEeprom(compassOffset, i2cData);  // write the compass memory and get back what it read back as
    Serial.print(" reads back as: ");
    Serial.println(i2cData, DEC);
//Encoder commands
  } else if(message.checkString("e")) {
    bret = readOdometry();
    if (bret)
      Serial << rEncoder << endl;
//R/C commands
  } else if(message.checkString("rc")) {
    if (message.checkString("on")) {
      rcEnable = true;
      ledMetro.interval(200);    // flash faster when we're on R/C control
    } else if(message.checkString("off")) {
      rcEnable = false;
      ledMetro.interval(500);
    } else if (message.checkString("status")) {
      if (rcEnable) Serial.println("on");
      else Serial.println("off");
    } else if(message.checkString("print")) {
      rcPrint = true;
    }  else if (message.checkString("noprint")) {
        rcPrint = false;
    }
//Send an arbitrary max 6 char cmd to the avr       
  } else if(message.checkString("avr")) {   
    char avrCmd[6];
    while (message.available()) {
      message.copyString(avrCmd, 6);
      Serial3.println(avrCmd);
      Serial2.print(avrCmd);
      Serial2.print(" ");
    }      
    Serial2.print("\n");
//Emulate Roomba command    
  } else if(message.checkString("m")) {        
    emulateRoomba();
// Robot type command
  } else if(message.checkString("robot")) {
    if (message.checkString("t")) {
      robotType = tankbot;
      Serial3.println("Tankbot");
    } else if(message.checkString("r")) {
      robotType = roomba;
      Serial3.println("Roomba");
    } else if (message.checkString("m")) {
      robotType = mobot;
      Serial3.println("Mobot");
    }
    //EEPROM.write(eeRobotType, robotType);
//Help    
  } else if(message.checkString("help")) {
    usage();
  } else if(message.checkString("x")) {
    address = message.readInt();
    Serial.println(address, HEX);
  } else {
    Serial.println("Invalid command in command interpreter");
    Serial3.println("Invalid command in command interpreter");
  }
}

void usage()
{
  Serial.println("help print this message");
  Serial.println("robot (r | m | t) Set robot type to roomba, mobot, or tankbot");
  Serial.println("e print wheel encoder");
  Serial.println("c print compass heading once");
  Serial.println("p Print compass heading continuously until key pressed");
  Serial.println("cal Calibrate compass");
  Serial.println("r {offset} Read EEPROM data at offset in compass");
  Serial.println("w {offset} {data} Write EEPROM data at offset in compass");
  Serial.println("rc {on | off | status | print | noprint} Enable or disable R/C drive, print to Serial");
  Serial.println("  report enable status, cause values to be printed or not");
  Serial.println("avr {avrString} Print the string to the avr port. e.g. avr RMT 20<cr> or avr ROD<cr> or avr XXX<cr>");
  Serial.println("m {roomba binary command} Emulate roomba");
}

/*
 * Compass routines
 */
void readSensor() 
{ 
  // step 1: instruct sensor to read compass 
  Wire.beginTransmission(compassAddress);  // transmit to device
                           // the address specified in the datasheet is 66 (0x42) 
                           // but i2c adressing uses the high 7 bits so it's 21 
  Wire.write('A');         // Send a "Post Heading Data" (0x50) command to the HMC6352  
  Wire.endTransmission();  // stop transmitting 
 
  // step 2: wait for readings to happen 
  delay(8);               // datasheet suggests at least 6 ms 
  
  // step 3: request reading from sensor 
  Wire.requestFrom(compassAddress, 2);  // request 2 bytes from slave device #21 
 
  // step 4: receive reading from sensor 
  if(2 <= Wire.available())     // if six bytes were received 
  { 
    heading = Wire.read();  // read high byte (overwrites previous reading) 
    heading = heading << 8;    // shift high byte to be high 8 bits 
    heading += Wire.read(); // read low byte as lower 8 bits 
    heading /= 10; 
  }  
} 

void readCompassData(int command, int readLength)
{
  Wire.beginTransmission(compassAddress);  // transmit to device
  Wire.write(command);         // Send a command  
  Wire.endTransmission();  // stop transmitting 
 
  delay(2);               // datasheet suggests at least 1 ms 
  
  Wire.requestFrom(compassAddress, readLength);  // request readLength bytes from slave device #33 
 
  // step 4: receive reading from sensor 
  if(readLength <= Wire.available())     // if six bytes were received 
  {
    for(int i = 0; i<readLength; i++) {
      responseBytes[i] = Wire.read();
    }
  }  
}
  
void printHeading()
{
  readSensor();    // read data from the HMC6343 sensor
  Serial.println(heading, DEC);
  Serial3.print(heading, DEC);
}

void calibrateCompass()
{
  Wire.beginTransmission(compassAddress);  // transmit to device
  Wire.write('C');         // Send the calibration command 
  Wire.endTransmission();  // stop transmitting 
  Serial.println("Entered calibration mode. Rotate compass twice in 10 sec smoothly then press any key");
  while (!Serial.available())  // loop till user presses a key
    ;
  Wire.beginTransmission(compassAddress);  // transmit to device
  Wire.write('E');         // Send the calibration command 
  Wire.endTransmission();  // stop transmitting 
}
  
void printCompass()
{
  while (!Serial.available())
  {
    readSensor();  // read data from the HMC6343 sensor
    // Note that heading, tilt and roll values are in tenths of a degree, for example
    // if the value of heading is 1234 would mean 123.4 degrees, that's why the result
    // is divided by 10 when printing.
    Serial.print("Heading: ");
    Serial.println(heading, DEC); 
    // print on Serial2 too
    //Serial2.print("Heading: ");
    //Serial2.println(heading, DEC); 
    delay(200);                   // wait for quarter second     
  }
  Serial.read();  // empty the buffer on our way out
}

// Read a byte from the compass EEPROM area at offset addr
byte readCompassEeprom(byte addr) 
{ 
  byte readData;
  // step 1: instruct compass to read EEPROM 
  Wire.beginTransmission(compassAddress);  // transmit to device
  Wire.write('r');         // Send a "Read eeprom/ram" command to the HMC6352
  Wire.write(addr);       // send the address to read
  Wire.endTransmission();  // stop transmitting 
 
  // step 2: wait for readings to happen 
  delay(1);               // datasheet suggests at least 70us 
  
  // step 3: request reading from sensor 
  Wire.requestFrom(compassAddress, 1);  // request 1 bytes from slave device
 
  // step 4: receive reading from sensor 
  if(1 <= Wire.available())     // if six bytes were received 
    readData = Wire.read();
  return readData;
} 

byte writeCompassEeprom(byte addr, byte writeData) 
{ 
  byte readData;
  // instruct compass to write EEPROM with supplied data
  Wire.beginTransmission(compassAddress);  // transmit to device
  Wire.write('w');         // Send a "Write eeprom/ram" (0xf1) command to the HMC6343  
  Wire.write(addr);       // send the address to read
  Wire.write(writeData);
  Wire.endTransmission();  // stop transmitting 
 
  // step 2: wait for write to happen 
  delay(1);               // datasheet suggests at least 70us

  // instruct compass to read back the address just written 
  Wire.beginTransmission(compassAddress);  // transmit to device
  Wire.write('r');         // Send a "Read eeprom/ram" (0xe1) command to the HMC6343  
  Wire.write(addr);       // send the address to read
  Wire.endTransmission();  // stop transmitting 
 
  // wait for readings to happen 
  delay(1);
  
  // Now read the byte we just wrote and return it to caller - allows sanity checking
  Wire.requestFrom(compassAddress, 1);  // request 1 bytes from slave device #33 
  if(1 <= Wire.available())     // if one bytes were received 
    readData = Wire.read();
  return readData;
} 

/*
 * Wheel Encoders logic
 */
// handle the Mo'bot wheel encoder interrupt and its counter
void countRencPulse()
{
  rWheelEncoderCount++;
  //Serial3.println("countRencPulse");
}

// Read Mo'bot encoder counter, or send the ROD command to AVR and wait with timeout for return. True return = valid data
boolean readOdometry()
{
  long timeout;
  
  if (robotType == mobot) {    // for mobot, read the counter run by the interrupt routine
    noInterrupts();    // make sure we don't get interrupted partway through reading the long
    rEncoder = rWheelEncoderCount;
    Serial3 << "rEncoder: " << _DEC(rEncoder) << " rWheelEncoderCount: " << _DEC(rWheelEncoderCount) << endl;
    interrupts(); 
    return true; 
    
  } else if (robotType == tankbot) {    // for tankbot, ask avr to report its odometry readings
    Serial2.flush();    // flush out the COK's
    Serial2.println("ROD");  // read odometry from AVR with timeout
    Serial3.println("Sent ROD command, waiting for response");
    timeout = millis() + 500; // give it 500ms to read
    rodComplete = false;
    while ((millis() < timeout) && !rodComplete) {
      while (Serial2.available() > 0) {
        avrMessage.process(readAvr());   
      }
    }
    if (rodComplete)
      return true;
    else
      return false;        
  } else {
    Serial3 << "Request for odometry when robot type not set" << endl;
    return false;
  }
}

/*
 * R/C and motor routines
 */
void 
setMotorSpeed(int speed, struct MotorPins *mp)
{
    int pwmValue;
    // input speed request should be -255 to +255
    // output pwm is 0 - 255 (128 is stopped)
    pwmValue = (speed + 256)/2;
    if (pwmValue > 255) pwmValue = 255;
    else if (pwmValue < 0) pwmValue = 0;
    
    // set direction
    if (pwmValue != 128)
      digitalWrite(mp->inhPin, HIGH);    // enable motor drivers
    analogWrite(mp->inaPin, pwmValue);  
    analogWrite(mp->inbPin, pwmValue); 
    // record the last motor speed requested, so we can time stop requests & inhibit motor drivers
    mp->lastMotorPwm = pwmValue;  
    //if (rcPrint)
      //Serial.print(pwmValue, DEC);  
}

void 
checkMotorsStopped()
{
  if ((lMotorPins.lastMotorPwm == 128) && (rMotorPins.lastMotorPwm == 128)) {
    robotStopped++;
    if (robotStopped == 2) {  // if stopped for 2 periods, disable motor drivers
      robotStopped = 1;
      digitalWrite(lMotorPins.inhPin, LOW);
      digitalWrite(rMotorPins.inhPin, LOW);
    }
  }
}

void 
initMotorPins(int inhpin, int ina, int inb, struct MotorPins *mp, int timerId)
{
  pinMode(inhpin, OUTPUT);
  digitalWrite(inhpin, LOW);
  pinMode(ina, OUTPUT);
  pinMode(inb, OUTPUT);
  analogWrite(ina, 128);
  analogWrite(inb, 128);
  if (timerId == 3)
    TCCR3A |= 0x4;    // Set B & C to be complementary outputs for driving H-bridge in LAP mode
  if (timerId == 4)
    TCCR4A |= 0x4;    // Set B & C to be complementary outputs for driving H-bridge in LAP mode
  mp->inaPin = ina;
  mp->inbPin = inb;
  mp->inhPin = inhpin;
}

// Drive under R/C control
void 
rcDrive()
{
  unsigned long rcSpeedRqst;
  unsigned long rcDirection;
  unsigned long rcMode;
  int lSpeed, rSpeed;

  // Drive blade if mode pushed far enough
  rcMode = pulseIn(channel3, HIGH);
  if (rcMode < 1500) {
    digitalWrite(bladeOnpin, HIGH);
  } else {
    digitalWrite(bladeOnpin, LOW);
  }
  // get pulse width in us
  rcSpeedRqst = pulseIn(channel2, HIGH);
  rcDirection = pulseIn(channel1, HIGH);
  
  digitalWrite(ledPin, HIGH);
  
  if (rcPrint) {
    Serial.print("Speed Request: ");
    Serial.print(rcSpeedRqst);
    Serial.print(" Direction: ");
    Serial.print(rcDirection);
  }
  
  // calculate speed based on rc reading, cap at +/- 254
  lSpeed = ((1500 - (int)rcSpeedRqst - (1500 - (int)rcDirection))*7)/10; // normalize rc Speed to 0, & subtract turn
  if (lSpeed > 252) lSpeed = 252;
  if (lSpeed < -252) lSpeed = -252;
  rSpeed = ((1500 - (int)rcSpeedRqst - ((int)rcDirection - 1500))*7)/10; // xply by .7 to to bring range to +/-255
  if (rSpeed > 252) rSpeed = 252;
  if (rSpeed < -252) rSpeed = -252;
  
  // prevent Tankbot's tracks from running in opposite directions, or they fall off. 
  // if either track wants to run forward, prevent the other from going backward
  if (robotType == tankbot) {
    if (rSpeed > 0 && lSpeed < 0) {if ((rSpeed + lSpeed) > 0) lSpeed = 0; else rSpeed = 0;}
    if (lSpeed > 0 && rSpeed < 0) {if ((rSpeed + lSpeed) > 0) rSpeed = 0; else lSpeed = 0;} 
  }

  // provide deadband around stopped
  if (abs(lSpeed) < 15) { 
    lSpeed = 0;
    setMotorSpeed(0, &lMotorPins);
    Serial2.println("LMT 0");
  }

  if (abs(rSpeed) < 15) {
    rSpeed = 0;
    setMotorSpeed(0, &rMotorPins);
    Serial2.println("RMT 0");
  }

  if (rcPrint) {
    Serial.print(" lSpeed: ");
    Serial.print(lSpeed);
    Serial.print(" rSpeed: ");
    Serial.print(rSpeed);
    Serial.print(" rcMode: ");
    Serial.print(rcMode, DEC);
  }
  
  // Set motor speeds & directions
  if ((lSpeed > 0) || (lSpeed < 0)) {
    if (rcPrint)
      Serial.print(" lMotor: ");
    setMotorSpeed(lSpeed, &lMotorPins);
    Serial2.print("LMT ");
    Serial2.println(lSpeed/2, DEC);
  }
  if ((rSpeed > 0) || (rSpeed < 0)) {
    if (rcPrint)
      Serial.print(" rMotor: ");
    setMotorSpeed(rSpeed, &rMotorPins);
    Serial2.print("RMT ");
    Serial2.println(rSpeed/2, DEC);
  }
  if (rcPrint)
    Serial.println("");
}

// drive under program control - pretend we're a roomba and respond to drive command
void drive()
{
  int velocity, radius;
  int rSpeed, lSpeed;
  int differential;
  byte *rcmd = &roombaCmd[2]; // command is in buffer at offset 2 (m<space> preceeds it). Point rcmd to it

  Serial3 << "drive bytes " << _HEX(rcmd[1]) << " " << _HEX(rcmd[2]) << " " << _HEX(rcmd[3]) << " "
      << _HEX(rcmd[4]) << endl;

  // calculate velocity
  velocity = ((rcmd[1]<<8) + rcmd[2])/2;  // roombaspeed is +/- 500, reduce range to +/-250
  lSpeed = rSpeed = velocity;  // go straight at velocity by default
  
  // calculate differential motor speed if we've been asked to turn
  radius = ((rcmd[3]<<8) + rcmd[4]);  // turn radius +/-2000 into +/-100 
  if (radius != 0 && radius != -32768 && radius != 0x7fff) {
    differential = (2000 - abs(radius))/50;    // roomba direction is +/- 2000mm, scale to +/-200
    if (differential < 0) differential == 0;
    if (radius > 0) {   // turn left
      lSpeed -= differential;
      rSpeed += differential;
    } else {            // turn right
      lSpeed += differential;
      rSpeed -= differential;
    }
  }
  
  // limit speed to +/-254
  if (lSpeed > 254) lSpeed = 254; else if (lSpeed < -254) lSpeed = -254;
  if (rSpeed > 254) rSpeed = 254; else if (rSpeed < -254) rSpeed = -254;
  
  // prevent Tankbot's tracks from running in opposite directions, or they fall off. 
  // if either track wants to run forward, prevent the other from going backward
  if (robotType == tankbot) {
    if (rSpeed > 0 && lSpeed < 0) {if ((rSpeed + lSpeed) > 0) lSpeed = 0; else rSpeed = 0;}
    if (lSpeed > 0 && rSpeed < 0) {if ((rSpeed + lSpeed) > 0) rSpeed = 0; else lSpeed = 0;} 
  }
  if (velocity = 0) lSpeed = rSpeed = 0;  
  
  Serial3 << "Drive command from roombacomm, speed: " << velocity << " radius: " << radius << " lSpeed: "
        << lSpeed << " rSpeed: " << rSpeed << " differential: " << differential << endl;
  
  // Set motor speeds & directions
    setMotorSpeed(lSpeed, &lMotorPins);
    Serial2.print("LMT ");    // send motor command to avr if tankbot
    Serial2.println(lSpeed/2, DEC);
  //}
    setMotorSpeed(rSpeed, &rMotorPins);
    Serial2.print("RMT ");
    Serial2.println(rSpeed/2, DEC);
  //}
  
}

void emulateRoomba()
{
  byte *rcmd = &roombaCmd[2]; // command is in buffer at offset 2 (m<space> preceeds it). Point rcmd to it
  Serial3 << "roomba command: " << _HEX(rcmd[0]) << " " << _HEX(rcmd[1]) << " " << _HEX(rcmd[2]) << " " << _HEX(rcmd[3])
     << " " << _HEX(rcmd[4]) << " " << _HEX(rcmd[5]) << " " << _HEX(rcmd[6]) << " " << _HEX(rcmd[7]) << endl;
  switch(rcmd[0]) {  
    case 137:  // drive command
      drive();
      break;
    case 142:    // query group
      Serial3.print("Recieved query command for group: ");
      Serial3.println(rcmd[1], DEC);
      if (rcmd[1] == 0) {
        for (int i=0; i<26; i++)
          Serial.write((byte)0);
      }
      break;
    // cases to ignore
    case 149:    // query list
      Serial3.print("Received query command for list of ");
      Serial3.print(rcmd[1], DEC);
      Serial3.println(" sensors: ");
      readOdometry();
      Serial.write(responseBuf, 4);  // write encoder values back to chumby
      break;
    case 128:
    case 129:
    case 130:
    case 139:
    case 140:
    case 141:
      Serial3.print("Ignored command to Roomba: ");
      Serial3.print(rcmd[0], DEC); 
      Serial3.println();
      break;
    default:
      Serial3.print("Unrecognized command to Roomba: ");
      Serial3.print(rcmd[0], DEC); 
      Serial3.println();
  }
}

void processAvrOutput ()
{

  Serial3.println("\nprocessing message");
  if (avrMessage.checkString("ODO")) {
    rEncoder = avrMessage.readLong();
    rEncoder = avrMessage.readLong();  
    Serial3 << " " << rEncoder << " (" << " " << _HEX(rEncoder) << ")" << endl;
    
    // write the data to serial port
    // note this is hacked to only use the right encoder (why use 2 when you can use 1?)
    responseBuf[0] = (byte)((rEncoder>>8) & 0xff);
    responseBuf[1] = (byte)((rEncoder) & 0xff);
    responseBuf[2] = (byte)((rEncoder>>8) & 0xff);
    responseBuf[3] = (byte)((rEncoder) & 0xff);
    rodComplete = true;    // flag read odometry complete
  }
}

// read characters from AVR and transform \n into \r. Must be called with data available
char readAvr ()
{
  int incomingByte = Serial2.read();
  Serial3.print((char)incomingByte);
  if ((char)incomingByte == '\n')
    incomingByte = '\r';
  return incomingByte;
}


