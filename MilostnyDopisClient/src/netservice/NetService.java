package netservice;

import gui.App;
import javafx.application.Platform;
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


    /** zda je netservice ve stavu běžícím (podmínka pro vlákna sender, reciever a message handler) */
    private static boolean runFlag;
    /** Zda byl konec spojení iniciovaný uživatelem */
    private static boolean userEnd;
    /** Informace zda jiné vlákno vyvolalo reconnect */
    private static boolean reconnecting;

    static final int MAX_MSG_LENGTH = 2048;

    public static long recvBytes = 0;
    public static long sendBytes = 0;

    private String addressS;
    private String serverName;
    private int port;

    private Socket socket;
    private InetAddress address;

    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private static NetService INSTANCE = new NetService();

    BlockingQueue<Message> toServe = new LinkedBlockingQueue<>();

    public Sender sender = null;
    Receiver receiver = null;
    private static MessageHandler messageHandler = null;

    // ================ SINGLETON INIT ===========================================================================
    /** Privátní konstruktor */
    private NetService() {}

    /** INSTANCE NETSERVICE */
    public static NetService getInstance(){
        return INSTANCE;
    }


    /** Přpiravena pro paralelní volání pokud bude
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        NetService.logger.debug("initialize() - start");

        InetAddress address = InetAddress.getByName(addressS);
        NetService.logger.trace("initialize() - get inet adress");
        NetService.logger.debug("Pripojuju se na : "+address.getHostAddress()+" se jmenem : "+address.getHostName()+"\n" );

        Socket socketTMP = new Socket(address, port);
        NetService.logger.trace("initialize() - socket creation");

        if(Thread.currentThread().isInterrupted()){
            logger.trace("Umřel přihlašovač");
            return;
        }

        logger.debug("clean old mess");
        NetService.getInstance().destroy();
        toServe.clear();

        socket = socketTMP;
        this.address = address;
        serverName = address.getHostName();

        socket.setSoTimeout(1000);
        outputStream = socket.getOutputStream();
        NetService.logger.trace("initialize() - output stream");

        inputStream = socket.getInputStream();
        NetService.logger.trace("initialize() - input stream");

        userEnd = false;
        runFlag = true;
        reconnecting = false;
        startThreads();
        NetService.logger.trace("initialize() - end");
    }

    private void startThreads() {
        NetService.logger.debug("startThreads() - start");

        sender = new Sender();
        sender.start();
        receiver = new Receiver();
        receiver.start();
        messageHandler = new MessageHandler();
        messageHandler.start();
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

    public Message getMessageToServe() throws InterruptedException {
        return toServe.take();

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

    public void stop() {
        runFlag = false;
    }

    public static void userEnding() {
        userEnd = false;
    }

    public static void initMessageHandler(MessageHandler messageHandler) {
        if(NetService.messageHandler == null){
            NetService.messageHandler = messageHandler;
            logger.debug("new message handler");
        }
        if(!NetService.messageHandler.isAlive()){
            NetService.messageHandler = messageHandler;
        }
    }

    synchronized static void reconnect(){
        logger.debug("Reconnect");
        if(userEnd){
            logger.trace("User end");
            return;
        }
        if (reconnecting) {
            logger.fatal("Anothrer thread invoke reconnect");
            return;
        }
        logger.fatal("Information: Invoke reconnect");
        reconnecting = true;
        App.addLoginWorker(new Thread(App::reconnect));
    }

    public static void startMessageHandler() {
        try {
            messageHandler.start();
        }catch (IllegalThreadStateException e){
            logger.debug("netservice was already started");
        }
    }

    public static String getPort() {
        return getInstance().getServerPort();
    }

    public static void endOfReconnection() {
        logger.debug("successful reconnection atemp");
        reconnecting = false;
    }


    public static boolean isRunning() {
        return runFlag;
    }

    public static String getServerName() {
        return NetService.getInstance().serverName;
    }

    public static boolean isUserEnd() {
        return userEnd;
    }

//================= STATICKE METODY NEZAVISLE NA OBJEKTU==========================================================

    /**
     * Metoda kontroluje použitelný rozsah portu 0 - 65535
     * @param portS textová reprezentace čísla portu
     * @return portn number or -1 if port number is not valid
     */
    public static int checkPort(String portS){
        int port;
        try {
            port = Integer.parseInt(portS);
        }catch (NumberFormatException e){
            return -1;
        }
        if ( port > 0 && port < 65536){
            return port;
        }
        return -1;
    }
}
