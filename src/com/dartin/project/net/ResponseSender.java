package com.dartin.project.net;

import com.dartin.net.ServerMessage;
import com.dartin.project.ServerManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Daniil Y on 23.06.2017.
 */
public class ResponseSender extends Thread {

	private ServerMessage message;
	private InetAddress destination;

	public ResponseSender(ServerMessage message, InetAddress destination) {
		this.message = message;
		this.destination = destination;
	}

	@Override
	public void run() {
		try {
			send(message, destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void send(ServerMessage message, InetAddress destination) throws IOException {

		System.out.println("Send-back, cmd: " + message.getCmd() + ", dest: " + destination);
		byte[] bytes = message.toBytes();
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, destination, ServerManager.SEND_PORT);
		socket.send(packet);
	}
}
