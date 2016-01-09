package clientUDP;

import java.net.*;
import java.io.*;
import java.util.Scanner;

//	-Djava.net.preferIPv4Stack=true
public abstract class clientUDP
{

 
	public static void main( String[] args )
	{
		Scanner sc = new Scanner(System.in);
		while (true) {
			String nacteny;
			DatagramSocket ds;
			System.out.println("Zadej zprávu");
			nacteny = sc.nextLine();

			try
            {

                   ds = new DatagramSocket( );

			   String data;
			   if(!nacteny.isEmpty()){
				   data = nacteny;
			   }
			   else{
				   data = "I am default datagram and I'm OK.";
			   }
                   byte[] buffer = data.getBytes();

               InetAddress address = InetAddress.getByName( "127.0.0.1" );
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
}

