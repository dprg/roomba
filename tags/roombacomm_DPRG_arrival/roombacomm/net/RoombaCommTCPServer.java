/*
 *  RoombaComm TCP Interface
 *
 *
 *  Copyright (c) 2005 Tod E. Kurt, tod@todbot.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General
 *  Public License along with this library; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA  02111-1307  USA
 *
 */


package roombacomm.net;

import java.net.*;
import java.io.*;

public class RoombaCommTCPServer
{
    // default port
    int port = 8765;

    // the shutdown command received
    private boolean shutdown = false;

    public RoombaCommTCPServer() {
    }
    
    public void await() {
        ServerSocket serverSocket = null;
      try {
          serverSocket =  new ServerSocket(port, 1, null );
          //                              InetAddress.getByName("127.0.0.1"));
      }
      catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
      }
      /*      
      // Loop waiting for a request
      while (!shutdown) {
          Socket socket = null;
          InputStream input = null;
          OutputStream output = null;
          try {
              socket = serverSocket.accept();
              input = socket.getInputStream();
              output = socket.getOutputStream();
              
              if( input.available() ) {
              }
              // create Request object and parse
              Request request = new Request(input);
              request.parse();
              
              // create Response object
              Response response = new Response(output);
              response.setRequest(request);
              response.sendStaticResource();
              
              // Close the socket
              socket.close();
              
              //check if the previous URI is a shutdown command
              shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
          }
          catch (Exception e) {
              e.printStackTrace();
              continue;
          }
      }
      */
    }
      
    public static void main(String[] args) {
        RoombaCommTCPServer server = new RoombaCommTCPServer();
        server.await();
    }
    
}


