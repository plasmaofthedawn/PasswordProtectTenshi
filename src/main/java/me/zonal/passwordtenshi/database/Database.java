package me.zonal.passwordtenshi.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcConnectionPool;

public class Database{

    private final JdbcConnectionPool credentials;
    private Connection connection;
    private ResultSet resultset;
    private Statement statement;

    public Database (String hostip, String user, String pass) {
        credentials = JdbcConnectionPool.create(
                "jdbc:h2:file:"
                +hostip
                +";IFEXISTS=FALSE"
                , user
                , pass);
    }

    public boolean check() {
        final StringBuilder tablereq 
                = new StringBuilder("CREATE TABLE IF NOT EXISTS users (");
                tablereq.append("`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
                tablereq.append("`uuid` VARCHAR(128) NOT NULL,");
                tablereq.append("`password` VARCHAR(512) NOT NULL)");

        try {
            this.connection = credentials.getConnection();
            this.statement = connection.createStatement();
            this.statement.executeUpdate(tablereq.toString());
            return true;
        } catch(SQLException ex){
            return false;
        }
    }

    public boolean addPass(String playeruuid, String password){
        boolean result = false;
        final String insertquery = "INSERT INTO users (uuid, password) "
                + "VALUES ('"
                +playeruuid
                +"', '"
                +password
                +"');";

        try {
            connection = credentials.getConnection(); 
            statement = connection.createStatement();
            int response = statement.executeUpdate(insertquery);
            if (response > 0){
                result = true;
            }
        } catch(SQLException ex){
            //ignore for now
        }
        
        closeSQL();
        return result;
    }

    public boolean deletePass(String playeruuid){
        boolean result = false;
        final String delquery = "DELETE FROM users WHERE uuid = "+"'"
                +playeruuid
                +"'";

        try {
            connection = credentials.getConnection(); 
            statement = connection.createStatement();
            int response = statement.executeUpdate(delquery);
            if (response > 0){
                result = true;
            }
        } catch(SQLException ex){
            //ignore for now.
            //TODO RAISE WARNING
        }
        closeSQL();
        return result;
    }

    public String getPass(String playeruuid){
        String retrievedpass = null;
        final String getQuery = "SELECT password FROM users WHERE uuid = "+"'"
                +playeruuid
                +"'";

        try {
            connection = credentials.getConnection();
            statement = connection.createStatement();
            resultset = statement.executeQuery(getQuery);
            // if the user has not made a password yet
            if (!resultset.next()) {
                closeSQL();
                return null;
            }
            retrievedpass = resultset.getString("password");
        } catch(SQLException ex){

        }
        closeSQL();
        return retrievedpass;
    }

    private void closeSQL(){
        try {
            if( connection != null){
                try{
                    connection.close();
                }
                catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
            if( statement != null){
                try{
                    statement.close();
                }
                catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
            if( resultset != null){
                try{
                    resultset.close();
                }
                catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
} 