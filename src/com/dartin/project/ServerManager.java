package com.dartin.project;

import com.dartin.net.ServerMessage;
import com.dartin.project.net.ResponseSender;
import com.dartin.project.net.SocketListener;
import com.dartin.project.server.Logger;
import com.dartin.project.server.data.RequestManager;
import com.dartin.project.server.processes.story.Story;
import com.dartin.util.Item;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

	public static final int SEND_PORT = 5554;
	public static final int RECEIVE_PORT = 5555;
	private static final String CMD_SHOW_STATE = "state";
	private static final String CMD_AVLBL_CMDS = "commands";
	private static final String CMD_PREPARE = "prepare";
	private static final String CMD_START = "start";
	private static final String CMD_PAUSE = "pause";
	private static final String CMD_RESUME = "resume";
	private static final String CMD_EXE_STORY = "execute_story";
	private static final String CMD_CLOSE = "close";

	private static Scanner consoleScanner = new Scanner(System.in);

	private static boolean serverPrepared = false;
	private static boolean serverStarted = false;
	private static boolean serverRunning = false;
	private static SocketListener receiverThread;
	private static Thread consoleThread;
	private static Logger consoleLogger;
	private static Logger serverLogger;

	public static void main(String[] args) {
		System.out.println("Starting server manager...");
		consoleThread = new Thread(ServerManager::loadConsoleControl);
		consoleThread.start();
		System.out.println("Manager started");
		printAvailableCommands();
	}

	private static boolean prepareServer() throws SocketException, UnknownHostException {
		receiverThread = new SocketListener(RECEIVE_PORT);
		consoleLogger = new Logger() {
			@Override
			protected void newMessage(String msg) {
				System.out.println(msg);
			}
		};
		serverLogger = new Logger() {

			private StringBuilder log = new StringBuilder(512);

			@Override
			protected void newMessage(String msg) {
				log.append(msg).append('\n');
			}

			public void sendMessage() {
				//send log
				log.delete(0, log.length());
			}
		};
		return true;
	}

	private static boolean startServer() {
		receiverThread.start();
		return true;
	}

	private static boolean pauseServer() {
		try {
			receiverThread.pauseListener();
			sendFalseMessage();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to stop server");
			startServer();
			return true;
		}
		return false;
	}

	private static boolean resumeServer() {
		receiverThread.resumeListener();
		return true;
	}

	private static void loadConsoleControl() {
		while (true) {
			manageCommand(
				consoleScanner.next()
			);
		}
	}

	private static void manageCommand(String cmd) {
		switch (cmd) {
			case CMD_PREPARE:
				if (!serverPrepared)
					try {
						serverPrepared = prepareServer();
						System.out.println("Server got ready");
					} catch (Exception e) {
						System.out.println("Unable to prepare server:");
						e.printStackTrace();
					}
					else {
					System.out.println("Server was ready");
				}
				break;

			case CMD_START:
				if (!serverPrepared)
					System.out.println("Prepare server first");
				else if (!serverStarted) {
					serverStarted = startServer();
					serverRunning = serverStarted;
					System.out.println("Server started");
				} else
					System.out.println("Server was already started");

				break;

			case CMD_PAUSE:
				if (serverRunning) {
					serverRunning = pauseServer();
					System.out.println("Server paused");
				} else System.out.println("Server was not running");
				break;

			case CMD_RESUME:
				if (!serverRunning) {
					serverRunning = resumeServer();
					System.out.println("Server resumed");
				} else
					System.out.println("Server is already running!");
				break;

			case CMD_EXE_STORY:
				if (!serverPrepared)
					System.out.println("Prepare server first");
				else
					executeStory(consoleLogger);
				break;

			case CMD_SHOW_STATE:
				showServerState();
				break;

			case CMD_AVLBL_CMDS:
				printAvailableCommands();
				break;

			case CMD_CLOSE:
				manageCommand(CMD_PAUSE);
				System.exit(0);
				break;

			default:
				System.out.println("No command '" + cmd + "' found");
				printAvailableCommands();
				break;
		}
	}

	private static void executeStory(Logger logger) {
		new Story(logger).start();
	}

	public static void executeStory(Logger logger, Runnable onStopRun) {
		new Story(logger){

			@Override
			protected void onStop() throws Exception {
				onStopRun.run();
			}
		}.start();
	}

	private static void showServerState() {
		System.out.println(
				"serverPrepared = " + serverPrepared + '\n' +
				"serverStarted = " + serverStarted + '\n' +
				"serverRunning = " + serverRunning + '\n' +
				(receiverThread != null? receiverThread.toString(): "")
		);
	}

	private static void printAvailableCommands() {
		System.out.println(
				"Available commands:" + '\n'
				+ " - " + CMD_AVLBL_CMDS + '\n'
				+ " - " + CMD_CLOSE + '\n'
				+ " - " + CMD_EXE_STORY + '\n'
				+ " - " + CMD_PREPARE + '\n'
				+ " - " + CMD_SHOW_STATE + '\n'
				+ " - " + CMD_START + '\n'
				+ " - " + CMD_PAUSE + '\n'
				+ " - " + CMD_RESUME
		);
	}

	private static void sendFalseMessage() throws IOException {
		InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), receiverThread.PORT);
		DatagramSocket datagramSocket = new DatagramSocket();
		datagramSocket.send(new DatagramPacket(new byte[3], 3, socketAddress));
	}
}
