package com.dartin.project.server.data;

import com.sun.rowset.CachedRowSetImpl;

import javax.jws.Oneway;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.sql.*;

public class DatabaseManager {

	private String url;
	private CachedRowSetImpl crs;
	private static volatile DatabaseManager instance = new DatabaseManager();

	public static DatabaseManager getInstance() {
		return instance;
	}

	public DatabaseManager(String url, String user, String pass){
		this.url = "jdbc:postgresql://" + url + "/postgres?user=" +
				user + "&password=" + pass;
	}

	private DatabaseManager(){
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

//    public boolean commitToDataBase(CachedRowSetImpl crs){
//        Connection con;
//
//        try{
//            Class.forName("org.postgresql.Driver");
//            con = DriverManager.getConnection(url);
//            crs.acceptChanges(con);
//            con.close();
//            return true;
//
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

	public int execUpdate(String sql) throws SQLException {
		Connection con;

		try{
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url);
			Statement stmt = con.createStatement();
			int res = stmt.executeUpdate(sql);
			con.close();
			return res;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void createTable(Object o){
        String tableName = o.getClass().getSimpleName().toLowerCase();
        DatabaseManager dm = DatabaseManager.getInstance();
        StringBuilder str = new StringBuilder();
        Method[] methods = o.getClass().getMethods();

        str.append("begin;");
        str.append("CREATE TABLE if NOT EXISTS ");
        str.append(tableName);
        str.append("();");
        for (Method m :
                methods) {
            String methodName = m.getName();
            if (m.getReturnType() == String.class &&
                    methodName.startsWith("get")){
                str.append("alter table ");
                str.append(tableName);
                str.append(" add ");
                str.append(methodName.substring(3).toLowerCase());
                str.append(" text;");
            }
        }
        str.append("commit;");

        try {
            dm.execUpdate(str.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
