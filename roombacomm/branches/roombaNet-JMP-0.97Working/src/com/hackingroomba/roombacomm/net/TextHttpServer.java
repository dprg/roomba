


package com.hackingroomba.roombacomm.net;

import java.net.*;
import java.io.*;

public class TextHttpServer {
    
    int port = 6767;
    /*
    String cmds[] =
    {
        "reset",     // zero args
        "stop",      // zero args
        "goforward", // one optional arg
        "gobackward", // one optional arg
        "spinleft",  // one optional arg
        "spinright", // one optional arg
        "beep",  // two args
    };
    */

    // the shutdown command received
    private boolean shutdown = false;
    
    public static void main(String[] args) {
        TextHttpServer server = new TextHttpServer();
        server.await();
    }
    
    
    public void await() {
        System.out.println("awaiting connections on port "+port+"...");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 1, null);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Loop waiting for a request
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                socket = serverSocket.accept(); // this blocks

                input = socket.getInputStream();
                output = socket.getOutputStream();

                StringBuffer request = parseRequest( input );
                String uristr = parseUri( request.toString() );
                System.out.println("uristr: "+uristr);

                URI uri = new URI( uristr );
                System.out.println("path:"+uri.getPath()+", query:"+uri.getQuery());
                
                // Close the socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                //System.exit(1);
            }
        }

    }



    public StringBuffer parseRequest(InputStream input) {
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = input.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j=0; j<i; j++) {
            request.append((char) buffer[j]);
        }
        System.out.print(request.toString());
        
        return request;
    }

    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }

    
}
