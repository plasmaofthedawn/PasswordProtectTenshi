package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandRegister implements CommandExecutor {

    private final PasswordTenshi pt;

    public CommandRegister(PasswordTenshi pt) {
        this.pt = pt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("PPTenshi says: fuck off console user");
            return false;
        }

        Player player = (Player) sender;

        final String password = args[0];

        if(pt.getPasswordHash(player.getUniqueId()) != null) {
            sender.sendMessage("PPTenshi says: you already have a password");
            return false;
        }

        try {
            String hash = PasswordChecker.getSaltedHash(password);
            pt.setPasswordHash(player.getUniqueId(), hash);
            pt.setAuthorized(player.getUniqueId(), true);
            sender.sendMessage("PPTenshi says: i got your password now <3");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.sendMessage("PPTenshi says: fuxk fuck fuck fuck");
        return true;
    }
}
