/**
 * Created by Martin on 16.06.2017.
 */
import javax.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.dartin.util.*;
import org.postgresql.jdbc.PgResultSet;

import javax.sql.rowset.CachedRowSet;

public class DataBaseManager {

    private String URL_ADDRESS = "localhost";
    private String USER_NAME = "postgres";
    private String PASSWORD = "postgres";
    private String STATEMENT = "select name, usage, size, date from item";

    public static void main(String[] args) {
        CachedRowSet
    }

}
