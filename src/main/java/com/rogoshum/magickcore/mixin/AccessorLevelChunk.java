package com.rogoshum.magickcore.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelChunk.class)
public interface AccessorLevelChunk {

    @Invoker("updateBlockEntityTicker")
    <T extends BlockEntity> void invokerUpdateBlockEntityTicker(T p_156407_);
}
