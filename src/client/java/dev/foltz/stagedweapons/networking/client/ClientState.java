package dev.foltz.stagedweapons.networking.client;

import dev.foltz.stagedweapons.networking.ServerEntityState;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.UUID;

public class ClientState {
    public static final HashMap<UUID, ClientEntityState> players = new HashMap<>();

    public static ClientEntityState getEntityState(Entity entity) {
        return players.computeIfAbsent(entity.getUuid(), uuid -> new ClientEntityState(entity));
    }
}
