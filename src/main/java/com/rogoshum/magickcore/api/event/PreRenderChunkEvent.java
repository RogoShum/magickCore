package com.rogoshum.magickcore.api.event;

import net.minecraft.core.BlockPos;

public class PreRenderChunkEvent extends Event {
    private final BlockPos renderPosition;

    public PreRenderChunkEvent(BlockPos renderPosition) {
        this.renderPosition = renderPosition;
    }

    public BlockPos getRenderPosition() {
        return renderPosition;
    }
}
