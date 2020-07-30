package me.zonal.passwordtenshi.commands;

import me.zonal.passwordtenshi.PasswordChecker;
import me.zonal.passwordtenshi.PasswordTenshi;
import me.zonal.passwordtenshi.utils.ConfigFile;
import me.zonal.passwordtenshi.player.PlayerSession;
import me.zonal.passwordtenshi.player.PlayerStorage;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
            sender.sendMessage(ConfigFile.getLocal("console.console_not_allowed"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ConfigFile.getLocal("register.no_arguments"));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pt, () -> {

            final String password = args[0];
            Player player = (Player) sender;
            PlayerSession playersession = PlayerStorage.getPlayerSession(player.getUniqueId());

            if (playersession.isAuthorized() || playersession.getPasswordHash() != null) {
                sender.sendMessage(ConfigFile.getLocal("register.already_registered"));
                return;
            }

            try {
                String hash = PasswordChecker.getSaltedHash(password);
                playersession.setPasswordHash(hash);
                playersession.setAuthorized(true);

                // TODO: make this not such a quick fix
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    Bukkit.getScheduler().runTask(pt, () -> player.setGameMode(GameMode.SURVIVAL));
                }

                sender.sendMessage(ConfigFile.getRegisterMsg(player.getDisplayName()));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
