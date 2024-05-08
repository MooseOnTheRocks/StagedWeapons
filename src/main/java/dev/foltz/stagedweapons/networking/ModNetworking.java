package dev.foltz.stagedweapons.networking;

import dev.foltz.stagedweapons.StagedWeapons;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public abstract class ModNetworking {
    public static final Identifier PACKET_ID_RELOAD_PRESS = Identifier.of(StagedWeapons.MODID, "reload_press");
    public static final Identifier PACKET_ID_RELOAD_RELEASE = Identifier.of(StagedWeapons.MODID, "reload_release");

    public static final Identifier PACKET_ID_SHOOT_PRESS = Identifier.of(StagedWeapons.MODID, "shoot_press");
    public static final Identifier PACKET_ID_SHOOT_RELEASE = Identifier.of(StagedWeapons.MODID, "shoot_release");

    public static final Identifier PACKET_ID_AIM_PRESS = Identifier.of(StagedWeapons.MODID, "aim_press");
    public static final Identifier PACKET_ID_AIM_RELEASE = Identifier.of(StagedWeapons.MODID, "aim_release");

    public static void registerAllEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> ServerEntityState.onHeldItemChange(handler.getPlayer())));

        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID_SHOOT_PRESS, (server, player, handler, buf, responseSender) -> server.execute(() -> ServerEntityState.onPressShoot(player)));
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID_SHOOT_RELEASE, (server, player, handler, buf, responseSender) -> server.execute(() -> ServerEntityState.onReleaseShoot(player)));

        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID_RELOAD_PRESS, (server, player, handler, buf, responseSender) -> server.execute(() -> ServerEntityState.onPressReload(player)));
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID_RELOAD_RELEASE, (server, player, handler, buf, responseSender) -> server.execute(() -> ServerEntityState.onReleaseReload(player)));

        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID_AIM_PRESS, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            System.out.println("AIM PRESS");
            ServerEntityState.onPressAim(player);
            for (var other : PlayerLookup.tracking(player)) {
                ServerPlayNetworking.send(other, PACKET_ID_AIM_PRESS, buf);
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID_AIM_RELEASE, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            System.out.println("AIM RELEASE");
            ServerEntityState.onReleaseAim(player);
            for (var other : PlayerLookup.tracking(player)) {
                ServerPlayNetworking.send(other, PACKET_ID_AIM_RELEASE, buf);
            }
        }));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerState serverState = ServerState.getServerState(server);
            serverState.players.forEach((uuid, entityState) -> {
                var entity = entityState.getEntity();
                if (entity == null) {
                    return;
                }

                if (entity.getHandItems().iterator().next() != entityState.getLastHeldItemStack()) {
                    ServerEntityState.onHeldItemChange(entity);
                }
                else {
                    ServerEntityState.onTick(entity);
                }
            });
        });
    }
}
