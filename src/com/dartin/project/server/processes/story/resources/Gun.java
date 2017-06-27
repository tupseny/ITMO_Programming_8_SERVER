package com.dartin.project.server.processes.story.resources;

import com.dartin.util.Item;

import java.time.LocalDate;

/**
 * @author Daniil Yurkov on 17.11.2016.
 */
public class Gun extends Item implements Shootable {
	
	private int belt;
	private int currentHolder;
	private int shootDamage;
	private int hitDamage = 42;
	public final int HOLDER;
	
	public Gun(String name, String usage, Size size, int holder, int bullets, int shootDamage) {
		super(name, usage, size, LocalDate.now());
		HOLDER = holder;
		this.belt = bullets;
		this.shootDamage = shootDamage;
	}
	
	@Override
	public boolean shoot(Hero target) {
		if (currentHolder > 0) {
			target.injure(shootDamage);
			currentHolder--;
			return true;
		} else return false;
	}
	
	@Override
	public boolean reload() {
		if (belt > 0 && belt < HOLDER) {
			currentHolder = belt;
			belt = 0;
			return true;
		} else if (belt > 0) {
			currentHolder = HOLDER;
			belt -= HOLDER;
			return true;
		} else return false;
	}
	
	@Override
	public void hit(Hero target) {
		target.injure(hitDamage);
	}
}
