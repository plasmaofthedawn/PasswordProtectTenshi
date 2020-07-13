package me.zonal.passwordtenshi;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    final PasswordTenshi pt;
    private final ArrayList<String> ALLOWED_COMMANDS;

    public PlayerListener(PasswordTenshi pt){
        this.pt = pt;

        // kinda stupid but it should work
        ALLOWED_COMMANDS = new ArrayList<>();
        ALLOWED_COMMANDS.add("/login ");
        ALLOWED_COMMANDS.add("/register ");
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        // Check if the player already is online (and kick the current one)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(event.getName())) {
                boolean authorized = this.pt.authentication_map.get(player.getUniqueId());
                if (authorized) {
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage("§bPPTenshi says§r: bruh you're already online");
                    return;
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove the player from the sessions because he's being yeeted
        this.pt.authentication_map.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        if(isInvalidPlayer(player)) return;

        // start the session for the player
        this.pt.authentication_map.put(player.getUniqueId(), false);

        pt.sendRegisterLoginSpam(player);

        // final LoginSecurityConfig config = LoginSecurity.getConfiguration();
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        if(pt.isAuthorized(event.getPlayer().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        if(pt.isAuthorized(event.getPlayer().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        if(pt.isAuthorized(event.getPlayer().getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        defaultEventAction(event);
    }

    /**
     * Player action filtering.
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(isInvalidPlayer(event.getPlayer())) return;
        if(pt.isAuthorized(event.getPlayer().getUniqueId())) return;

        // Check whitelisted commands, and allow it if it's whitelisted
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
        if(pt.isAuthorized(event.getPlayer().getUniqueId())) return;

        // Prevent moving
        final Location from = event.getFrom();
        final Location to = event.getTo();
        assert to != null;
        if(from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            event.setTo(event.getFrom());
        }

        // TODO: this is a quick fix
        if (event.getPlayer().getLocation().getBlock().getType() == Material.NETHER_PORTAL && pt.getConfig().getBoolean("workaround.portal")) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        // TODO: Set user to fly mode....
        // TODO: Figure out what the hell this means....
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        defaultEventAction(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        defaultEventAction(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        if(isInvalidPlayer(player)) return;
        if(pt.isAuthorized(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return; // Not a player
        final Player player = (Player) event.getEntity();
        if(isInvalidPlayer(player)) return;
        if(pt.isAuthorized(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTarget(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) return; // Not a player
        final Player player = (Player) event.getTarget();
        if(!player.isOnline()) return; // Target logged out
        if(isInvalidPlayer(player)) return;
        if(pt.isAuthorized(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    private void defaultEventAction(PlayerEvent event) {
        if(!(event instanceof Cancellable)) {
            throw new IllegalArgumentException("Event cannot be cancelled!");
        }
        if(isInvalidPlayer(event.getPlayer())) return;
        if(pt.isAuthorized(event.getPlayer().getUniqueId())) return;

        ((Cancellable) event).setCancelled(true);
    }


    private boolean isInvalidPlayer(HumanEntity human) {
        if(!(human instanceof Player)) return true;
        final Player player = (Player) human;
        return player.hasMetadata("NPC") || !player.isOnline();
    }
}