package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


public class CommandUnregisterPlayer implements CommandExecutor {

    private final PasswordTenshi pt;

    public CommandUnregisterPlayer(PasswordTenshi pt) {
        this.pt = pt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("§bPPTenshi says§r: fuck off whoever you are");
            return true;
         }

         if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
             sender.sendMessage("§bPPTenshi says§r: you're not a mod");
             return true;
         }

         if (args.length == 0) {
             sender.sendMessage("§bPPTenshi says§r: you need to specify which player :p");
             return false;
         }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage("§bPPTenshi says§r: couldn't find that player online >.<");
                return;
            }

            try {
                pt.removePasswordHash(player.getUniqueId());
                sender.sendMessage("§bPPTenshi says§r: rip that dudes password :OkayuPray:");
                player.sendMessage("§bPPTenshi says§r: your password privileges have been removed");
                pt.setAuthorized(player.getUniqueId(), false);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

            sender.sendMessage("§bPPTenshi says§r: fuxk fuck fuck fuck");
        });
        return false;
    }
}
