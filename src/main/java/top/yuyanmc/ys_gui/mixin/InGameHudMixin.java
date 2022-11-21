package top.yuyanmc.ys_gui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    private static final Identifier HEALTH_TEXTURE = new Identifier("textures/ys/gui/health.png");

    @Final @Shadow private MinecraftClient client;

    @Shadow private int scaledHeight;

    @Shadow private int scaledWidth;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "renderStatusBars", at = @At("INVOKE"), cancellable = true)
    public void renderStatusBar(MatrixStack matrices, CallbackInfo ci){
        PlayerEntity playerEntity = this.getCameraPlayer();
        float health = playerEntity.getHealth();
        float maxHealth = playerEntity.getMaxHealth();
        int j = this.getZOffset();
        this.setZOffset(-90);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HEALTH_TEXTURE);
        String s = (int) health + "/" + (int) maxHealth;
        int x = this.scaledWidth / 2 - 32;
        int k = (this.scaledWidth - this.getTextRenderer().getWidth(s)) / 2;
        int l = this.scaledHeight - 20;
        this.drawTexture(matrices, x, l, 0, 0, 64, 7);
        if(health / maxHealth <= 0.25){
            this.drawTexture(matrices, x+1, l+1, 1,7, (int)(health / maxHealth * 62), 5);
        }else{
            this.drawTexture(matrices, x+1, l+1, 1,12, (int)(health / maxHealth * 62), 5);
        }
        this.getTextRenderer().draw(matrices, s, (float)k-1, (float)l, 0);
        this.getTextRenderer().draw(matrices, s, (float)k+1, (float)l, 0);
        this.getTextRenderer().draw(matrices, s, (float)k, (float)l-1, 0);
        this.getTextRenderer().draw(matrices, s, (float)k, (float)l+1, 0);
        this.getTextRenderer().draw(matrices, s, (float)k, (float)l, 0xffffff);
        this.setZOffset(j);
        ci.cancel();
    }
}
