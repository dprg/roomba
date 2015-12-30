//                                                                              
// roombaring --
//   
// 2006, Tod E. Kurt, tod@todbot.com,  http://roombahacking.com/                
//                                                                              

import roombacomm.*;

String rtttl = "tron:d=4,o=5,b=100:8f6,8c6,8g,e,8p,8f6,8c6,8g,8f6,8c6,8g,e,8p,8f6,8c6,8g,e.,2d";

String roombacommPort = "/dev/cu.KeySerial1";
//String roombacommPort = "/dev/cu.BlueRadios-COM0-1";

RoombaCommSerial roombacomm = new RoombaCommSerial();
if( ! roombacomm.connect( roombacommPort ) ) {
  println("couldn't connect. goodbye.");  System.exit(1);
}
println("Roomba startup");
roombacomm.startup();
roombacomm.control();
roombacomm.pause(50);

ArrayList notelist = RTTTLParser.parse( rtttl );
int songsize = notelist.size();
// if within the size of a roomba song,  make the song, then play      
if( songsize <= 16 ) {
  println("creating a song with createSong()");
  int notearray[] = new int[songsize*2];
  int j=0;
  for( int i=0; i< songsize; i++ ) {
    Note note = (Note) notelist.get(i);
    int sec64ths = note.duration * 64/1000;
    notearray[j++] = note.notenum;
    notearray[j++] = sec64ths;
  }
  roombacomm.createSong( 1, notearray );
  roombacomm.playSong( 1 );
} 
// otherwise, try to play it in realtime                                
else {
  println("playing song in realtime with playNote()");
  int fudge = 20;
  for( int i=0; i< songsize; i++ ) {
    Note note = (Note) notelist.get(i);
    int duration = note.duration;
    int sec64ths = duration*64/1000;
    if( sec64ths < 5 ) sec64ths = 5;
    if( note.notenum != 0 )
      roombacomm.playNote( note.notenum, sec64ths );
    roombacomm.pause( duration + fudge );
  }
}
System.out.println("Disconnecting");
roombacomm.disconnect();
