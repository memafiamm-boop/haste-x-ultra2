package com.hastex.ultra.mixin;

import com.hastex.ultra.HasteXUltraConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side Auto Sprint. Presses the vanilla sprint key for the player when
 * sprinting is legitimately possible, so vanilla logic and packets are used
 * unchanged. Grants no speed beyond normal sprinting.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void hasteXUltra$autoSprint(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (client == null || client.player != player || client.options == null) return;

        HasteXUltraConfig.tickReload();
        HasteXUltraConfig cfg = HasteXUltraConfig.get();
        if (!cfg.autoSprint) return;
        if (client.currentScreen != null) return; // never interfere with GUIs/chat

        boolean forward = client.options.forwardKey.isPressed();
        boolean sneaking = client.options.sneakKey.isPressed() || player.isSneaking();
        boolean flying = player.getAbilities().flying;
        boolean hungry = player.getHungerManager().getFoodLevel() <= 6
                && !player.getAbilities().allowFlying;

        if (!forward || sneaking || hungry || player.isUsingItem()
                || player.hasStatusEffect(StatusEffects.BLINDNESS)
                || player.hasVehicle()
                || (flying && !cfg.sprintWhileFlying)) {
            return;
        }
        client.options.sprintKey.setPressed(true);
    }
}
