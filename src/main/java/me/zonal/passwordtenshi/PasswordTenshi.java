package me.zonal.passwordtenshi;

import me.zonal.passwordtenshi.commands.CommandLogin;
import me.zonal.passwordtenshi.commands.CommandRegister;
import me.zonal.passwordtenshi.commands.CommandUnregister;
import me.zonal.passwordtenshi.commands.CommandUnregisterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordTenshi extends JavaPlugin {

    ConcurrentHashMap<UUID, Boolean> authentication_map;
    MySQL database;

    ConcurrentHashMap<UUID, Integer> repeat_task_id;

    // ConcurrentHashMap<UUID, String> password_map;
    // :OkayuPray: for password_map


    @Override
    public void onEnable() {

        loadConfigFile();
        authentication_map = new ConcurrentHashMap<>();
        repeat_task_id = new ConcurrentHashMap<>();

        FileConfiguration config = this.getConfig();

        database = new MySQL((String) config.get("database.dbhost"),
                (int) config.get("database.dbport"),
                (String) config.get("database.dbname"), (String) config.get("database.dbuser"),
                (String) config.get("database.dbpass"));

        database.check();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        this.getCommand("register").setExecutor(new CommandRegister(this));
        this.getCommand("unregister").setExecutor(new CommandUnregister(this));
        this.getCommand("login").setExecutor(new CommandLogin(this));
        this.getCommand("resetplayer").setExecutor(new CommandUnregisterPlayer(this));

        getLogger().info("PPTenshi be here to protect your server <3");
    }

    public void loadConfigFile() {

        this.getConfig().addDefault("workaround.portal", true);
        this.getConfig().addDefault("database.sql", false);
        this.getConfig().addDefault("database.dbhost", "myhostdb");
        this.getConfig().addDefault("database.dbport", "3306");
        this.getConfig().addDefault("database.dbname", "mypasswordb");
        this.getConfig().addDefault("database.dbuser", "mydbuser");
        this.getConfig().addDefault("database.dbpass", "mydbpass");
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
        return database.getpass(uuid.toString());
    }

    public void setPasswordHash(UUID uuid, String hash){
        removePasswordHash(uuid);
        database.addpass(uuid.toString(), hash);
    }

    public void removePasswordHash(UUID uuid) {
        database.deletepass(uuid.toString());
    }

    public void sendRegisterLoginSpam(Player player) {

        boolean registered = getPasswordHash(player.getUniqueId()) != null;

        if (!registered) {
            player.sendMessage("§bPPTenshi says§r: register using /register <password> you baka~");
        } else {
            player.sendMessage("§bPPTenshi says§r: login using /login <password> you baka~");
        }

        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

            if (!isAuthorized(player.getUniqueId())) {
                if (!registered) {
                    player.sendMessage("§bPPTenshi says§r: register using /register <password> you baka~");
                } else {
                    player.sendMessage("§bPPTenshi says§r: login using /login <password> you baka~");
                }
            } else {
                Bukkit.getScheduler().cancelTask(repeat_task_id.get(player.getUniqueId()));
                repeat_task_id.remove(player.getUniqueId());
            }
        }, 0L, 100L);

        repeat_task_id.put(player.getUniqueId(), id);

    }
}
