package exampleTime;

import java.lang.System;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Date;

public class TimeServerApp {

  public static void main(String args[]){
    try{
      DatagramSocket socket = new DatagramSocket(2345);
      String localAddress = InetAddress.getLocalHost().getHostName().trim();
      int localPort = socket.getLocalPort();
      System.out.print(localAddress+": ");
      System.out.println("Time Server is listening on port "+localPort+".");
      int bufferLength = 256;
      byte outBuffer[];
      byte inBuffer[] = new byte[bufferLength];
      DatagramPacket outDatagram;
      DatagramPacket inDatagram =
        new DatagramPacket(inBuffer,inBuffer.length);
      boolean finished = false;
      do {
        socket.receive(inDatagram);
        InetAddress destAddress = inDatagram.getAddress();
        String destHost = destAddress.getHostName().trim();
        int destPort = inDatagram.getPort();
        System.out.println("\nReceived a datagram from "+destHost+" at port "+
        destPort+".");
        String data = new String(inDatagram.getData()).trim();
        System.out.println("It contained the data: "+data);
        if(data.equalsIgnoreCase("quit")) finished=true;
        String time = new Date().toString();
        outBuffer=time.getBytes();
        outDatagram =
          new DatagramPacket(outBuffer,outBuffer.length,destAddress,destPort);
        socket.send(outDatagram);
        System.out.println("Sent "+time+" to "+destHost+" at port "+destPort+".");
      } while(!finished);
    }catch (IOException ex){
      System.out.println("IOException occurred.");
    }
  }
}