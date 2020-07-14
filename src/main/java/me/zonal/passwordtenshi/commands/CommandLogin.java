package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandLogin implements CommandExecutor {

    private final PasswordTenshi pt;
    private final String chatprefix = PasswordTenshi.chatprefix;

    public CommandLogin(PasswordTenshi pt) {
        this.pt = pt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(chatprefix+"fuck off console user");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(chatprefix+"enter a password");
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = (Player) sender;
            final String password = args[0];

            if (pt.isAuthorized(player.getUniqueId())) {
                sender.sendMessage(chatprefix+"why are you trying to log in again?");
                return;
            }

            try {
                String hash = pt.getPasswordHash(player.getUniqueId());
                pt.getLogger().info("Logging in player " + player.getDisplayName());
                if (PasswordChecker.check(password, hash)) {
                    sender.sendMessage(chatprefix+"welcome back, my homie");
                    pt.setAuthorized(player.getUniqueId(), true);

                    // TODO: make this not such a quick fix
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    return;
                } else {
                    sender.sendMessage(chatprefix+"that ain't the right password");
                    return;
                }
            } catch (NullPointerException e) {
                sender.sendMessage(chatprefix+"register first pls");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

            sender.sendMessage(chatprefix+"fuck fuck fuck fuck");

        });
        return true;
    }
}