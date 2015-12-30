//
// roombaview -- 
//
// 2006, Tod E. Kurt, tod@todbot.com,  http://roombahacking.com/
//

import mkv.MyGUI.*;
import roombacomm.*;

// if you set roombaCommPort to null, it will bring up the port picker dialog
// if you set it to "testdummy", it will be a bring up a non-functioning test of the GUI
String roombacommPort = "testdummy";
//String roombacommPort = "/dev/cu.KeySerial1";
//String roombacommPort = "/dev/cu.BlueRadios-COM0-1";
//String roombacommPort = null;

RoombaCommSerial roombacomm;

MyGUI gui;
MyGUIButton butForward, butBackward, butStop;
MyGUIButton butSpinLeft, butSpinRight, butTurnLeft, butTurnRight;
MyGUIButton butReset, butSafe, butFull, butSpy, butVacuum, butBeep;
MyGUIPinSlider sliderSpeed;
PFont fontA;
// all the colors used
color cBack = color(150);
color cOff  = color(100);
color cWhl  = color(80);
color cOn   = color(255, 0, 0);
color cTxt  = color(40);
color cGrid = color(120);
color cFlor = color(190);
color cStat = color(170);
color cBut = color(170,170,210);

float rx,ry,rangle;  // location of displayed roomba icon                  
float scalex = 0.4;  // convert mm to pixels
float scaley = 0.4;  // convert mm to pixels

int frameCounter=0;

// called once on startup
void setup() {
  size(600, 500);
  frameRate(5);
  smooth();
  background(cFlor);
  stroke(100);
  ellipseMode(CENTER_RADIUS);
  rectMode(CENTER);
  // Initiate GUI objects
  fontA = loadFont("CourierNewPSMT-9.vlw");
  textFont(fontA, 12);
  fill(0);
  gui = new MyGUI(this);
  makeMoveButtons(width-75,height-100);
  makeControlButtons(width-75,height-225);
  
  roombacommSetup();
  reset();
}

void reset() {
  rx = width/2;
  ry = height/2;
  rangle = 0;
}
  
// called 'framerate' times a second
void draw() {
  frameCounter++;
  background(cFlor);
  fill(0);
  stroke(100);
  
  drawGridlines();
  drawStatus();
  if( roombacomm != null ) {
      computeRoombaLocation();
      drawRoombaStatus( rx, ry, rangle );
      updateRoombaState();
  }
}
 
//                                                                  
// Get roomba sensors and update display                                        
//                                                                              
void updateRoombaState() {
    roombacomm.sensors();
}

// 
void computeRoombaLocation() {
  int distance = roombacomm.distance();
  float angle  = roombacomm.angleInRadians();
  rangle = rangle - angle ;
  rangle %= TWO_PI;
  float dx = distance * sin(rangle);
  float dy = distance * cos(rangle);
  rx = rx + (dx * scaley);
  ry = ry - (dy * scalex);
  
  // torroidial mapping, sir
  if( rx > width  ) rx = 0;
  if( rx < 0 ) rx = width;
  if( ry > height ) ry = 0;
  if( ry < 0 ) ry = height;
}  

//
// 
void actionPerformed(ActionEvent e) {
  println("e="+e);
  if( !roombacomm.connected() ) return;
  String cmd = e.getActionCommand();
  Object src = e.getSource();
  
  if( cmd.equals("speed-update") ) {
    roombacomm.setSpeed( sliderSpeed.getValue() ); 
    return;
  }
  else if( src == butStop ) {
    roombacomm.stop();
  } else if( src == butForward ) {
    roombacomm.goForward();
  } else if( src == butBackward ) {
    roombacomm.goBackward();
  } else if( src == butSpinLeft ) {
    roombacomm.spinLeft();
  } else if( src == butSpinRight ) {
    roombacomm.spinRight();
  } else if( src == butTurnLeft ) {
    roombacomm.turnLeft();
  } else if( src == butTurnRight ) {
    roombacomm.turnRight();
  }  else if( src == butReset ) {
    roombacomm.reset();
    reset();
  }  else if( src == butSafe ) {
    roombacomm.start(); 
    roombacomm.control();
  }  else if( src == butFull ) {
    roombacomm.start(); 
    roombacomm.control();
    roombacomm.full();
  } else if( src == butSpy ) {
    roombacomm.start();
    roombacomm.clean();  // clean mode
  } else if( src == butBeep ) {
    int song[] = { 69,8, 72,18, 76,8};
    roombacomm.createSong(1, song );
    roombacomm.playSong(1);
  }
}

