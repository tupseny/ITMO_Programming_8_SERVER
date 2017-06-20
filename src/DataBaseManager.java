/**
 * Created by Martin on 16.06.2017.
 */
import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;

public class DataBaseManager {

    private String url;
    private CachedRowSetImpl crs;

    public DataBaseManager(String url, String user, String pass){
        this.url = "jdbc:postgresql://" + url + "/postgres?user=" +
                user + "&password=" + pass;
    }

    public DataBaseManager(){
        this("localhost", "postgres", "postgres");
    }

    public boolean execQuery(String sql){
        Statement stmt;
        Connection con;
        ResultSet rs;

        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url);
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            crs = new CachedRowSetImpl();
            crs.populate(rs);

            con.close();

            return true;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CachedRowSetImpl getRowSet(){
        return crs;
    }

    public boolean commitToDataBase(CachedRowSetImpl crs){
        Connection con;

        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url);
            crs.acceptChanges(con);
            con.close();
            return true;


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean execUpdate(String sql){
        Connection con;

        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.close();
            return true;


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
