import com.dartin.util.Item;
import com.sun.rowset.CachedRowSetImpl;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martin on 20.06.2017.
 */
public class ResponseManager {
    public static void response(DatagramPacket packet) throws SocketException, UnknownHostException {

        int msg = Integer.parseInt(packet.getData().toString());

        switch (msg){
            case 0:
                new Thread(new Sender(getSetItemsFromBase().toString()));
                break;
        }

        //new Thread(new Sender(packet, "ok")).start();
    }

    private static Set<Item> getSetItemsFromBase(){
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
