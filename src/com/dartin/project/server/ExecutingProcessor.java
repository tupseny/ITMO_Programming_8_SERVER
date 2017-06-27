package com.dartin.project.server;

import com.dartin.net.ServerMessage;
import com.dartin.project.ServerManager;
import com.dartin.project.net.ResponseSender;
import com.dartin.project.server.data.RequestManager;
import com.dartin.util.Item;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Daniil Y on 27.06.2017.
 */
public class ExecutingProcessor extends Thread {

	private ServerMessage message;
	private InetAddress address;

	public ExecutingProcessor(ServerMessage message, InetAddress address) {
		this.message = message;
		this.address = address;
	}

	@Override
	public void run() {
		try {
			executeMessageRequest(message, address);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to execute message! cmd: " + message.getCmd());
		}
	}

	private static synchronized void executeMessageRequest(ServerMessage message, InetAddress address) throws IOException {

		switch (message.getCmd()) {

			case ServerMessage.CMD_VERIFY:
				ServerMessage verifyResponse = new ServerMessage(ServerMessage.CMD_VERIFY);
				verifyResponse.addContent(ServerMessage.CONTENT_VER, new AtomicBoolean(
						((AtomicInteger)message.getContent(ServerMessage.CONTENT_VER))
								.get() == Item.VERSION
				));
				verifyResponse.lock();
				new ResponseSender(verifyResponse, address).start();
				break;

			case ServerMessage.CMD_RESTORE:
				ServerMessage restoreResponse = new ServerMessage(ServerMessage.CMD_RESTORE);
				restoreResponse.addContent(
						ServerMessage.CONTENT_SET,
						(Serializable) RequestManager.getSetItemsFromBase()
				);
				restoreResponse.lock();
				new ResponseSender(restoreResponse, address).run();
				break;

			case ServerMessage.CMD_RUN:
				Logger sendBackLogger = new Logger() {
					@Override
					protected void newMessage(String msg) {
						log.append(msg).append('\n');
					}
				};
				Runnable submitResponse = () -> {
					ServerMessage runResponse = new ServerMessage(
							ServerMessage.CMD_RUN
					);
					runResponse.addContent(ServerMessage.CONTENT_LOG, sendBackLogger.getLog());
					runResponse.addContent(ServerMessage.CONTENT_SET,
							(Serializable) RequestManager.getSetItemsFromBase());
					runResponse.lock();
					new ResponseSender(runResponse, address).start();
				};
				ServerManager.executeStory(sendBackLogger, submitResponse);
				//replace with Thread.join()..?
				break;

			case ServerMessage.CMD_ADD:
				Item insertItem = null;
				try {
					insertItem = (Item)message.getContent(ServerMessage.CONTENT_SET);
					Set<Item> newSet = RequestManager.insertItem(insertItem);
					ServerMessage addResponse = new ServerMessage(ServerMessage.CMD_ADD);
					addResponse.addContent(ServerMessage.CONTENT_SET, (Serializable) newSet);
					addResponse.lock();
					new ResponseSender(addResponse, address).start();
				} catch (SQLException e) {
					ServerMessage addResponse = new ServerMessage(ServerMessage.CMD_ADD);
					addResponse.addContent(ServerMessage.CONTENT_SET, null);
					addResponse.lock();
					new ResponseSender(addResponse, address).start();
					System.out.println("Cannot insert " + insertItem + "!");
				} catch (ClassCastException e) {
					e.printStackTrace();
				}
				break;

			case ServerMessage.CMD_REMOVE:
				Item removeItem = null;
				try {
					removeItem = (Item)message.getContent(ServerMessage.CONTENT_SET);
					Set<Item> newSet = RequestManager.removeItem(removeItem);
					ServerMessage removeResponse = new ServerMessage(ServerMessage.CMD_REMOVE);
					removeResponse.addContent(ServerMessage.CONTENT_SET, (Serializable) newSet);
					removeResponse.lock();
					new ResponseSender(removeResponse, address).start();
				} catch (SQLException e) {
					e.printStackTrace();
					ServerMessage removeRespones = new ServerMessage(ServerMessage.CMD_REMOVE);
					removeRespones.addContent(ServerMessage.CONTENT_SET, null);
					removeRespones.lock();
					new ResponseSender(removeRespones, address).start();

					System.out.println("Cannot remove " + removeItem + "!");
				} catch (ClassCastException e) {
					e.printStackTrace();
				}
				break;

			case ServerMessage.CMD_REMOVE_LOWER:
				Item removeLowerItem;
				try {
					removeLowerItem = (Item)message.getContent(ServerMessage.CONTENT_SET);
					Set<Item> newSet = RequestManager.removeLower(removeLowerItem);
					ServerMessage removeLowerResponse = new ServerMessage(ServerMessage.CMD_REMOVE_LOWER);
					removeLowerResponse.addContent(ServerMessage.CONTENT_SET, (Serializable) newSet);
					removeLowerResponse.lock();
					new ResponseSender(removeLowerResponse, address).start();
				} catch (ClassCastException e) {
					e.printStackTrace();
				}
				break;
		}

	}

}
