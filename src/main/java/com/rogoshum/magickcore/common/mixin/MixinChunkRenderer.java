package com.rogoshum.magickcore.common.mixin;

import com.rogoshum.magickcore.common.api.event.PreRenderChunkEvent;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)

public abstract class MixinChunkRenderer {
    @ModifyVariable(method = "renderBlockLayer",
            at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender.getPosition ()Lnet/minecraft/util/math/BlockPos;")
            )
    private ChunkRenderDispatcher.ChunkRender onRenderBlockLayer(ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender) {
        MinecraftForge.EVENT_BUS.post(new PreRenderChunkEvent(chunkrenderdispatcher$chunkrender.getPosition()));
        return chunkrenderdispatcher$chunkrender;
    }
}