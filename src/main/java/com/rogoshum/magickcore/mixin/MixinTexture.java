package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.render.AccessorTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(RenderStateShard.TextureStateShard.class)
public class MixinTexture implements AccessorTexture {
    @Mutable
    @Shadow @Final private Optional<ResourceLocation> texture;
    private Optional<ResourceLocation> realTexture = Optional.empty();

    @Inject(method = "<init>", at = @At("RETURN"))
    protected void onConstructor(CallbackInfo info) {
        realTexture = this.texture;
    }

    @Override
    public Optional<ResourceLocation> getTexture() {
        return texture;
    }

    @Override
    public void setTexture(ResourceLocation res) {
        this.texture = Optional.of(res);
    }

    @Override
    public Optional<ResourceLocation> getRealTexture() {
        return realTexture;
    }
}
