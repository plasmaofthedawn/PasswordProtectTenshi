package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandUnregister implements CommandExecutor {

    private final PasswordTenshi pt;
    private final ConfigFile config;

    public CommandUnregister(PasswordTenshi pt) {
        this.pt = pt;
        config = new ConfigFile(this.pt);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getLocal("console.console_not_allowed"));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = (Player) sender;

            try {
                pt.removePasswordHash(player.getUniqueId());
                sender.sendMessage(config.getLocal("unregister.player_unregister"));
                pt.setAuthorized(player.getUniqueId(), false);

                player.sendMessage(config.getLocal("unregister.register_again"));

                pt.sendRegisterLoginSpam(player);

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
