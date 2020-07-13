package me.zonal.passwordtenshi;


import me.zonal.passwordtenshi.database.*;
import me.zonal.passwordtenshi.commands.CommandLogin;
import me.zonal.passwordtenshi.commands.CommandRegister;
import me.zonal.passwordtenshi.commands.CommandUnregister;
import me.zonal.passwordtenshi.commands.CommandUnregisterPlayer;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordTenshi extends JavaPlugin {

    ConcurrentHashMap<UUID, Boolean> authentication_map;
    ConcurrentHashMap<UUID, Integer> repeat_task_id;
    Database database;

    // ConcurrentHashMap<UUID, String> password_map;
    // :OkayuPray: for password_map


    @Override
    public void onEnable() {

        loadConfigFile();
        authentication_map = new ConcurrentHashMap<>();
        repeat_task_id = new ConcurrentHashMap<>();
        FileConfiguration config = this.getConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        this.getCommand("register").setExecutor(new CommandRegister(this));
        this.getCommand("unregister").setExecutor(new CommandUnregister(this));
        this.getCommand("login").setExecutor(new CommandLogin(this));
        this.getCommand("resetplayer").setExecutor(new CommandUnregisterPlayer(this));

        if (config.getBoolean("database.mysql.enable")){
            database = new MySQLdb(config.getString("database.mysql.dbhost"),
                    (int) config.get("database.mysql.dbport"),
                    config.getString("database.mysql.dbname"),
                    config.getString("database.dbuser"),
                    config.getString("database.mysql.dbpass"));

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
                    (String) config.getString("database.h2.dbname"), 
                    (String) config.getString("database.h2.dbuser"),
                    (String) config.getString("database.h2.dbpass"));

            if (!database.check()){
                getLogger().info("PPTenshi has encountered an error when writing and/or reading the H2 local database");
                getLogger().info("Check your file system permissions and the credentials in the config.yml file.");
                getLogger().info("The default credentials are 'dontchangeme' and should not be changed after the database is first created.");
                getLogger().info("Authentication will not be possible until you fix the problem.");
            } else {
                getLogger().info("PPTenshi has successfully established a connection with your H2 local database and will now use it to store credentials.");
            }
        }
        org.apache.logging.log4j.core.Logger consoleLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        consoleLogger.addFilter(new LogFilter());
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
        if (uuid == null) { return false; }
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
    
    public void sendRegisterLoginSpam(Player player) {

        boolean registered = getPasswordHash(player.getUniqueId()) != null;
        UUID uuid = player.getUniqueId();

        if (!registered) {
            player.sendMessage("§bPPTenshi says§r: register using /register <password> you baka~");
        } else {
            player.sendMessage("§bPPTenshi says§r: login using /login <password> you baka~");
        }

        final int[] times = {0};

        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

            if (player.isOnline() && !isAuthorized(uuid)) {
                if (!registered) {
                    player.sendMessage("§bPPTenshi says§r: register using /register <password> you baka~");
                } else {
                    player.sendMessage("§bPPTenshi says§r: login using /login <password> you baka~");
                }
            } else {
                Bukkit.getScheduler().cancelTask(repeat_task_id.get(uuid));
                repeat_task_id.remove(uuid);
            }

            times[0]++;

            if (times[0] > 12) {
                player.kickPlayer("§bPPTenshi says§r: you've been logged out for too long");
            }

        }, 0L, 200L);

        repeat_task_id.put(uuid, id);

    }
}
