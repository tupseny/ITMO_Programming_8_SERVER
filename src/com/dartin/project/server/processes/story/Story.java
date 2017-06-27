package com.dartin.project.server.processes.story;


import com.dartin.project.server.Logger;
import com.dartin.project.server.data.DatabaseManager;
import com.dartin.project.server.data.RequestManager;
import com.dartin.project.server.processes.story.resources.*;
import com.dartin.util.Item;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniil Yurkov and Martin Sikora on 16.11.2016.
 */
public class Story extends Thread {

	private Logger l;
	private Hero.EventHandler handler;
	private static HashMap<Action, Action.Result> actionMap = new HashMap<>();
	private static Action stealHatAction;
	private static Action stealConvertAction;
	private static Action giveHatAction;
	private static Action giveConvertAction;
	
	public static final String FIRST_HERO_NAME = "Neznaika";
	public static final String SECOND_HERO_NAME = "Julio";
	
	public static final Item hat = new Item("Hat", "Wearable", Item.Size.NORMAL, LocalDate.now());
	public static final Item convert = new Item("Convert", "Readable", Item.Size.SMALL, LocalDate.now());
	public static final Item gun = new Item("Bourbon", "7-charge revolver. One shoot - one kill...", Item.Size.SMALL , LocalDate.now()   );
	public static final int GUN_PRICE = 42;

	public static Shop shop = new Shop(new HashMap<>(), "Default shop");
	
	public static Hero neznaika = new Hero(
			FIRST_HERO_NAME,
			shop);
	
	public static final Hero julio = new Hero(
			SECOND_HERO_NAME,
			shop);
	
	public static final Hero victim = new Hero(
			"victim",
			shop);
	
	public static final Hero noname = new Hero(
			Hero.NO_NAME,
			Location.UNIVERSE){{
				becomeRich(100500);
				wear(
						new Appearance.Clothes("cap", "checked" , Appearance.Clothes.BodyPart.HEAD),
						Appearance.Clothes.BodyPart.HEAD
				);
				wear(
						new Appearance.Clothes("top", "grey", Appearance.Clothes.BodyPart.TOP),
						Appearance.Clothes.BodyPart.TOP
				);
				wear(
						new Appearance.Clothes("trousers, which make movements harder", "checked", Appearance.Clothes.BodyPart.BOTTOM),
						Appearance.Clothes.BodyPart.BOTTOM
				);
	}};
	
	static {
		loadCollection(neznaika);
		stealHatAction = new Action(neznaika, victim, Action.Intent.STEAL, hat);
		stealConvertAction = new Action(neznaika, victim, Action.Intent.STEAL, convert);
		giveHatAction = new Action(neznaika, julio, Action.Intent.GIVE, hat);
		giveConvertAction = new Action(neznaika, julio, Action.Intent.GIVE, convert);
		victim.get(hat, convert);
		shop.getWithPrice(gun, GUN_PRICE);
		actionMap.put(
				stealHatAction,
				Action.Result.SUCCESSFUL
		);
		actionMap.put(
				stealConvertAction,
				Action.Result.SUCCESSFUL
		);
		actionMap.put(
				giveHatAction,
				Action.Result.INTERRUPTED
		);
		actionMap.put(
				giveConvertAction,
				Action.Result.INTERRUPTED
		);

	}
	
	public static Action.Result getActionResult(Action action) {
		return actionMap.get(action);
	}

	public Story() {
		l = null;
		init();
	}

	public Story(Logger logger) {
		l = logger;
		init();
	}

	private void init() {
		handler = new Hero.EventHandler(l);
		neznaika.setEventHandler(handler);
		noname.setEventHandler(handler);
	}

	@Override
	public void run() {
		try {
			main();
		} catch (Exception e) {
			System.out.println("Story run failed!");
			e.printStackTrace();
		}
	}

	private void main() throws Exception{

		l.inf("Story has been started");
		try {
			neznaika.steal(victim, hat, convert);
			neznaika.give(julio, hat, convert);
		} catch (ActionInterruptedException e) {
			l.err("ActionInterruptedException caught:\n" + e.toString());
			noname.announce();
			noname.enter(shop);
			noname.openTrade();
			noname.buy(gun);
			noname.closeTrade();
			noname.enter(Location.UNIVERSE);

			Stashable can = new Stashable() {

				class TinyItem extends Item{

					public TinyItem(Item item) throws ClassCastException{
						if (item.size.larger(Size.TINY))
							throw new ClassCastException("Cannot cast " + this + " to " + getClass());
					}
				}

				Set<TinyItem> stash = new HashSet<>();

				@Override
				public void get(Item... items) {
					for (Item item : items){
						stash.add(new TinyItem(item));
					}
				}

				@Override
				public void loose(Item... items) {
					for (Item item : items){
						stash.remove(new TinyItem(item));
					}
				}
			};
		}
		finishStory(neznaika);
		onStop();
	}

	private static void loadCollection(Hero hero) {
		RequestManager.getSetItemsFromBase().forEach(hero::get);
	}

	private static void finishStory(Hero hero) {
		RequestManager.commitCollection(hero.getStashSafely());
	}

	protected void onStop() throws Exception{};

}