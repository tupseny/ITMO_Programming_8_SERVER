import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by Martin on 20.06.2017.
 */
public class Server implements Runnable{
    private boolean isStopped;
    private SocketAddress socketAddress;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private Thread thread;

    public Server(InetAddress address, int port) throws IOException {


        socketAddress = new InetSocketAddress(address, port);
        datagramSocket = new DatagramSocket(socketAddress);
    }

    public Server() throws IOException {
        this(InetAddress.getLocalHost(), 5555);
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();

        while (!isStopped) {
            System.out.println("Listening...");

            try {
                byte[] b = new byte[11];
                datagramPacket = new DatagramPacket(b, b.length);
                datagramSocket.receive(datagramPacket);

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(Arrays.toString(datagramPacket.getData()));

        }
    }
}