//
// called on key press
//
void keyPressed() {
  if( key == CODED ) {
    if( keyCode == UP )
      roombacomm.goForward();
    else if( keyCode == DOWN )
      roombacomm.goBackward();
    else if( keyCode == LEFT )
      roombacomm.spinLeft();
    else if( keyCode == RIGHT )
      roombacomm.spinRight();
  }
  else if( keyCode == RETURN || keyCode == ENTER ) {
    println("resetting");
    roombacomm.reset();
    reset();
  }
  else if( key == ' ' ) {
    roombacomm.stop();
  }
  else if( key == '.' || key == '>' ) {
    sliderSpeed.setValue( sliderSpeed.getValue() + 25 );
    roombacomm.setSpeed( sliderSpeed.getValue() ); 
  }
  else if( key == '.' || key == '>' ) {
    sliderSpeed.setValue( sliderSpeed.getValue() - 25 );
    roombacomm.setSpeed( sliderSpeed.getValue() ); 
  }
}

//
// drawRoombaStatus -- called during every draw()
// draws a little virtual roomba with in-built sensor icons
//
void drawRoombaStatus(float posx, float posy, float angle) {
  pushMatrix();
  translate( (int)posx,(int)posy);
  rotate(angle);
  smooth();
  textFont(fontA, 10);
  textAlign(CENTER);
  
  fill(cBack);
  strokeWeight(2);
  stroke(100);
  ellipseMode(CENTER_RADIUS);
  //rectMode(CENTER);
  ellipse(0,0, 50,50);

  fill(0);
  stroke(cTxt);
  strokeWeight(4);
  text("cliff", 0, -30);
  if( roombacomm.cliffLeft()       ) stroke(cOn);
  else stroke(cOff);
  line(-35,-25, -40,-15);
  if( roombacomm.cliffRight()      ) stroke(cOn);
  else stroke(cOff);
  line( 35,-25,  40,-15);

  if( roombacomm.cliffFrontLeft()  ) stroke(cOn);
  else stroke(cOff);
  line(-30,-30, -20,-30);
  if( roombacomm.cliffFrontRight() ) stroke(cOn);
  else stroke(cOff);
  line( 30,-30,  20,-30);
  
  stroke(cTxt);
  strokeWeight(7);
  text("bump", 0,-17);
  if( roombacomm.bumpLeft()  ) stroke(cOn);
  else stroke(cOff);
  line(-25,-20, -20,-20);
  if( roombacomm.bumpRight() ) stroke(cOn);
  else stroke(cOff);
  line( 25,-20,  20,-20);

  stroke(cTxt);
  text("wheeldrop", 0,3);
  if( roombacomm.wheelDropLeft()  ) stroke(cOn);
  else stroke(cWhl);
  line(-35,8, -20, 8);
  if( roombacomm.wheelDropRight() ) stroke(cOn);
  else stroke(cWhl);
  line( 35,8,  20, 8);
  if( roombacomm.wheelDropCenter()) stroke(cOn);
  else stroke(cWhl);
  line(-2,-8,   2,-8);
  
  stroke(cTxt);
  strokeWeight(5);
  text("over",0,18);
  if( roombacomm.motorOvercurrentDriveLeft()  ) stroke(cOn);
  else stroke(cOff);
  line(-30,16, -20, 16);
  if( roombacomm.motorOvercurrentDriveRight() ) stroke(cOn);
  else stroke(cOff);
  line( 30,16,  20, 16);
  
  stroke(cTxt);
  strokeWeight(7);
  text("dirt",0,42);
  if( roombacomm.dirtLeft()>0 ) stroke(40,40,roombacomm.dirtLeft());
  else stroke(cOff);
  line(-22,40,-18,40);
  if( roombacomm.dirtRight()>0 ) stroke(40,40,roombacomm.dirtRight());
  else stroke(cOff);
  line( 22,40, 18,40);
  
  popMatrix();
}

// called during each draw()
void drawStatus() {
  fill(cStat);
  rectMode(CENTER);
  rect( width-75,height-125, 150,250 );

  rectMode(CORNER);
  rect(0,0, width,50); 
  fill(0);
  textAlign(LEFT);
  textFont(fontA,14);
  String status = "unknown";
  int batt_percent=0, batt_charge=0, batt_volts=0, batt_mA=0, batt_cap=0;
  if( roombacomm == null ) {
    status = "not connected. no roomba. please restart.";
  } else if( ! roombacomm.connected() ) {
    status = "not connected. no roomba. please restart.";
  } else if( ! roombacomm.sensorsValid() ) {
     status = "connected. sensors invalid. unplugged?";
  } else if( roombacomm.safetyFault() ) {
     status = "connected. safety fault. reposition roomba and reset.";
  } else {
    status = "connected. roomba detected. ok.";
  }
  if( roombacomm != null ) {
    batt_mA     = roombacomm.current();
    batt_volts  = roombacomm.voltage();
    batt_charge = roombacomm.charge();
    batt_cap    = roombacomm.capacity();
    batt_percent = (batt_cap == 0) ? 0 : (batt_charge*100)/batt_cap;
  }
  text(" status: "+status, 8,12);
  text("   mode: "+((roombacomm==null)?"no roomba":roombacomm.modeAsString()), 8,24);
  text("battery: "+batt_percent+"% ("+batt_charge+"/"+batt_cap+" mAh)  voltage: "
                  +batt_volts+" mV @ "+batt_mA+" mA", 8,36);               
}

