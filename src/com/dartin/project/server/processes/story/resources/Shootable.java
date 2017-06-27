package com.dartin.project.server.processes.story.resources;

/**
 * @author Daniil Yurkov on 17.11.2016.
 */
public interface Shootable {
	public boolean shoot(Hero target);
	public boolean reload();
	public void hit(Hero target);
}
