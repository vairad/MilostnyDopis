package test;

import java.lang.System;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.io.IOException;

public class SendEcho {

	static final int PORT = 1234;

	  public static void main( String args[] ) throws Exception {
	    String theStringToSend = "I'm a datagram and I'm O.K.";
	    byte[] theByteArray = new byte[ theStringToSend.length() ];
	    theByteArray = theStringToSend.getBytes();
	
	// Get the IP address of our destination...
	    InetAddress theIPAddress = null;
	    try {
	      System.out.println("Get the IP address of our destination...");
	      theIPAddress = InetAddress.getByName( "192.168.2.103" );
	    } catch (UnknownHostException e) {
	      System.out.println("Host not found: " + e);
	      System.exit(1);
	    }
	// Build the packet...
	//  System.out.println("Build the packet...");
	//  System.out.println("IP address:" + getAddress() + " Port:" /*+ getPort()*/ );
	  
	    DatagramPacket thePacket = new DatagramPacket( theByteArray,
	    		theStringToSend.length(), theIPAddress, PORT );
	    System.out.println("Build the packet...");
	    System.out.println("IP address:" + thePacket.getAddress() + " Port:" + thePacket.getPort() );
	
	// Now send the packet
	    DatagramSocket theSocket = null;
	    try {
	      theSocket = new DatagramSocket();
	    } catch (SocketException e) {
	      System.out.println("Underlying network software has failed: " + e);
	      System.exit(1);
	    }
	    try {
	      System.out.println("Now send the packet...");
	      theSocket.send( thePacket );
	    } catch (IOException e) {
	      System.out.println("IO Exception: " + e);
	    }
	    
	// Receive echo  
	    byte[] inBuffer = new byte[ 2048 ];
	    DatagramPacket inDatagram = new DatagramPacket(inBuffer,inBuffer.length);
	    theSocket.receive(inDatagram);
	    System.out.println("Ozvena?:");
	    System.out.println(new String(inBuffer));
	    
	    theSocket.close();
	  }
}
