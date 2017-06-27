package com.dartin.project.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class which allows to send a message to class
 * where the logger was created.
 * <p> Supports prefixes. Exact prefix order:
 * <pre>
 *     [time][prefix][type]message
 * </pre>
 * Order is immutable. Time format: hh:mm:ss
 * <p>
 * Has inner private StringBuilder log, which is ,
 * initially, not appended by Logger methods - it
 * is up to you whether it is needed or not.
 * <P>
 * Max log length is 512. You can change it in
 * constructor while creating Logger instance.
 */
public abstract class Logger {

	///////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	///////////////////////////////////////////////////////////////////////////

	/**
	 * The INFO prefix
	 */
	public static String L_INFO     = "inf";

	/**
	 * The DEBUG prefix
	 */
	public static String L_DEBUG    = "dbg";

	/**
	 * The WARNING prefix
	 */
	public static String L_WARNING  = "war";

	/**
	 * The ERROR prefix
	 */
	public static String L_ERROR    = "err";


	///////////////////////////////////////////////////////////////////////////
	// VARIABLES
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Variable for accumulating messages
	 */
	protected StringBuilder log;

	/**
	 * Nuff said.
	 */
	private static final int STANDART_LOG_CAPACITY = 512;

	/**
	 * A prefix which is included in message
	 */
	private String prefix;

	/**
	 * Indicates whether it is needed to include current
	 * time as a prefix. Being included, it appears as
	 * a leading prefix.
	 */
	private boolean timePrefix = false;


	///////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new Logger with empty parameters.
	 * <p> When you create new Logger, you have to define
	 * its abstract method {@link Logger#newMessage(String)}.
	 * It is used to manage the messages which are sent by this logger.
	 */
	public Logger() {
		this("", STANDART_LOG_CAPACITY);
	}

	/**
	 * Private constructor for log capacity generalization.
	 * @param capacity capacity of StringBuilder
	 */
	public Logger(int capacity) {
		this("", capacity);
	}

	/**
	 * Creates a new Logger with given prefix.
	 * @param prefix a prefix for each message
	 */
	public Logger(String prefix) {
		this(prefix, STANDART_LOG_CAPACITY);
	}

	public Logger(String prefix, int capacity) {
		this.prefix = (prefix.equals("")? "": '[' + prefix + ']');
		log = new StringBuilder(capacity);
	}


	///////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Whether it is needed to include time in a log message.
	 * Being included, it appears as a leading prefix.
	 * <p> Example:
	 * <p> Current date: 11/30/1998
	 * <p> Current time (hh:mm:ss): 06:20:15 am
	 * <pre>
	 *     logger.useTimePrefix(true);
	 *     logger.sendMessage("Answer is 42");
	 *     // message: "[06:20:15]Answer is 42"
	 *
	 *     logger.setPrefix("TOP_SECRET");
	 *     logger.sendMessage("Gold needed!")
	 *     // message: "[06:20:15][TOP_SECRET]Gold needed!"
	 * </pre>
	 * @param state boolean value
	 * @see Logger
	 */
	private void useTimePrefix(boolean state) {
		timePrefix = state;
	}

	/**
	 * Sets a prefix which will be included in each of log's message.
	 * Set 'null' to exclude prefix from further messages.
	 * @param prefix a preffix to be included
	 * @see Logger
	 */
	private void setPrefix(String prefix) {
		if (prefix != null)
			this.prefix = '[' + prefix + ']';
		else
			this.prefix = "";
	}

	/**
	 * Sends the message to the creator.
	 * @param msg
	 */
	public void sendMessage(String msg) {
		String newMessage =
				((timePrefix) ? LocalDateTime.now().format(DateTimeFormatter.ISO_TIME) : "")
				+ prefix
				+ msg;
		newMessage(newMessage);
	}

	/**
	 * Logs with 'information' prefix
	 * @param msg message to log
	 */
	public void inf(String msg) {
		sendMessage('[' + L_INFO + ']' + msg);
	}

	/**
	 * Logs with 'debug' prefix
	 * @param msg message to log
	 */
	public void dbg(String msg) {
		sendMessage('[' + L_DEBUG + ']' + msg);
	}

	/**
	 * Logs with 'warning' prefix
	 * @param msg message to log
	 */
	public void war(String msg) {
		sendMessage('[' + L_WARNING + ']' + msg);
	}

	/**
	 * Logs with 'error' prefix
	 * @param msg message to log
	 */
	public void err(String msg) {
		sendMessage('[' + L_ERROR + ']' + msg);
	}

	/**
	 * Returns current log of this Logger instance
	 * @return log in string representation
	 */
	public String getLog() {
		return log.toString();
	}

	/**
	 * Give instructions to Logger of what is needed to do with log
	 * @param msg
	 */
	protected abstract void newMessage(String msg);
}
