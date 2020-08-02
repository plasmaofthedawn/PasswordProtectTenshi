package me.zonal.passwordtenshi.player;

import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.database.*;
import me.zonal.passwordtenshi.utils.ConfigFile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PlayerSession {

    private boolean authorized;
    private GameMode gamemode;
    private final Player player;
    private final UUID uuid;
    private static Database database;
    private static PasswordTenshi pt;
    private static Logger logger;

    public PlayerSession(Player player){
        this.player = player;
        this.gamemode = player.getGameMode();
        this.uuid = player.getUniqueId();
        this.authorized = false;
    }

    public static void setDatabase(Database db){
        database = db;
    }

    public static void initialize(PasswordTenshi ptt){
        pt = ptt;
        logger = pt.getMainLogger();
    }

    public UUID getUUID(){
        return uuid;
    }

    public boolean isAuthorized(){
        if (uuid == null) { return false; }
        return authorized;
    }

    public void setGamemode(GameMode gamemode){
        this.gamemode = gamemode;
    }

    public GameMode getGamemode(){
        return this.gamemode;
    }

    public void setAuthorized(boolean authorized) {
        logger.info(String.format("Set %s authorization to %s", uuid, authorized));
        this.authorized = authorized;
    }

    public String getPasswordHash(){
        return database.getPass(uuid.toString());
    }

    public void setPasswordHash(String hash){
        removePasswordHash();
        database.addPass(uuid.toString(), hash);
    }

    public void removePasswordHash() {
        database.deletePass(uuid.toString());
    }

    public void registerLoginReminder(){
        final boolean registered = getPasswordHash() != null;
        final int[] times = {0};
        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {
            while (player.isOnline()){ 
                if(!isAuthorized()){
                    if (!registered){
                        player.sendMessage(ConfigFile.getLocal("register.first_message"));
                    } else{
                        player.sendMessage(ConfigFile.getLocal("login.first_message"));
                    }
                } else{
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e){
                    //pass
                }
                times[0]++;
                if (times[0] >= 12){
                    Bukkit.getScheduler().runTask(pt, () -> 
                            player.kickPlayer(
                            ConfigFile.getLocal("console.afk_kick")));
                    break;
                }
            }
        });
    }
}