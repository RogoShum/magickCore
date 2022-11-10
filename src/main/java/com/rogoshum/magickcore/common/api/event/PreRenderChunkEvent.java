package com.rogoshum.magickcore.common.api.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.eventbus.api.Event;

public class PreRenderChunkEvent extends Event {
    private final BlockPos renderPosition;

    public PreRenderChunkEvent(BlockPos renderPosition) {
        this.renderPosition = renderPosition;
    }

    public BlockPos getRenderPosition() {
        return renderPosition;
    }
}
