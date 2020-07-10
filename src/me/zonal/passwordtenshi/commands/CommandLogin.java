package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandLogin implements CommandExecutor {

    private final PasswordTenshi pt;

    public CommandLogin(PasswordTenshi pt) {
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

        if (pt.isAuthorized(player.getUniqueId())) {
            sender.sendMessage("PPTenshi says: why are you trying to log in again?");
            return true;
        }

        try {
            String hash = pt.getPasswordHash(player.getUniqueId());
            pt.getLogger().info("Logging in player" + player.getDisplayName());
            pt.getLogger().info(hash);
            if (PasswordChecker.check(password, hash)) {
                sender.sendMessage("PPTenshi says: welcome back, my homie");
                pt.setAuthorized(player.getUniqueId(), true);
                return true;
            } else {
                sender.sendMessage("PPTenshi says: that ain't the right password");
                return false;
            }
        } catch (NullPointerException e) {
            sender.sendMessage("PPTenshi says: register first pls");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.sendMessage("PPTenshi says: fuxk fuck fuck fuck");
        return false;
    }
}
