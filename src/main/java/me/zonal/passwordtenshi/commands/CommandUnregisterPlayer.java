package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


public class CommandUnregisterPlayer implements CommandExecutor {

    private final PasswordTenshi pt;
    private final ConfigFile config;

    public CommandUnregisterPlayer(PasswordTenshi pt) {
        this.pt = pt;
        config = new ConfigFile(this.pt);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(config.getLocal("unregisterplayer.unrecognized_execution"));
            return true;
         }

         if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
             sender.sendMessage(config.getLocal("unregisterplayer.player_not_op"));
             return true;
         }

         if (args.length == 0) {
             sender.sendMessage(config.getLocal("unregisterplayer.no_arguments"));
             return false;
         }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(config.getLocal("unregisterplayer.player_not_online"));
                return;
            }

            try {
                pt.removePasswordHash(player.getUniqueId());
                sender.sendMessage(config.getLocal("unregisterplayer.successful_unregister"));
                player.sendMessage(config.getLocal("unregisterplayer.target_player_unregister"));
                player.sendMessage(config.getLocal("unregister.register_again"));
                pt.setAuthorized(player.getUniqueId(), false);

                pt.sendRegisterLoginSpam(player);

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return false;
    }
}
