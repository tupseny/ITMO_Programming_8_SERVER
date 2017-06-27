package com.dartin.project.server.data;

import com.dartin.util.Item;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


public class RequestManager {

	public static Set<Item> getSetItemsFromBase(){
		DatabaseManager dbm = DatabaseManager.getInstance();
		Set<Item> setItems;

		if(!dbm.execQuery("select * from item")){
			System.exit(1);
		}

		try{
			CachedRowSetImpl crs = dbm.getRowSet();
			setItems = new HashSet<>();

			while(crs.next()){
				setItems.add(
						new Item(crs.getString("name"),
								crs.getString("usage"),
								crs.getString("size"),
								LocalDate.now())
				);
			}
			return setItems;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean commitCollection(Set<Item> newItems){
		DatabaseManager dm = DatabaseManager.getInstance();
		Set<Item> oldItems = getSetItemsFromBase();
		StringBuilder str = new StringBuilder();

		if(oldItems.equals(newItems)){
			System.out.println("Collection is up to date");
			return false;
		}else{
			str.append("begin;");

			newItems.forEach(item -> {
				if (!oldItems.contains(item)){
					str.append(genInsert(item));
				}
			});

			oldItems.forEach(item -> {
				if (!newItems.contains(item)){
					str.append(genDelete(item));
				}
			});

			str.append("commit;");

			try {
				dm.execUpdate(str.toString());
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	public static Set<Item> commitItem(Item item, boolean signal) throws SQLException {
		DatabaseManager dm = DatabaseManager.getInstance();
		StringBuilder str = new StringBuilder();

		str.append("begin;");
		if (signal){ //insert
			str.append(genInsert(item));
			str.append("commit;");
			dm.execUpdate(str.toString());
			return getSetItemsFromBase();
		}else{//delete
			str.append(genDelete(item));
			str.append("commit;");
			if (dm.execUpdate(str.toString()) != 0){
				return getSetItemsFromBase();
			}
			throw new SQLException(item.toString() + " is not exists");
		}
	}

	public static Set<Item> insertItem(Item item) throws SQLException{
		return commitItem(item, true);
	}

	public static Set<Item> removeItem(Item item) throws SQLException{
		return commitItem(item, false);
	}

	public static Set<Item> removeLower(Item than) {

		Set<Item> currentSet = getSetItemsFromBase();
		assert currentSet != null;
		currentSet.forEach(item -> {
			if (item.size().smaller(than.size)) {
				try {
					removeItem(item);
					currentSet.remove(item);
				} catch (SQLException e) {
					System.out.println("Unable to remove " + item);
					e.printStackTrace();
				}
			}
		});
		return currentSet;
	}

	private static String genInsert(Item item){
		StringBuilder str = new StringBuilder();

		str.append("insert into item(name, usage, size, date) values ('");
		str.append(item.name());
		str.append("','");
		str.append(item.usage());
		str.append("','");
		str.append(item.size().toString());
		str.append("','");
		str.append(item.date().toString());
		str.append("');");
		System.out.println("insert: " + item.toString());
		return str.toString();
	}

	private static String genDelete(Item item){
		StringBuilder str = new StringBuilder();

		str.append("delete from item where (name='");
		str.append(item.name());
		str.append("' and usage='");
		str.append(item.usage());
		str.append("' and size='");
		str.append(item.size().toString());
		str.append("' and date='");
		str.append(item.date().toString());
		str.append("');");
		System.out.println("delete: " + item.toString());
		return str.toString();
	}
}
