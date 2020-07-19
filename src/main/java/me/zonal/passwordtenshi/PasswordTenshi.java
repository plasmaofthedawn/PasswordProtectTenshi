package me.zonal.passwordtenshi;

import me.zonal.passwordtenshi.player.*;
import me.zonal.passwordtenshi.database.*;
import me.zonal.passwordtenshi.commands.*;
import me.zonal.passwordtenshi.utils.*;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class PasswordTenshi extends JavaPlugin {

    private final Logger logger = getLogger();

    @Override
    public void onEnable() {

        ConfigFile.initializeConfig(this);
        PlayerSession.initialize(this);
        PlayerStorage.initialize();
        initializeDatabase();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getCommand("register").setExecutor(new CommandRegister(this));
        this.getCommand("unregister").setExecutor(new CommandUnregister(this));
        this.getCommand("login").setExecutor(new CommandLogin(this));
        this.getCommand("resetplayer").setExecutor(new CommandUnregisterPlayer(this));

        org.apache.logging.log4j.core.Logger consoleLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        consoleLogger.addFilter(new LogFilter());
        logger.info("PPTenshi be here to protect your server <3");
    }

    @Override
    public void onDisable(){
        // pass
    }

    public Logger getMainLogger(){
        return logger;
    }

    private void initializeDatabase(){
        Database database = null;
        if (ConfigFile.getBoolean("database.mysql.enable")){
            database = new MySQLdb(ConfigFile.getString("database.mysql.database_host"),
                    ConfigFile.getInt("database.mysql.database_port"),
                    ConfigFile.getString("database.mysql.database_name"),
                    ConfigFile.getString("database.mysql.database_user"),
                    ConfigFile.getString("database.mysql.database_password"));

            if (!database.check()){
                ConfigFile.setBoolean("database.mysql.enable", false);
                logger.info("PPTenshi is falling back to H2 local database, check your MySQL configuration.");
                database = null;
            } else {
                logger.info("PPTenshi has successfully established a connection with your MySQL database and will now use it to store credentials.");
            }
        }  
        
        if (!ConfigFile.getBoolean("database.mysql.enable")){
            Path path = Paths.get(getDataFolder().getAbsolutePath(), ConfigFile.getString("database.h2.database_name"));
            database = new H2db(path.toString(),
                    ConfigFile.getInt("database.mysql.database_port"),
                    ConfigFile.getString("database.h2.database_name"), 
                    ConfigFile.getString("database.h2.database_user"),
                    ConfigFile.getString("database.h2.database_password"));

            if (!database.check()){
                logger.info("PPTenshi has encountered an error when writing and/or reading the H2 local database");
                logger.info("Check your file system permissions and the credentials in the config.yml file.");
                logger.info("The default credentials are 'dontchangeme' and should not be changed after the database is first created.");
                logger.info("Authentication will not be possible until you fix the problem.");
            } else {
                logger.info("PPTenshi has successfully established a connection with your H2 local database and will now use it to store credentials.");
            }
        }
        PlayerSession.setDatabase(database);
    }
}
