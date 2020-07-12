package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordTenshi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandUnregister implements CommandExecutor {

    private final PasswordTenshi pt;

    public CommandUnregister(PasswordTenshi pt) {
        this.pt = pt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§bPPTenshi says§r: fuck off console user");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = (Player) sender;

            try {
                pt.removePasswordHash(player.getUniqueId());
                sender.sendMessage("§bPPTenshi says§r: you have lost your registered abiliillties");
                pt.setAuthorized(player.getUniqueId(), false);

                player.sendMessage("§bPPTenshi says§r: register (/register <password>) again you baka~");

                pt.sendRegisterLoginSpam(player);

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

            sender.sendMessage("§bPPTenshi says§r: fuxk fuck fuck fuck");
        });
        return true;
    }
}
