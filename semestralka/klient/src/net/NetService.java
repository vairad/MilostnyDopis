package net;

import java.util.logging.Logger;
import java.util.logging.LogManager;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.16.
 */
public class NetService {

    public static Logger logger; // inicializace probíhá v konfiguraci logovani

    private int serverPort;
    private InetAddress address;

    private Sender sender;
    private Receiver receiver;

    private Thread sendThread;
    private Thread recvThread;

    private DatagramSocket socket;

    private final BlockingQueue<String> toSend;
    private final BlockingQueue<String> recvMsgs;

    public NetService(int port, String address) throws UnknownHostException, SocketException{

        // incializace blokujících front zpráv
        toSend = new LinkedBlockingQueue<String>();
        recvMsgs = new LinkedBlockingQueue<String>();

        //inicializace socketu
        socket = new DatagramSocket();

        //atributy připojení
        this.serverPort = port;
        this.address = InetAddress.getByName(address);

        //spuštění obsluhy
        sender = new Sender(this.serverPort, this.address, toSend, this);
        sendThread = new Thread(sender);
        sendThread.start();
        logger.info("send Thread start");


        receiver = new Receiver(recvMsgs, this);
        recvThread = new Thread(receiver);
        recvThread.start();
        logger.info("recv Thread start");
    }

    public void addMsg(String msg){
        try {
            toSend.put(msg);
        }catch (InterruptedException e){
            logger.severe("nelze vlozit zpravu");
        }
    }

    public synchronized DatagramSocket getDatagramSocket(){
        return socket;
    }

}
