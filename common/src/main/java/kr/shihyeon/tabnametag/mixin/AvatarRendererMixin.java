package kr.shihyeon.tabnametag.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
        at = @At("TAIL")
    )
    private void tabnametag$overrideNameTag(Avatar avatar, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.player.connection == null) {
            return;
        }

        UUID uuid = avatar.getUUID();
        client.player.connection.getOnlinePlayers().stream()
            .filter(info -> info.getProfile().id().equals(uuid))
            .findFirst()
            .ifPresent(info -> {
                Component display = info.getTabListDisplayName();
                if (display != null) {
                    state.nameTag = display;
                } else {
                    state.nameTag = Component.literal(info.getProfile().name());
                }
            });
    }
}
