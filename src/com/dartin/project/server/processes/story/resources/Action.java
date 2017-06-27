package com.dartin.project.server.processes.story.resources;

import com.dartin.util.Item;

/**
 * @author Daniil Yurkov on 15.12.2016.
 */
public class Action {
	
	private final Hero initiator;
	private final Hero target;
	private final Intent intent;
	private final Item item;

	public enum Intent{
		STEAL, GIVE
	}

	public enum Result{
		SUCCESSFUL, INTERRUPTED
	}

	public Action(Hero initiator, Hero target, Intent intent, Item item) {
		this.initiator = initiator;
		this.target = target;
		this.intent = intent;
		this.item = item;
	}

	@Override
	public String toString() {
		return initiator.toString() + ":"
				+ target + ":"
				+ intent + ":";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Action action = (Action) o;

		if (!initiator.equals(action.initiator)) return false;
		if (!target.equals(action.target)) return false;
		if (intent != action.intent) return false;
		return item.equals(action.item);
	}

	@Override
	public int hashCode() {
		return initiator.hashCode()
				+ 31 * target.hashCode()
				+ 31 * 31 * intent.hashCode()
				+ 31 * 31 * 31 * item.hashCode();
		
	}
}
