package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.ConfigFile;
import me.zonal.passwordtenshi.player.PlayerSession;
import me.zonal.passwordtenshi.player.PlayerStorage;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandRegister implements TabExecutor {

    private final PasswordTenshi pt;

    public CommandRegister(PasswordTenshi pt) {
        this.pt = pt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, 
                             String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ConfigFile.getLocal("console.console_not_allowed"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ConfigFile.getLocal("register.no_arguments"));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = (Player) sender;
            String password = "";

            for (String passArgument : args){
                password += passArgument+" ";
            }
            
            password = password.substring(0, password.length()-1);
            PlayerSession playersession 
                    = PlayerStorage
                    .getPlayerSession(player.getUniqueId());

            if (playersession.isAuthorized() 
                    || playersession.getPasswordHash() != null) {
                
                sender.sendMessage(
                        ConfigFile.getLocal("register.already_registered"));
                
                return;
            }

            try {
                String hash = PasswordChecker.getSaltedHash(password);
                playersession.setPasswordHash(hash);
                playersession.setAuthorized(true);

                Bukkit.getScheduler().runTask(pt, () -> 
                        player.setGameMode(playersession.getGamemode()));

                sender.sendMessage(
                        ConfigFile.getRegisterMsg(player.getDisplayName()));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public List onTabComplete(CommandSender sender, Command command, 
                              String alias, String[] args) {
        
        return Collections.emptyList();
    }
}
