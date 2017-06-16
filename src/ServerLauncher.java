import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ServerLauncher {

    public static void main(String[] args) {

        System.out.println("starting server");

        try {
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 5555);
            DatagramSocket datagramSocket = new DatagramSocket(socketAddress);

            byte[] b = new byte[11];
            DatagramPacket datagramPacket = new DatagramPacket(b, b.length);

            System.out.println(datagramSocket.getInetAddress());

            datagramSocket.receive(datagramPacket);
            System.out.println(Arrays.toString(datagramPacket.getData()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
