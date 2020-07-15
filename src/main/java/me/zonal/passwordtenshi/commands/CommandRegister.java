package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandRegister implements CommandExecutor {

    private final PasswordTenshi pt;
    private final ConfigFile config;

    public CommandRegister(PasswordTenshi pt) {
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
            sender.sendMessage(config.getLocal("register.no_arguments"));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            final String password = args[0];
            Player player = (Player) sender;


            if (pt.getPasswordHash(player.getUniqueId()) != null) {
                sender.sendMessage(config.getLocal("register.already_registered"));
                return;
            }

            try {
                String hash = PasswordChecker.getSaltedHash(password);
                pt.setPasswordHash(player.getUniqueId(), hash);
                pt.setAuthorized(player.getUniqueId(), true);

                // TODO: make this not such a quick fix
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SURVIVAL);
                }

                sender.sendMessage(config.getRegisterMsg(player.getDisplayName()));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }
}
