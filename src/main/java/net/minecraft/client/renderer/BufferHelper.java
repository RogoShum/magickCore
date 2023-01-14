package net.minecraft.client.renderer;

import java.util.Optional;
import java.util.Set;

public class BufferHelper {
    public static BufferBuilder getBuffer(IRenderTypeBuffer.Impl impl) {
        return impl.builder;
    }

    public static Optional<RenderType> getLastRenderType(IRenderTypeBuffer.Impl impl) {
        return impl.lastState;
    }

    public static Set<BufferBuilder> getStartedBuffers(IRenderTypeBuffer.Impl impl) {
        return impl.startedBuffers;
    }
}
