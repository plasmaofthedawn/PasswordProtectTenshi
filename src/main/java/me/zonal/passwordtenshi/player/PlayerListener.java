package me.zonal.passwordtenshi.player;

import me.zonal.passwordtenshi.utils.ConfigFile;
import me.zonal.passwordtenshi.PasswordTenshi;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Arrays;
import java.util.List;

public class PlayerListener implements Listener {

    final PasswordTenshi pt;
    private final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "/login", 
            "/register");

    public PlayerListener(PasswordTenshi pt){
        this.pt = pt;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        // Check if the player already is online (and kick the current one)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(event.getName())) {
                boolean authorized 
                        = PlayerStorage
                        .getPlayerSession(player.getUniqueId())
                        .isAuthorized();
                
                if (authorized) {
                    event.setLoginResult(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage(
                            ConfigFile.getLocal("console.already_online_kick"));
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove the player from the sessions because he's being yeeted
        PlayerSession playerSession 
                = PlayerStorage
                .getPlayerSession(
                event.getPlayer()
                .getUniqueId());

        if (!playerSession.isAuthorized()){
            event.getPlayer().setGameMode(playerSession.getGamemode());
        }
        
        PlayerStorage.removePlayerSession(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        
        final Player player = Bukkit.getPlayer(event.getPlayer().getName());
        final PlayerSession playersession = new PlayerSession(player);
        player.setGameMode(GameMode.SPECTATOR);
        playersession.registerLoginReminder(); //start the spam
        PlayerStorage.addPlayerSession(playersession); //store in playerstorage
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        
        if(PlayerStorage.getPlayerSession(
                event.getPlayer().getUniqueId()).isAuthorized()) return;

        final String message = event.getMessage().toLowerCase();
        for(String cmd : ALLOWED_COMMANDS) {
            if(message.startsWith(cmd)) {
                return;
            }
        }

        // Cancel the command otherwise
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        defaultEventAction(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        if(PlayerStorage.getPlayerSession(
                event.getPlayer().getUniqueId()).isAuthorized()) return;
        
        event.setCancelled(true);
//        final Location from = event.getFrom();
//        final Location to = event.getTo();
//        assert to != null;
//        if(from.getBlockX() != to.getBlockX() 
//                || from.getBlockY() != to.getBlockY() 
//                || from.getBlockZ() != to.getBlockZ()) {
//            
//            event.setTo(event.getFrom());
//        }

    }

    private void defaultEventAction(PlayerEvent event) {
        if(!(event instanceof Cancellable)) {
            throw new IllegalArgumentException("Event cannot be cancelled!");
        }
        if(isInvalidPlayer(event.getPlayer())) return;
        if(PlayerStorage.getPlayerSession(
                event.getPlayer().getUniqueId()).isAuthorized()) return;

        ((Cancellable) event).setCancelled(true);
    }


    private boolean isInvalidPlayer(HumanEntity human) {
        if(!(human instanceof Player)) return true;
        final Player player = (Player) human;
        return player.hasMetadata("NPC") || !player.isOnline();
    }
}