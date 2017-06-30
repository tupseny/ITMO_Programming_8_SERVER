package com.dartin.project.server.data;

import com.sun.rowset.CachedRowSetImpl;

import javax.jws.Oneway;
import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

	public static boolean createTable(Object o){
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
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean dropTable(Object o){
	    DatabaseManager dm = new DatabaseManager();

	    StringBuilder str = new StringBuilder();
	    str.append("begin;");
	    str.append("drop table ");
	    str.append(o.getClass().getSimpleName().toLowerCase());
	    str.append(";");
	    str.append("commit;");

        try {
            dm.execUpdate(str.toString());
            return true;
        } catch (SQLException e) {
            System.out.println("");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertTable(Object o){
        Method[] methods = o.getClass().getMethods();
        DatabaseManager dm = new DatabaseManager();
        StringBuilder str = new StringBuilder();
        String className = o.getClass().getSimpleName();

        str.append("begin;");

        str.append("insert into ");
        str.append(className);
        str.append("(");

        for (int i=0; i<methods.length;i++){
            String mName = methods[i].getName();
            if (mName.startsWith("get") &&
                    methods[i].getReturnType() == String.class) {
                str.append(mName.substring(3).toLowerCase());
                str.append(",");
            }
        }
        str.deleteCharAt(str.length()-1);
        str.append(") values('");


        for (int i=0; i<methods.length; i++) {
            String mName = methods[i].getName();
            if (mName.startsWith("get") &&
                    methods[i].getReturnType() == String.class) {
                try {
                    str.append(methods[i].invoke(o));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                str.append("','");
            }
        }
        str.delete(str.length()-3, str.length()-1);
        str.append(");");

        str.append("commit;");

//        System.out.println(str.toString());
//        return true;
        try {
            dm.execUpdate(str.toString());
            return true;
        } catch (SQLException e) {
            System.out.println();
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteTFromTable(Object o){
        DatabaseManager dm = new DatabaseManager();
        StringBuilder str = new StringBuilder();
        String className = o.getClass().getSimpleName();
        Method[] methods = o.getClass().getMethods();

        str.append("begin;");
        str.append("delete from ");
        str.append(className);
        str.append(" where ");
        for (Method method:
             methods) {
            String mName = method.getName();
            if (mName.startsWith("get") &&
                    method.getReturnType() == String.class) {
                str.append(mName.substring(3).toLowerCase());
                str.append("='");
                try {
                    str.append(method.invoke(o));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                str.append("' and ");
            }
        }
        str.delete(str.length()-5, str.length());
        str.append(";");
        str.append("commit;");

//        System.out.println(str.toString());
//        return true;
        try {
            dm.execUpdate(str.toString());
            return true;
        } catch (SQLException e) {
            System.out.println(" ");
            e.printStackTrace();
            return false;
        }
    }

}
