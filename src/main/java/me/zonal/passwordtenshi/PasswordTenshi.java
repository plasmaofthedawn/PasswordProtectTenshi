package me.zonal.passwordtenshi;


import me.zonal.passwordtenshi.database.*;
import me.zonal.passwordtenshi.commands.CommandLogin;
import me.zonal.passwordtenshi.commands.CommandRegister;
import me.zonal.passwordtenshi.commands.CommandUnregister;
import me.zonal.passwordtenshi.commands.CommandUnregisterPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordTenshi extends JavaPlugin {

    ConcurrentHashMap<UUID, Boolean> authentication_map;
    Database database;

    // ConcurrentHashMap<UUID, String> password_map;
    // :OkayuPray: for password_map


    @Override
    public void onEnable() {

        loadConfigFile();
        authentication_map = new ConcurrentHashMap<>();
        FileConfiguration config = this.getConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        this.getCommand("register").setExecutor(new CommandRegister(this));
        this.getCommand("unregister").setExecutor(new CommandUnregister(this));
        this.getCommand("login").setExecutor(new CommandLogin(this));
        this.getCommand("resetplayer").setExecutor(new CommandUnregisterPlayer(this));

        if (config.getBoolean("database.mysql.enable")){
            database = new MySQLdb((String) config.get("database.mysql.dbhost"),
                    (int) config.get("database.mysql.dbport"),
                    (String) config.get("database.mysql.dbname"), 
                    (String) config.get("database.dbuser"),
                    (String) config.get("database.mysql.dbpass"));

            if (!database.check()){
                config.set("database.mysql.enable", false);
                getLogger().info("PPTenshi is falling back to H2 local database, check your MySQL configuration.");
                database = null;
            } else {
                getLogger().info("PPTenshi has successfully established a connection with your MySQL database and will now use it to store credentials.");
            }
        }  
        
        if (!config.getBoolean("database.mysql.enable")){
            Path path = Paths.get(getDataFolder().getAbsolutePath(), config.getString("database.h2.dbname"));
            database = new H2db((String) path.toString(),
                    (int) config.get("database.mysql.dbport"),
                    (String) config.get("database.h2.dbname"), 
                    (String) config.get("database.h2.dbuser"),
                    (String) config.get("database.h2.dbpass"));

            if (!database.check()){
                getLogger().info("PPTenshi has encountered an error when writing and/or reading the H2 local database");
                getLogger().info("Check your file system permissions and the credentials in the config.yml file.");
                getLogger().info("The default credentials are 'dontchangeme' and should not be changed after the database is first created.");
                getLogger().info("Authentication will not be possible until you fix the problem.");
            } else {
                getLogger().info("PPTenshi has successfully established a connection with your H2 local database and will now use it to store credentials.");
            }
        }

        getLogger().info("PPTenshi be here to protect your server <3");
    }

    public void loadConfigFile() {

        this.getConfig().addDefault("database.mysql.enable", false);
        this.getConfig().addDefault("database.mysql.dbhost", "myhostdb");
        this.getConfig().addDefault("database.mysql.dbport", 3306);
        this.getConfig().addDefault("database.mysql.dbname", "mypasswordb");
        this.getConfig().addDefault("database.mysql.dbuser", "mydbuser");
        this.getConfig().addDefault("database.mysql.dbpass", "mydbpass");
        this.getConfig().addDefault("database.h2.dbname", "pass");
        this.getConfig().addDefault("database.h2.dbuser", "dontchangeme");
        this.getConfig().addDefault("database.h2.dbpass", "dontchangeme");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

     }

    @Override
    public void onDisable(){
        // pass
    }

    public boolean isAuthorized(UUID uuid){
        return authentication_map.get(uuid);
    }

    public void setAuthorized(UUID uuid, boolean authorized) {
        getLogger().info(String.format("Set %s authorization to %s", uuid, authorized));
        authentication_map.put(uuid, authorized);
    }

    public String getPasswordHash(UUID uuid){
        return database.getPass(uuid.toString());
    }

    public void setPasswordHash(UUID uuid, String hash){
        removePasswordHash(uuid);
        database.addPass(uuid.toString(), hash);
    }

    public void removePasswordHash(UUID uuid) {
        database.deletePass(uuid.toString());
    }
}