// called during each draw()
void drawGridlines() {
  int gridspacing = 50;
  stroke(cGrid);
  strokeWeight(1);
  for( int i=0; i< 15; i++ ) {
    line(0, i*gridspacing, width, i*gridspacing);
    line(i*gridspacing,0, i*gridspacing, height);
  }
}

//
void makeMoveButtons(int posx, int posy) {
  MyGUIGroup buttonGroup = new MyGUIGroup(this, posx, posy);
  buttonGroup.setStyle( new MyGUIStyle(this, cBut) );
  PImage forward   = loadImage("but_forward.png");
  PImage backward  = loadImage("but_backward.png");
  PImage spinleft  = loadImage("but_spinleft.png");
  PImage spinright = loadImage("but_spinright.png");
  PImage stopit    = loadImage("but_stop.png");
  PImage turnFL    = loadImage("but_turnleft.png");
  PImage turnFR    = loadImage("but_turnright.png");
  butForward   = new MyGUIButton(this,  0,-45, forward);
  butBackward  = new MyGUIButton(this,  0, 45, backward);
  butSpinLeft  = new MyGUIButton(this,-45,  0, spinleft);
  butSpinRight = new MyGUIButton(this, 45,  0, spinright);
  butStop      = new MyGUIButton(this,  0,  0, stopit);
  butTurnLeft  = new MyGUIButton(this,-45,-45, turnFL);
  butTurnRight = new MyGUIButton(this, 45,-45, turnFR);
  sliderSpeed = new MyGUIPinSlider(this, 0,80, 100,20, 0,500);
  sliderSpeed.setValue(200);
  sliderSpeed.setActionCommand("speed-update");
  
  gui.add(buttonGroup);
  //buttonGroup.add( label );
  buttonGroup.add( butForward );
  buttonGroup.add( butBackward );
  buttonGroup.add( butSpinLeft );
  buttonGroup.add( butSpinRight);
  buttonGroup.add( butStop );
  buttonGroup.add( butTurnLeft );
  buttonGroup.add( butTurnRight );
  buttonGroup.add( sliderSpeed );
}

//
void makeControlButtons(int posx, int posy) {
  MyGUIGroup buttonGroup = new MyGUIGroup(this, posx,posy);
  buttonGroup.setStyle( new MyGUIStyle(this, cBut) );
  butReset    = new MyGUIButton(this, -40, 25, "reset", 35,20 );
  butSafe     = new MyGUIButton(this,   0, 25, "safe", 35,20 );
  butFull     = new MyGUIButton(this,  40, 25, "full", 35,20 );
  butSpy      = new MyGUIButton(this, -40,  0, "SPY", 35,20 );  
  butBeep     = new MyGUIButton(this,  40,  0, "beep", 35,20 );  
//  butVacuum   = new MyGUIButton(this,  40, 25, "vacuum", 35,20 );
  
  gui.add( buttonGroup );
  buttonGroup.add( butReset );
  buttonGroup.add( butSafe );
  buttonGroup.add( butFull );
  buttonGroup.add( butSpy );
  buttonGroup.add( butBeep );
  //buttonGroup.add( butVacuum );
}

//
void roombacommPortSelected()
{
  roombacomm = new RoombaCommSerial();
  if( roombacommPort.equals("testdummy") ) return;  // so we can test UI w/o Roomba
  println("opening roomba serial port '" +roombacommPort+"'");
  if( ! roombacomm.connect( roombacommPort ) ) {
    println("oh noes");
  }
  println("Roomba startup");
  roombacomm.startup();
  roombacomm.control();
  roombacomm.pause(50);
  println("Playing some notes");
  roombacomm.playNote( 72, 8 );
  roombacomm.pause( 200 );
  roombacomm.playNote( 72, 10 );
  roombacomm.pause( 200 );
}

//
// On startup, popup a dialog to choose roomba port
//
void roombacommSetup()
{
  if( roombacommPort != null ) {
    roombacommPortSelected();
    return;
  }

  RoombaComm rc = new RoombaCommSerial();     // throwaway just to find ports   
  final String portlist[] = rc.listPorts();   // FIXME: should be made static   
  javax.swing.SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      try {
        String port = (String) javax.swing.JOptionPane.showInputDialog( null,
                              "Select Roomba Port",
                              "RoombaView",
                              javax.swing.JOptionPane.QUESTION_MESSAGE,
                              null, portlist, null);
        if( port == null )  {
          javax.swing.JOptionPane.showMessageDialog(null, "No port chosen, goodbye");
          System.exit(1);
        }
        roombacommPort = port;
        roombacommPortSelected();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  } );

}
