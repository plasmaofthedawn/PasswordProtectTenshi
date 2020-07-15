package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandLogin implements CommandExecutor {

    private final PasswordTenshi pt;
    private final ConfigFile config;

    public CommandLogin(PasswordTenshi pt) {
        this.pt = pt;
        config = new ConfigFile(this.pt);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getLocal("console.console_not_allowed"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(config.getLocal("login.no_arguments"));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = (Player) sender;
            final String password = args[0];

            if (pt.isAuthorized(player.getUniqueId())) {
                sender.sendMessage(config.getLocal("login.already_logged"));
                return;
            }

            try {
                String hash = pt.getPasswordHash(player.getUniqueId());
                pt.getLogger().info(config.getLocal("console.log.player_login")+" "+player.getDisplayName());
                if (PasswordChecker.check(password, hash)) {
                    sender.sendMessage(config.getLoginMsg(player.getDisplayName()));
                    pt.setAuthorized(player.getUniqueId(), true);

                    // TODO: make this not such a quick fix
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    return;
                } else {
                    sender.sendMessage(config.getLocal("login.wrong_password"));
                    return;
                }
            } catch (NullPointerException e) {
                sender.sendMessage(config.getLocal("login.not_registered"));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

            sender.sendMessage("fuck fuck fuck fuck");

        });
        return true;
    }
}