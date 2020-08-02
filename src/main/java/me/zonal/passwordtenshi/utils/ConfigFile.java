package me.zonal.passwordtenshi.utils;

import me.zonal.passwordtenshi.PasswordTenshi;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Random;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigFile {
    private static PasswordTenshi pt;
    private static FileConfiguration config;
    private static FileConfiguration language;
    private static Boolean randomMsg;
    private static String chatPrefix;
    private static String[] loginMgs;
    private static String[] registerMgs;

    //translation
    public static String getLoginMsg(String player){
        if (randomMsg) {
            int randomindex = new Random().nextInt(loginMgs.length);
            return chatPrefix+loginMgs[randomindex].replace("$PLAYER", player);
        } else {
            return chatPrefix+loginMgs[0].replace("$PLAYER", player);
        }
    }

    public static String getRegisterMsg(String player){
        if (randomMsg) {
            int randomindex = new Random().nextInt(registerMgs.length);
            return chatPrefix+registerMgs[randomindex].replace("$PLAYER", player);
        } else {
            return chatPrefix+registerMgs[0].replace("$PLAYER", player);
        }
    }

    public static String getLocal(String ymlpath){
        return chatPrefix+language.getString(ymlpath);
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
        randomMsg = false;
        config = pt.getConfig();
        File languagefile = new File(
                pt.getDataFolder()
                .getAbsolutePath()
                , "language.yml");
        
        if (!languagefile.exists()) {            
            pt.saveResource("language.yml", false);
        }
        
        language = YamlConfiguration.loadConfiguration(languagefile);
        InputStream intImplentation = pt.getResource("language.yml");
        language.setDefaults(YamlConfiguration
                .loadConfiguration(new InputStreamReader(intImplentation)));
        
        //general config
        //h2
        config.addDefault("database.h2.database_name", "pass");
        config.addDefault("database.h2.database_user", "dontchangeme");
        config.addDefault("database.h2.database_password", "dontchangeme");
        //write defaults
        config.options().copyDefaults(true);
        pt.saveConfig();
        //load config
        loginMgs = language.getList("login.login_messages").stream()
                .toArray(String[]::new);
        
        registerMgs = language.getList("register.register_messages").stream()
                .toArray(String[]::new);
        
        chatPrefix = getColor(language.getString("pptenshi.chat.prefix.color"))
                +language.getString("pptenshi.chat.prefix.text")
                +getColor(language.getString("pptenshi.chat.color"))
                +" ";

        if (language.getBoolean("random.use_random_login-register_messages")){
            randomMsg = true;
        }
    }
    
    private static ChatColor getColor(String color) {
        try {
            return ChatColor.valueOf(color.toUpperCase().replaceAll("\\s+", ""));
        } catch(IllegalArgumentException | NullPointerException ex) {
            return ChatColor.WHITE;
        }
    }
}