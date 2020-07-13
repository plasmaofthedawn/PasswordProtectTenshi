package me.zonal.passwordtenshi.database;

import java.sql.*;

public interface Database {
    
    boolean check();

    boolean addPass(String playeruuid, String password);

    boolean deletePass(String playeruuid);

    String getPass(String playeruuid);

}