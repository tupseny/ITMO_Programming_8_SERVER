package com.dartin.project.server.processes.story.resources;

import com.dartin.util.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniil Yurkov on 18.11.2016.
 */
public class Shop extends Location implements Stashable {
	
	private Map<Item, Integer> stash = new HashMap<Item, Integer>(){
		public void save(){

		}
	};

	public Shop(HashMap<Item, Integer> stash, String name) {
		super(name);
		this.stash = stash;

	}

	public Shop(String name) {
		super(name);
	}
	
	@Override
	public void get(Item... items) {
		for (Item item : items) {
			stash.put(item, 0);
		}
	}
	
	@Override
	public void loose(Item... items) {
		for (Item item : items) {
			stash.remove(item);
		}
	}

	/* Can replace an existing item price */
	public void getWithPrice(Item item, int price) {
		stash.put(item, price);
	}

	public int getPrice(Item item){
		return stash.get(item);
	}

	public boolean available(Item item){
		return stash.containsKey(item);
	}

	public boolean sell(Hero hero, Item item) {
		if (stash.containsKey(item) && hero.isTrader()){
			hero.looseMoney(stash.get(item));
			this.loose(item);
			hero.get(item);
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "root.Shop '" + name + "'";
	}
}
