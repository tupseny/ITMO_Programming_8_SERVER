package com.dartin.project.server.processes.story.resources;

/**
 * @author Daniil Yurkov on 16.11.2016.
 */
public class ActionInterruptedException extends Exception {
	
	public ActionInterruptedException(String message) {
		super(message);
	}
	
	public ActionInterruptedException() {
		super("root.ActionInterruptedException");
	}
}
