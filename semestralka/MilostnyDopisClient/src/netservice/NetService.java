package netservice;

import message.Message;
import message.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

/**
 * Created by Radek VAIS on 16.10.16.
 */
public class NetService {
    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(NetService.class.getName());


    public static boolean runFlag = false;
    public static final int MAX_MSG_LENGTH = 2048;
    public static String serverName;
    public static long recvBytes = 0;
    public static long sendBytes = 0;


    private String addressS;
    private int port;

    private Socket socket;
    private InetAddress address;

    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private static NetService INSTANCE = new NetService();

    public BlockingQueue<Message> toServe = new LinkedBlockingQueue<>();

    public Sender sender;
    public Receiver receiver;
    public static MessageHandler messageHandler;

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

    public static boolean isRunning() {
        return runFlag;
    }

    public String getServerName() {
        return address.getHostName();
    }

    public void initialize() throws IOException {
        NetService.logger.debug("initialize() - start");

        address = InetAddress.getByName(addressS);
        NetService.logger.trace("initialize() - get inet adress");
        NetService.logger.debug("Pripojuju se na : "+address.getHostAddress()+" se jmenem : "+address.getHostName()+"\n" );
        NetService.serverName = address.getHostName();

        socket = new Socket(address, port);
        NetService.logger.trace("initialize() - socket creation");
        socket.setSoTimeout(1000);

        outputStream = socket.getOutputStream();
        NetService.logger.trace("initialize() - output stream");

        inputStream = socket.getInputStream();
        NetService.logger.trace("initialize() - input stream");

        runFlag = true;
        startThreads();


        NetService.logger.trace("initialize() - end");
    }

    private void startThreads() {
        NetService.logger.debug("startThreads() - start");

        sender = new Sender();
        sender.start();
        receiver = new Receiver();
        receiver.start();
    }

    public void setup(String address, int port) {
        this.addressS = address;
        this.port = port;
    }

    private void closeStreams() throws IOException {
        outputStream.close();
        inputStream.close();
        socket.close();
    }

    private void nullStreams() {
        outputStream = null;
        inputStream = null;
        socket = null;
    }

    public void joinThreads() {
        try {
            sender.join();
            receiver.join();
            messageHandler.join();
        } catch (InterruptedException e) {
            logger.error("Snaha přerušit čekání na korektní ukončení vláken", e);
        } catch (NullPointerException e){
            logger.error("Vlákna nebyla inicializovaná", e);
        }
    }

    public synchronized OutputStream getOutputStream() {
        return outputStream;
    }

    public synchronized InputStream getInputStream() {
        return inputStream;
    }

    public Message getMessageToServe() {
        try {
            return toServe.take();
        } catch (InterruptedException e) {
            logger.debug("Čekání bylo přerušeno", e);
        }
        return null;
    }

    public void destroy() {
        runFlag = false;
        try {
            sender.interrupt();
            receiver.interrupt();
            messageHandler.interrupt();
            try {
                sleep(600);
            } catch (InterruptedException e) {
                logger.debug(e);
            }
            try {
                closeStreams();
            } catch (IOException e) {
                logger.debug(e);
            }
        }catch (NullPointerException e){
            logger.debug(e);
        }
        nullStreams();
    }


    public String getServerPort() {
        return "" + socket.getPort();
    }
}
