package me.zonal.passwordtenshi;


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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordTenshi extends JavaPlugin {

    ConcurrentHashMap<UUID, Boolean> authentication_map;
    ConcurrentHashMap<UUID, Integer> repeat_task_id;
    Database database;
    private final ConfigFile config = new ConfigFile(this);

    // ConcurrentHashMap<UUID, String> password_map;
    // :OkayuPray: for password_map


    @Override
    public void onEnable() {

        config.initializeConfig();
        authentication_map = new ConcurrentHashMap<>();
        repeat_task_id = new ConcurrentHashMap<>();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        this.getCommand("register").setExecutor(new CommandRegister(this));
        this.getCommand("unregister").setExecutor(new CommandUnregister(this));
        this.getCommand("login").setExecutor(new CommandLogin(this));
        this.getCommand("resetplayer").setExecutor(new CommandUnregisterPlayer(this));

        if (config.getBoolean("database.mysql.enable")){
            database = new MySQLdb(config.getString("database.mysql.database_host"),
                    config.getInt("database.mysql.database_port"),
                    config.getString("database.mysql.database_name"),
                    config.getString("database.mysql.database_user"),
                    config.getString("database.mysql.database_password"));

            if (!database.check()){
                config.setBoolean("database.mysql.enable", false);
                getLogger().info("PPTenshi is falling back to H2 local database, check your MySQL configuration.");
                database = null;
            } else {
                getLogger().info("PPTenshi has successfully established a connection with your MySQL database and will now use it to store credentials.");
            }
        }  
        
        if (!config.getBoolean("database.mysql.enable")){
            Path path = Paths.get(getDataFolder().getAbsolutePath(), config.getString("database.h2.database_name"));
            database = new H2db(path.toString(),
                    config.getInt("database.mysql.database_port"),
                    config.getString("database.h2.database_name"), 
                    config.getString("database.h2.database_user"),
                    config.getString("database.h2.database_password"));

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
            player.sendMessage(config.getLocal("register.first_message"));
        } else {
            player.sendMessage(config.getLocal("login.first_message"));
        }

        final int[] times = {0};

        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

            if (player.isOnline() && !isAuthorized(uuid)) {
                if (!registered) {
                    player.sendMessage(config.getLocal("register.first_message"));
                } else {
                    player.sendMessage(config.getLocal("login.first_message"));
                }
            } else {
                Bukkit.getScheduler().cancelTask(repeat_task_id.get(uuid));
                repeat_task_id.remove(uuid);
            }

            times[0]++;

            if (times[0] > 12) {
                player.kickPlayer(config.getLocal("console.afk_kick"));
            }

        }, 0L, 200L);

        repeat_task_id.put(uuid, id);

    }
}
