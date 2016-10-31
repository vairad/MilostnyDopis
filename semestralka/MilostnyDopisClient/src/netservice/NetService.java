package netservice;

import message.Event;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.db.jpa.converter.MessageAttributeConverter;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Radek VAIS on 16.10.16.
 */
public class NetService {
    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(NetService.class.getName());
    public static boolean runFlag = true;
    public static final int MAX_MSG_LENGTH = 2048;

    private String addressS;
    private int port;

    private Socket socket;
    private InetAddress address;

    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private static NetService INSTANCE = new NetService();

    public Sender sender;
    public Reciever reciever;

    private NetService() {
        // no code here
    }

    public static NetService getInstance(){
        return INSTANCE;
    }

    @Deprecated
    public static boolean checkIpOctet(String ipOctet){
        int ip = Integer.parseInt(ipOctet);
        return ip > 0 && ip < 256;
    }

    public static int checkPort(String portS){
        int port = Integer.parseInt(portS);
        if ( port > 0 && port < 65536){
            return port;
        }
        return -1;
    }

    public void initialize() throws IOException {
        NetService.logger.debug("initialize() - start");

        address = InetAddress.getByName(addressS);
        NetService.logger.trace("initialize() - get inet adress");
        NetService.logger.debug("Pripojuju se na : "+address.getHostAddress()+" se jmenem : "+address.getHostName()+"\n" );

        socket = new Socket(address, port);
        NetService.logger.trace("initialize() - socket creation");

        outputStream = socket.getOutputStream();
        NetService.logger.trace("initialize() - output stream");

        inputStream = socket.getInputStream();
        NetService.logger.trace("initialize() - input stream");


        startThreads();

        NetService.logger.trace("initialize() - end");
    }

    private void startThreads() {
        NetService.logger.debug("startThreads() - start");

        sender = new Sender();
        sender.start();
        reciever = new Reciever();
        reciever.start();
    }

    public void setup(String address, int port) {
        this.addressS = address;
        this.port = port;
    }

    public void release() throws IOException {
        stop();
        joinThreads();
        outputStream.close();
        inputStream.close();
        socket.close();
        outputStream = null;
        inputStream = null;
        socket = null;
    }

    private void joinThreads() {
        try {
            sender.join();
            reciever.join();
        } catch (InterruptedException e) {
            logger.error("nějakej bordel ve vláknech", e);
        }

    }

    public static void stop(){
        runFlag = false;
    }

    public synchronized OutputStream getOutputStream() {
        return outputStream;
    }

    public synchronized InputStream getInputStream() {
        return inputStream;
    }
}
