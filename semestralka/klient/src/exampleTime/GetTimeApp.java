package exampleTime;

import java.lang.System;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class GetTimeApp {

  public static void main(String args[]){
    try{
      DatagramSocket socket = new DatagramSocket();
      InetAddress localAddress = InetAddress.getLocalHost();
      String localHost = localAddress.getHostName();
      int bufferLength = 256;
      byte outBuffer[];
      byte inBuffer[] = new byte[bufferLength];
      DatagramPacket outDatagram;
      DatagramPacket inDatagram = new DatagramPacket(inBuffer,inBuffer.length);
      for(int i=0;i<5;++i){
        outBuffer = new byte[bufferLength];
        outBuffer = "time".getBytes();
        outDatagram = new DatagramPacket(outBuffer,outBuffer.length,
        localAddress,2345);
        socket.send(outDatagram);
        System.out.println("\nSent time request to "+localHost+
        " at port 2345.");
        socket.receive(inDatagram);
        InetAddress destAddress = inDatagram.getAddress();
        String destHost = destAddress.getHostName().trim();
        int destPort = inDatagram.getPort();
        System.out.println("Received a datagram from "+destHost+" at port "+
        destPort+".");
        String data = new String(inDatagram.getData());
        data=data.trim();
        System.out.println("It contained the following data: "+data);
      }
      outBuffer = new byte[bufferLength];
      outBuffer = "quit".getBytes();
      outDatagram = new DatagramPacket(outBuffer,outBuffer.length,
      localAddress,2345);
      socket.send(outDatagram);
    }catch (IOException ex){
      System.out.println("IOException occurred.");
    }
  }
}