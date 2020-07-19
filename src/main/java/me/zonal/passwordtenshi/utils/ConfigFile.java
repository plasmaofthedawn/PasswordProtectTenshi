package me.zonal.passwordtenshi.utils;

import me.zonal.passwordtenshi.PasswordTenshi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigFile {
    private static PasswordTenshi pt;
    private static FileConfiguration config;
    private static FileConfiguration language;
    private static Boolean randommsg;
    private static String chatprefix;
    private static String[] loginmgs;
    private static String[] registermgs;

    //translation
    public static String getLoginMsg(String player){
        if (randommsg) {
            int randomindex = new Random().nextInt(loginmgs.length);
            return chatprefix+loginmgs[randomindex].replace("$PLAYER", player);
        } else {
            return chatprefix+loginmgs[0].replace("$PLAYER", player);
        }
    }

    public static String getRegisterMsg(String player){
        if (randommsg) {
            int randomindex = new Random().nextInt(registermgs.length);
            return chatprefix+registermgs[randomindex].replace("$PLAYER", player);
        } else {
            return chatprefix+registermgs[0].replace("$PLAYER", player);
        }
    }

    public static String getLocal(String ymlpath){
        return chatprefix+language.getString(ymlpath);
    }
    //config
    public static String getString(String ymlpath){
        return config.getString(ymlpath);
    }

    public static Integer getInt(String ymlpath){
        return config.getInt(ymlpath);
    }

    public static Boolean getBoolean(String ymlpath){
        return config.getBoolean(ymlpath);
    }

    public static Boolean getlangBoolean(String ymlpath){
        return language.getBoolean(ymlpath);
    }

    public static void setBoolean(String ymlpath, Boolean value){
        config.set(ymlpath, value);
    }

    public static void initializeConfig(PasswordTenshi ptt) {
        //initialize stuff
        pt = ptt;
        randommsg = false;
        config = pt.getConfig();
        File languagefile = new File(pt.getDataFolder().getAbsolutePath(), "language.yml");
        if (!languagefile.exists()) {            
            pt.saveResource("language.yml", false);
        }
        language = YamlConfiguration.loadConfiguration(languagefile);
        InputStream internalimplementation = pt.getResource("language.yml");
        language.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(internalimplementation)));
        //general config
        //mysql
        config.addDefault("database.mysql.enable", false);
        config.addDefault("database.mysql.database_host", "127.0.0.1");
        config.addDefault("database.mysql.database_port", 3306);
        config.addDefault("database.mysql.database_name", "passwords");
        config.addDefault("database.mysql.database_user", "root");
        config.addDefault("database.mysql.database_password", "root");
        //h2
        config.addDefault("database.h2.database_name", "pass");
        config.addDefault("database.h2.database_user", "dontchangeme");
        config.addDefault("database.h2.database_password", "dontchangeme");
        //write defaults
        config.options().copyDefaults(true);
        pt.saveConfig();
        //load config
        loginmgs = language.getList("login.login_messages").stream().toArray(String[]::new);
        registermgs = language.getList("register.register_messages").stream().toArray(String[]::new);
        chatprefix = getColor(language.getString("pptenshi.chat.prefix.color"))+language.getString("pptenshi.chat.prefix.text")+getColor(language.getString("pptenshi.chat.color"))+" ";

        if (language.getBoolean("random.use_random_login-register_messages")){
            randommsg = true;
        }
    }
    

    private static ChatColor getColor(String color) {
        try {
            return ChatColor.valueOf(color.toUpperCase().replaceAll("\\s+", ""));
        } catch(Exception ex) {
            return ChatColor.WHITE;
        }
    }
}