package me.zonal.passwordtenshi;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class MetaData {

    static PasswordTenshi pt;

    public static void setPT(PasswordTenshi pt) {
        MetaData.pt = pt;
    }

    public static void set(Player player, String key, Object value) {
        player.setMetadata(key, new FixedMetadataValue(pt, value));
    }

    public static boolean has(Player player, String key) {
        return get(player, key, Object.class) != null;
    }

    public static <T> T get(Player player, String key, T def) {
        Object value = get(player, key, Object.class);
        return value == null ? def : (T) value;
    }

    public static int incrementAndGet(Player player, String key) {
        final int value = get(player, key, 0) + 1;
        set(player, key, value);
        return value;
    }

    public static <T> T get(Player player, String key, Class<T> type) {
        if(!player.hasMetadata(key)) {
            return null;
        }

        for(MetadataValue value : player.getMetadata(key)) {
            if(value.getOwningPlugin().equals(pt)) {
                return type.cast(value.value());
            }
        }

        return null;
    }

    public static void unset(Player player, String key) {
        player.removeMetadata(key, pt);
    }
}