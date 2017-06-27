package com.dartin.project.net;

import com.dartin.net.ServerMessage;
import com.dartin.project.ServerManager;
import com.dartin.project.server.ExecutingProcessor;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;


public class SocketListener extends Thread {

	private static final Object lock = new Object();

	private static final int messageLength = 1024;
	public final int PORT;
	private DatagramSocket socket;
	private static final byte STOP_BYTE = 13;
	private boolean isRunning = false;

	public SocketListener(int port) throws SocketException, UnknownHostException {
		System.out.println("initializing listener");
		PORT = port;
		socket = new DatagramSocket(port, InetAddress.getLocalHost());
	}

	public SocketListener(InetSocketAddress isa) throws SocketException {
		System.out.println("initializing listener");
		PORT = isa.getPort();
		socket = new DatagramSocket(isa);
	}

	@Override
	public void run() {
		isRunning = true;
		byte[] receivedData;
		DatagramPacket packet;
		System.out.println("starting listener");
		while (true) {
			System.out.println("listening...");
			receivedData = new byte[messageLength];
			packet = new DatagramPacket(receivedData, messageLength);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!isRunning) try {
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} else {
				System.out.println("Datagram received: " + Arrays.toString(packet.getData()));
				System.out.println("From: " + packet.getAddress().toString());
				try {
					ServerMessage message = ServerMessage.recover(packet.getData());
					new ExecutingProcessor(message, packet.getAddress()).start();
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("Failed to recover message!");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean pauseListener() {
		return isRunning = false;
	}

	public boolean resumeListener() {
		isRunning = true;
		synchronized (lock) {
			lock.notify();
		}
		return true;
	}

	@Override
	public String toString() {
		return "SocketListener is "
				+ (isRunning? "running": "stopped")
				+ " using port " + PORT;
	}
}