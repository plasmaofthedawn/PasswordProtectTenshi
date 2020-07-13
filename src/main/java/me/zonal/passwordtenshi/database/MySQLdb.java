package me.zonal.passwordtenshi.database;

import org.apache.commons.dbcp.BasicDataSource;
import java.sql.*;

public class MySQLdb implements Database{

    private Connection connection;
    private ResultSet resultset;
    private Statement statement;
    private final String database;
    private final BasicDataSource credentials;

    public MySQLdb (String hostip, int port, String database, String user, String pass) {
        credentials = new BasicDataSource();
        credentials.setUrl("jdbc:mysql://" + hostip + ":" + port + "/" + database);
        credentials.setUsername(user);
        credentials.setPassword(pass);
        credentials.addConnectionProperty("autoReconnect", "true");
        credentials.addConnectionProperty("useSSL", "false");
        this.database = database;
    }

    public boolean check() {
        boolean result = false;
        final StringBuilder tablereq = new StringBuilder("CREATE TABLE IF NOT EXISTS users (");
                            tablereq.append("`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");
                            tablereq.append("`uuid` VARCHAR(128) NOT NULL,");
                            tablereq.append("`password` VARCHAR(512) NOT NULL)");

        try {
            //check if the database exists inside the server as we cant create it ourselves
            connection = credentials.getConnection();
            resultset = connection.getMetaData().getCatalogs();
            while (resultset.next()) {
                String catalogs = resultset.getString(1);
                if (database.equals(catalogs)) {
                    result = true;
                }
            }
            statement = connection.createStatement();
            statement.executeUpdate(tablereq.toString());
        } catch(Exception ex){
            ex.printStackTrace();
            result = false;
        }
        closeSQL();
        return result;
    }

    public boolean addPass(String playeruuid, String password){
        boolean result = false;
        final String insertquery = "INSERT INTO users (uuid, password) VALUES ('"+playeruuid+"', '"+password+"');";

        try {
            connection = credentials.getConnection();
            statement = connection.createStatement();
            int response = statement.executeUpdate(insertquery);
            if (response > 0){
                result = true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        closeSQL();
        return result;
    }

    public boolean deletePass(String playeruuid){
        boolean result = false;
        final String delquery = "DELETE FROM users WHERE uuid = "+"'"+playeruuid+"'";

        try {
            connection = credentials.getConnection();
            statement = connection.createStatement();
            int response = statement.executeUpdate(delquery);
            if (response > 0){
                result = true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        closeSQL();
        return result;
    }

    public String getPass(String playeruuid){
        String retrievedpass = null;
        final String getquery = "SELECT password FROM users WHERE uuid = "+"'"+playeruuid+"'";

        try {
            connection = credentials.getConnection();
            statement = connection.createStatement();
            resultset = statement.executeQuery(getquery);
            // if the user has not made a password yet
            if (!resultset.next()) {
                closeSQL();
                return null;
            }
            retrievedpass = resultset.getString("password");
        } catch(Exception ex){
            ex.printStackTrace();
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