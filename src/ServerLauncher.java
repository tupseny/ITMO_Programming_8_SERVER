import com.dartin.util.Item;
import com.sun.rowset.CachedRowSetImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ServerLauncher {

    public static void main(String[] args) {
        try{
            runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private static void runServer() throws IOException {
        new Thread(new Server()).start();
    }
}
