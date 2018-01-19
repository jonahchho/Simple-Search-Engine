import java.sql.*;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class DB {

    public Connection getConnection() throws SQLException
    {
        SQLiteConfig config = new SQLiteConfig();
        // config.setReadOnly(true);   
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
    
            
        SQLiteDataSource ds = new SQLiteDataSource(config); 
        ds.setUrl("jdbc:sqlite:./Database/store.db");
        return ds.getConnection();
        //ds.setServerName("sample.db");
 
        
    }
    //create Table
    public void createTable(Connection con, String tablename)throws SQLException{
        String sql = "DROP TABLE IF EXISTS " + tablename + " ;create table " + tablename  + " ( docID integer, word string, TF float ) ;";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        
    }
    public void createTable2(Connection con, String tablename)throws SQLException{
        String sql = "DROP TABLE IF EXISTS " + tablename + " ;create table " + tablename  + " ( docID integer, termNum integer ) ;";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
        
    }
    //drop table
    public void dropTable(Connection con, String tablename)throws SQLException{
        String sql = "drop table " + tablename + " ;";
        Statement stat = null;
        stat = con.createStatement();
        stat.executeUpdate(sql);
    }
	
}
