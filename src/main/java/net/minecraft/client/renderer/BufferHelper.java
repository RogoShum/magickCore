package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;

import java.util.Optional;
import java.util.Set;

public class BufferHelper {
    public static BufferBuilder getBuffer(MultiBufferSource.BufferSource impl) {
        return impl.builder;
    }

    public static Optional<RenderType> getLastRenderType(MultiBufferSource.BufferSource impl) {
        return impl.lastState;
    }

    public static Set<BufferBuilder> getStartedBuffers(MultiBufferSource.BufferSource impl) {
        return impl.startedBuffers;
    }
}
