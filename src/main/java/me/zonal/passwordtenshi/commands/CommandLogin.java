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

import java.util.logging.Logger;
import java.util.Collections;
import java.util.List;

public class CommandLogin implements TabExecutor {

    private final PasswordTenshi pt;
    private final Logger logger;

    public CommandLogin(PasswordTenshi pt) {
        this.pt = pt;
        this.logger = pt.getMainLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, 
                             String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    ConfigFile.getLocal("console.console_not_allowed"));
            
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(
                    ConfigFile.getLocal("login.no_arguments"));
            
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

            if (playersession.isAuthorized()) {
                sender.sendMessage(
                        ConfigFile.getLocal("login.already_logged"));
                return;
            }

            try {
                String hash = playersession.getPasswordHash();
                logger.info(ConfigFile.getLocal("console.player_login")
                        +" "+player.getDisplayName());
                
                if (PasswordChecker.check(password, hash)) {
                    sender.sendMessage(
                            ConfigFile.getLoginMsg(player.getDisplayName()));
                    
                    playersession.setAuthorized(true);
                    Bukkit.getScheduler().runTask(pt, () -> 
                            player.setGameMode(playersession.getGamemode()));
                    
                } else {
                    sender.sendMessage(
                            ConfigFile.getLocal("login.wrong_password"));
                }
            } catch (NullPointerException e) {
                sender.sendMessage(
                        ConfigFile.getLocal("login.not_registered"));
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