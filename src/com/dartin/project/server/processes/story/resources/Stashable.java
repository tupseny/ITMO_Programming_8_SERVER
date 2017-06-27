package com.dartin.project.server.processes.story.resources;

import com.dartin.util.Item;

/**
 * @author Daniil Yurkov on 18.11.2016.
 */
public interface Stashable {
	public void get(Item... items);
	public void loose(Item... items);

}
