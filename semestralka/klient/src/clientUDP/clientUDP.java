package clientUDP;

import java.net.*;
import java.io.*;

//	-Djava.net.preferIPv4Stack=true
public abstract class clientUDP
{

 
	public static void main( String[] args )
	{
		int bufferSize = 1; 
  		DatagramSocket ds;
 		try 
		{

    			ds = new DatagramSocket( );
  
			String data = "I am datagram and I'm OK.";
    			byte[] buffer = data.getBytes();
			
			InetAddress address = InetAddress.getByName( "192.168.1.102" );
			int port = 1234;

			System.out.print(" Odesilam data = "+data+"\n" );
      			DatagramPacket send;
				send = new DatagramPacket(buffer, buffer.length, address, port);
				ds.send(send);

      			DatagramPacket recv = new DatagramPacket(buffer, buffer.length );
			ds.receive( recv );
			data = new String( buffer );
			System.out.print( "Prijal sem data - "+data+"\n");
				
      		}
		catch (Exception e) 
		{
        		System.err.println(e);
      		}      
	}
}

