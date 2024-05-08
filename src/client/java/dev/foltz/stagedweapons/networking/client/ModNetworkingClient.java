package dev.foltz.stagedweapons.networking.client;

import dev.foltz.stagedweapons.networking.ServerEntityState;
import dev.foltz.stagedweapons.networking.ModNetworking;
import dev.foltz.stagedweapons.stage.IStagedItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dev.foltz.stagedweapons.StagedWeaponsClient.*;

public class ModNetworkingClient {
    private static boolean isPressingShoot = false;
    private static boolean isPressingReload = false;
    private static boolean isPressingAim = false;
    private static boolean hasOpenScreen = false;

    public void registerAllNetworkingEvents() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.PACKET_ID_AIM_PRESS, (client, handler, buf, responseSender) ->
            client.execute(() -> {
                ClientState.getEntityState(client.player).setAiming(true);
            })
        );

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.PACKET_ID_AIM_RELEASE, (client, handler, buf, responseSender) ->
                client.execute(() -> {
                    ClientState.getEntityState(client.player).setAiming(false);
                })
        );

        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> (player.getMainHandStack().getItem() instanceof IStagedItem<?>));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                return;
            }

            boolean holdingStagedItem = client.player.getMainHandStack().getItem() instanceof IStagedItem<?>;

            if (SHOOT_BIND.get().isPressed() && !isPressingShoot && holdingStagedItem) {
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_SHOOT_PRESS, PacketByteBufs.empty());
                isPressingShoot = true;
            }
            else if (!SHOOT_BIND.get().isPressed() && isPressingShoot) {
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_SHOOT_RELEASE, PacketByteBufs.empty());
                isPressingShoot = false;
            }

            if (RELOAD_BIND.get().isPressed() && !isPressingReload && holdingStagedItem) {
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_RELOAD_PRESS, PacketByteBufs.empty());
                isPressingReload = true;
            }
            else if (!RELOAD_BIND.get().isPressed() && isPressingReload) {
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_RELOAD_RELEASE, PacketByteBufs.empty());
                isPressingReload = false;
            }

            if (AIM_BIND.get().isPressed() && !isPressingAim && holdingStagedItem) {
                ClientState.getEntityState(client.player).setAiming(true);
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_AIM_PRESS, PacketByteBufs.empty());
                isPressingAim = true;
            }
            else if (!AIM_BIND.get().isPressed() && isPressingAim) {
                ClientState.getEntityState(client.player).setAiming(false);
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_AIM_RELEASE, PacketByteBufs.empty());
                isPressingAim = false;
            }

            if (!hasOpenScreen && client.currentScreen != null) {
                hasOpenScreen = true;
                isPressingShoot = false;
                isPressingReload = false;
                isPressingAim = false;
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_SHOOT_RELEASE, PacketByteBufs.empty());
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_RELOAD_RELEASE, PacketByteBufs.empty());
                ClientPlayNetworking.send(ModNetworking.PACKET_ID_AIM_RELEASE, PacketByteBufs.empty());
            }
            else if (hasOpenScreen && client.currentScreen == null) {
                hasOpenScreen = false;
            }
        });
    }
}
