package exampleTCP;

import java.net.*;
import java.io.*;

public class TcpServer {
  public final static int DEFAULT_PORT = 6789;
  public static void main (String args[]) throws IOException {
    Socket client;
    if (args.length != 1)
      client = accept (DEFAULT_PORT);
    else
      client = accept (Integer.parseInt (args[0]));
    try {
      PrintWriter writer;
      BufferedReader reader;
      reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
// read a line
      String line = reader.readLine();
      System.out.println("Client says: " + line);
// write a line
      writer.println ("You have connected to the Very Simple Server.");
      writer.flush();
      reader.close();
      writer.close();
    } finally { // closing down the connection
      System.out.println ("Closing");
      client.close ();
    }
  }

  static Socket accept (int port) throws IOException {
    System.out.println ("Starting on port " + port);
    ServerSocket server = new ServerSocket (port);
    System.out.println ("Waiting");
    Socket client = server.accept ();
    System.out.println ("Accepted from " + client.getInetAddress ());
    server.close ();
    return client;
  }
}