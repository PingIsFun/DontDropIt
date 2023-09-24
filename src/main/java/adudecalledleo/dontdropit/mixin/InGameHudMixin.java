package adudecalledleo.dontdropit.mixin;

import adudecalledleo.dontdropit.DropDelayRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderHotbarItem", at = @At(value = "TAIL"))
    public void renderHotbarDropProgress(DrawContext context, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        DropDelayRenderer.renderOverlay(context, stack, x, y, 0);
    }
}
