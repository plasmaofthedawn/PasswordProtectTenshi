package me.zonal.passwordtenshi;

import java.util.UUID;

public class PlayerSession {

    private final UUID player_uuid;
    private boolean authenticated;

    public PlayerSession(UUID player_uuid, boolean authenticated){
        this.player_uuid = player_uuid;
        this.authenticated = authenticated;
    }

    public void authenticate() {
        this.authenticated = true;
    }

    public void deauthenticate(){
        this.authenticated = false;
    }

    public UUID getPlayer_uuid() {
        return player_uuid;
    }

}
