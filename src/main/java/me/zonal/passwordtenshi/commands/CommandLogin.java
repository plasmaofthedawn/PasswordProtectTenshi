package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.ConfigFile;
import me.zonal.passwordtenshi.player.PlayerSession;
import me.zonal.passwordtenshi.player.PlayerStorage;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class CommandLogin implements CommandExecutor {

    private final PasswordTenshi pt;
    private final Logger logger;

    public CommandLogin(PasswordTenshi pt) {
        this.pt = pt;
        this.logger = pt.getMainLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ConfigFile.getLocal("console.console_not_allowed"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ConfigFile.getLocal("login.no_arguments"));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            Player player = (Player) sender;
            final String password = args[0];
            PlayerSession playersession = PlayerStorage.getPlayerSession(player.getUniqueId());

            if (playersession.isAuthorized()) {
                sender.sendMessage(ConfigFile.getLocal("login.already_logged"));
                return;
            }

            try {
                String hash = playersession.getPasswordHash();
                logger.info(ConfigFile.getLocal("console.player_login")+" "+player.getDisplayName());
                if (PasswordChecker.check(password, hash)) {
                    sender.sendMessage(ConfigFile.getLoginMsg(player.getDisplayName()));
                    playersession.setAuthorized(true);
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }

                    return;
                } else {
                    sender.sendMessage(ConfigFile.getLocal("login.wrong_password"));
                    return;
                }
            } catch (NullPointerException e) {
                sender.sendMessage(ConfigFile.getLocal("login.not_registered"));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}