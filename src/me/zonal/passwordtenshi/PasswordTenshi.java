package me.zonal.passwordtenshi;

import me.zonal.passwordtenshi.commands.CommandLogin;
import me.zonal.passwordtenshi.commands.CommandRegister;
import me.zonal.passwordtenshi.commands.CommandUnregister;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordTenshi extends JavaPlugin {

    ConcurrentHashMap<UUID, Boolean> authentication_map;
    ConcurrentHashMap<UUID, String> password_map;


    @Override
    public void onEnable() {
        authentication_map = new ConcurrentHashMap<>();
        password_map = new ConcurrentHashMap<>();
        getPasswordMap();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        this.getCommand("register").setExecutor(new CommandRegister(this));
        this.getCommand("unregister").setExecutor(new CommandUnregister(this));
        this.getCommand("login").setExecutor(new CommandLogin(this));

        getLogger().info("PPTenshi be here to protect your server <3");
    }

    private void getPasswordMap() {

        // fresh off the stack overflow printing press
        File file = new File("passwords.txt");
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        boolean is_name = true;
        ArrayList<Byte> name = new ArrayList<>();
        ArrayList<Byte> hash = new ArrayList<>();
        for (byte b : bFile) {
            if (b == (byte) '+') {
                if (!is_name) {
                    getLogger().info("key pair");
                    getLogger().info(new String(toByteArray(name)) + " " + new String(toByteArray(hash)));
                    password_map.put(UUID.fromString(new String(toByteArray(name))), new String(toByteArray(hash)));
                    name.clear();
                    hash.clear();
                }
                is_name = !is_name;
            } else if (is_name) {
                name.add(b);
            } else {
                hash.add(b);
            }
        }

        getLogger().info("Got the passwords map");
    }

    private void writePasswordMap() {
        try {
            FileWriter myWriter = new FileWriter("passwords.txt");
            for(Map.Entry<UUID, String> entry : password_map.entrySet()) {
                myWriter.write(entry.getKey().toString());
                myWriter.write("+");
                myWriter.write(entry.getValue());
                myWriter.write("+");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info("Wrote the passwords map");

    }

    @Override
    public void onDisable(){
        writePasswordMap();
    }

    public boolean isAuthorized(UUID uuid){
        return authentication_map.get(uuid);
    }

    public void setAuthorized(UUID uuid, boolean authorized) {
        getLogger().info(String.format("Set %s authorization to %s", uuid, authorized));
        authentication_map.put(uuid, authorized);
    }

    public String getPasswordHash(UUID uuid){
        try {
            return password_map.get(uuid);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setPasswordHash(UUID uuid, String hash){
        getLogger().info(String.format("Set %s password hash to %s", uuid, hash));
        System.out.println(uuid);
        System.out.println(hash);
        password_map.put(uuid, hash);
    }

    public void removePasswordHash(UUID uuid) {
        password_map.remove(uuid);
    }

    private static byte[] toByteArray(ArrayList<Byte> list){
        final int n = list.size();
        byte[] ret = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
}
