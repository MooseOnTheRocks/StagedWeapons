package dev.foltz.stagedweapons;

import dev.foltz.stagedweapons.networking.ModNetworking;
import dev.foltz.stagedweapons.networking.client.ModNetworkingClient;
import dev.foltz.stagedweapons.stage.IStagedItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class StagedWeaponsClient implements ClientModInitializer {

	public static final KeyBinding RELOAD_BINDING = new KeyBinding(
			"key.stagedweapons.reload",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_R,
			"key.category.stagedweapons"
	);

	public static final Supplier<KeyBinding> SHOOT_BIND = () -> MinecraftClient.getInstance().options.attackKey;

	public static final Supplier<KeyBinding> RELOAD_BIND = () -> RELOAD_BINDING;

	public static final Supplier<KeyBinding> AIM_BIND = () -> MinecraftClient.getInstance().options.useKey;


	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(RELOAD_BINDING);
		var modClient = new ModNetworkingClient();
		modClient.registerAllNetworkingEvents();
		ItemModelPredicates.registerAllItemModelPredicates();
		ModEntitiesClient.registerAllEntityRenderers();
	}
}