import com.dartin.util.Item;
import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;

import java.io.IOException;
import java.net.*;
import java.util.Set;

/**
 * Created by Martin on 20.06.2017.
 */
public class Sender implements Runnable {
    private SocketAddress socketAddress;
    private DatagramSocket datagramSocket;
    private String msg;
    private InetAddress address;
    private int port;


    @Override
    public void run() {
        System.out.println("Resonse...");

        socketAddress = new InetSocketAddress(address, port);
        try {
            datagramSocket = new DatagramSocket(socketAddress);

            byte[] b = msg.getBytes();

            DatagramPacket response = new DatagramPacket(b, b.length);

            datagramSocket.send(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Sender(String msg, InetAddress address, int port) throws SocketException, UnknownHostException {
        this.msg = msg;
        this.address = address;
        this.port = port;
    }
    public Sender(String msg) throws UnknownHostException, SocketException {
        this(msg, InetAddress.getLocalHost(), 5555);
    }
}

