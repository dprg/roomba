//                                                                              
// roombatune --
//                                                                              
// 2006, Tod E. Kurt, tod@todbot.com,  http://roombahacking.com/                
//                                                                              

import mkv.MyGUI.*;
import roombacomm.*;

// if you set roombaCommPort to null, it will bring up the port picker dialog
// if you set it to "testdummy", it will be a bring up a non-functioning test of the GUI                                                                       
//String roombacommPort = "testdummy";                                          
String roombacommPort = "/dev/cu.KeySerial1";
//String roombacommPort = "/dev/cu.BlueRadios-COM0-1";
//String roombacommPort = null;

RoombaCommSerial roombacomm;
int octave = 6;  // which octave we're on when playing notes

void setup() {
  roombacommSetup();
}

void draw() {
  if( roombacomm == null ) return;
}

void keyPressed() {

  int note=-1; // -1 means no note yet
  switch( key ) {
  // pseudo-keyboard to play notes on
  case 'a': note = 0; break;
  case 'w': note = 1; break;
  case 's': note = 2; break;
  case 'e': note = 3; break;
  case 'd': note = 4; break;
  case 'f': note = 5; break;
  case 't': note = 6; break;
  case 'g': note = 7; break;
  case 'y': note = 8; break;
  case 'h': note = 9; break;
  case 'u': note =10; break;
  case 'j': note =11; break;
  case 'k': note =12; break;
  case 'o': note =13; break;
  case 'l': note =14; break;
  case 'z': octave--; break;  // change octaves
  case 'x': octave++; break;  // change octaves
  }
  // we actually hit a note key                                                 
  if( note >= 0 ) {
    octave = (octave < 2) ? 2 : (octave > 9) ? 9 : octave;
    note = note + ((octave+1)*12);
    if( roombacomm != null ) 
      roombacomm.playNote( note, 10);
    println("playing note: "+note+" (octave:"+octave+")");
  }

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
                              "RoombaTune",
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
  });
}
