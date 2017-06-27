package com.dartin.project.server.processes.story.resources;

import com.dartin.project.server.Logger;
import com.dartin.project.server.processes.story.Story;
import com.dartin.util.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniil Yurkov on 16.11.2016.
 */
public class Hero implements Stashable {

	public static class EventHandler {

		private static final String EVENT_NAME_ENTER = "enter";
		private static final String EVENT_NAME_STEAL = "steal";
		private static final String EVENT_NAME_GIVE  = "give";
		private static final String EVENT_NAME_BUY   = "buy";
		private static final String EVENT_NAME_OPEN_TRADE  = "opens trade";
		private static final String EVENT_NAME_CLOSE_TRADE = "closes trade";
		private static final String EVENT_NAME_NEW_HERO = "new hero";
		private static final String EVENT_NAME_ANNOUNCE = "announcing";



		private Object handler;
		private final int HANDLING_MODE_NA = -1;
		private final int HANDLING_MODE_LOGGER = 0;
		private final int HANDLING_MODE_CURRENT;

		public EventHandler(Logger logger) {
			this.handler = logger;
			HANDLING_MODE_CURRENT = HANDLING_MODE_LOGGER;
		}

		EventHandler() {
			HANDLING_MODE_CURRENT = HANDLING_MODE_NA;
		}

		public void handle(String actionName, Object... args) {
			switch (HANDLING_MODE_CURRENT) {
				case HANDLING_MODE_LOGGER :
					((Logger)handler).sendMessage(formMessage(actionName, args));
					break;
			}
		}

		String formMessage(String eventName, Object[] args) {

			try {
				switch (eventName) {
					case EVENT_NAME_ENTER :
						return args[0] + " " + eventName + "s " + args[1];

					case EVENT_NAME_GIVE :
						return args[0] + " " + eventName + "s " + args[1] + " to " + args[2];

					case EVENT_NAME_STEAL :
						return args[0] + " " + eventName + "s " + args[1] + " from " + args[2];

					case EVENT_NAME_BUY :
						return args[0] + " " + eventName + "s " + args[1] + " for $" + args[2]+ " in " + args[3];

					case EVENT_NAME_OPEN_TRADE :
					case EVENT_NAME_CLOSE_TRADE :
						return args[0] + " " + eventName;

					case EVENT_NAME_NEW_HERO :
						return eventName + ": " + args[0];

					case EVENT_NAME_ANNOUNCE :
						return eventName + ": " + args[0];
					default :
						return "Unhandled event: " + eventName + ", args: " + Arrays.toString(args);
				}
			} catch (IndexOutOfBoundsException e) {
				throw new IllegalArgumentException
						("eventName: " + eventName + ", args: " + Arrays.toString(args), e);
			}
		}
	}

	private EventHandler eHandler;
	private EventHandler nullHandler = new EventHandler();

	public static final String NO_NAME = "noname";
	
	private final String name;
	private Body body = new Body();
	private Location location;
	private Set<Item> stash = new HashSet<>();
	
	private int health = 100;
	private boolean alive = true;
	private int money = 0;
	private boolean trader = false;
	
	public Hero(String name, Location location) {
		this(name, location, null);
	}

	public Hero(String name, Location location, EventHandler handler) {
		this.name = name;
		this.location = location;
		eHandler = (handler == null) ? nullHandler : handler;
		eHandler.handle(EventHandler.EVENT_NAME_NEW_HERO, this);
	}
	
	public String name() {
		return name;
	}
	public String location() {
		return location.toString();
	}

	public void setEventHandler(EventHandler handler) {
		this.eHandler = (handler == null) ? nullHandler : handler;
	}

	public void announce() {
		eHandler.handle(EventHandler.EVENT_NAME_ANNOUNCE, this);
	}

	public void wear(Appearance.Clothes clothes, Appearance.Clothes.BodyPart bodyPart){
		if (clothes.getBodyPart() != bodyPart){
			System.out.println(name + " tried to wear " + clothes + " to "
					+ bodyPart.toString().toLowerCase() + ", but he/she can't!");
			return;
		}
		switch (bodyPart){
			
			case HEAD:
				body.setAppearance(new Appearance(
						clothes,
						body.getAppearance().getTop(),
						body.getAppearance().getBottom()
				));
				break;
				
			case TOP:
				body.setAppearance(new Appearance(
						body.getAppearance().getHead(),
						clothes,
						body.getAppearance().getBottom()
				));
				break;
				
			case BOTTOM:
				body.setAppearance(new Appearance(
						body.getAppearance().getHead(),
						body.getAppearance().getTop(),
						clothes
				));
				break;
		}
	}
	
	public void becomeRich(int money){
		this.money += money;
	}
	public void looseMoney(int money){
		this.money -= money;
	}

	public void openTrade(){
		trader = true;
		eHandler.handle(EventHandler.EVENT_NAME_OPEN_TRADE, this.name());
	}
	public void closeTrade(){
		trader = false;
		eHandler.handle(EventHandler.EVENT_NAME_OPEN_TRADE, this.name() );
	}
	public boolean isTrader(){
		return trader;
	}

	public void steal(Hero target, Item... items) throws ActionInterruptedException {
		for (Item item : items) {
			if (Story.getActionResult(
					new Action(
							this,
							target,
							Action.Intent.STEAL,
							item)
			) == Action.Result.INTERRUPTED)
			throw new ActionInterruptedException(
					"Someone interrupted " + name + " stealing " + item.name() + " from " + target.name()
			);
			target.loose(item);
			stash.add(item);
			eHandler.handle(EventHandler.EVENT_NAME_STEAL, this.name(), item.name(), target.name());
		}
	}
	public void loose(Item... items) {
		for (Item item : items) {
			stash.remove(item);
		}
	}
	public void get(Item... items) {
		Collections.addAll(stash, items);
	}
	public void give(Hero target, Item... items) throws ActionInterruptedException {
		for (Item item : items) {
			if (Story.getActionResult(new Action(this, target, Action.Intent.GIVE, item)) == Action.Result.INTERRUPTED)
				throw new ActionInterruptedException(
						"Someone interrupted " + name + " giving " + item.name() + " to " + target.name()
				);
			this.stash.remove(item);
			target.get(item);
			System.out.println();
			eHandler.handle(EventHandler.EVENT_NAME_GIVE, this.name(), item.name(), target.name());
		}
	}

	public Set<Item> getStashSafely() {
		return new HashSet<>(stash);
	}

	public void heal(int heal) {
		if (alive) {
			health += heal;
			if (health > 100) health = 100;
		}
	}
	public void injure(int damage) {
		health -= damage;
		if (health <= 0) alive = false;
		if (!alive) health = 0;
	}

	public void enter(Location location) {
		this.location = location;
		eHandler.handle(EventHandler.EVENT_NAME_ENTER, this.name(), location);
	}

	public boolean buy(Item item){
		if (location instanceof Shop){
			if (((Shop) location).available(item)){
				eHandler.handle(
						EventHandler.EVENT_NAME_BUY,
						this.name,
						item.name(),
						((Shop) location).getPrice(item),
						location);
				((Shop) location).sell(this, item);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "There is " + name + ". " + body;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Hero hero = (Hero) o;

		return name.equals(hero.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public class Body{
		public final Hero owner = Hero.this;
		private Appearance appearance = new Appearance();
		public Appearance getAppearance(){
			return appearance;
		}
		public void setAppearance(Appearance appearance){
			this.appearance = appearance;
		}
		
		@Override
		public String toString() {
			return appearance.toString();
		}
	}
}
