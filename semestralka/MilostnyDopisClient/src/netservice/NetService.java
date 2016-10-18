package netservice;

/**
 * Created by Radek VAIS on 16.10.16.
 */
public class NetService extends Thread {

    private NetService() {
    }

    @Override
    public void run(){

    }

    public static boolean checkIpOctet(String ipOctet){
        int ip = Integer.parseInt(ipOctet);
        return ip > 0 && ip < 256;
    }

    public static boolean checkPort(String portS){
        int port = Integer.parseInt(portS);
        return port > 0 && port < 65536;

    }
}
