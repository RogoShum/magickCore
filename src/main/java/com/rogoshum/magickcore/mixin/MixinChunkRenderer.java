package com.rogoshum.magickcore.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)

public abstract class MixinChunkRenderer {
    /*
    @ModifyVariable(method = "renderBlockLayer",
            at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender.getPosition ()Lnet/minecraft/util/math/BlockPos;")
            )
    private ChunkRenderDispatcher.ChunkRender onRenderBlockLayer(ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender) {
        MagickCore.EVENT_BUS.post(new PreRenderChunkEvent(chunkrenderdispatcher$chunkrender.getPosition()));
        return chunkrenderdispatcher$chunkrender;
    }

     */
}
