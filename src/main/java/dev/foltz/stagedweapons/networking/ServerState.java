package dev.foltz.stagedweapons.networking;

import dev.foltz.stagedweapons.StagedWeapons;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class ServerState extends PersistentState {
    public static final Type<ServerState> TYPE = new Type<>(ServerState::new, ServerState::createFromNbt, null);
    public final HashMap<UUID, ServerEntityState> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    public static ServerState createFromNbt(NbtCompound nbt) {
        return new ServerState();
    }

    public static ServerState getServerState(MinecraftServer server) {
        var stateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        return stateManager.getOrCreate(TYPE, StagedWeapons.MODID);
    }

    public static ServerEntityState getEntityState(Entity entity) {
        var serverState = getServerState(entity.getServer());
        return serverState.players.computeIfAbsent(entity.getUuid(), uuid -> new ServerEntityState(entity));
    }
}
