import com.dartin.util.Item;
import com.sun.rowset.CachedRowSetImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin on 20.06.2017.
 */
public class Main {

    public static void main(String[] args) {

    }

    private Set<Item> getSetItemsFromBase(){
        DataBaseManager dbm = new DataBaseManager();
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
}
