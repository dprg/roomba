
package roombacomm.net;

import java.lang.reflect.*;
import roombacomm.*;


public class DumpMethods {

    RoombaCommSerial rcs;

    public static void main(String[] args) {
        new DumpMethods(args);
    }

    public DumpMethods(String[] args) {
        rcs = new RoombaCommSerial();
        
        if( args.length == 0 ) {
            dumpMethods(rcs);
            return;
        }
        
        String method_name = args[0];

        /*
        for( int i=1; i<args.length; i++ ) {
            String s = args[i];
            try { a0 = Integer.parseInt( s );
            } catch( Exception e ) { }
            
        }
        */
    }

    public void dumpMethods(Object obj) {
        //Object result = method.invoke(obj, new Object[0]);
        //Class c = Class.forName("roombacomm.RoombaComm");
        try {
            Method m[] = obj.getClass().getMethods();
            for( int i=0; i< m.length; i++ )
                System.out.println( m[i].getName() +" -- "+ m[i].toString() );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void getMethod( String name, Object obj ) {
        try {
            String mname = name;
            Class[] types = new Class[] {};
            Method method = obj.getClass().getMethod(mname, types);
            System.out.println("class: "+method.getDeclaringClass());
            System.out.println("method: "+method.toString());
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

}
    /*
    try {
      serialEventMethod =
        parent.getClass().getMethod("serialEvent",
                                    new Class[] { Serial.class });
    } catch (Exception e) {
      // no such method, or an error.. which is fine, just ignore
    }
    */
