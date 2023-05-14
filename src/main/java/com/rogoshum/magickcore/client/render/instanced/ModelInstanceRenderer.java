package com.rogoshum.magickcore.client.render.instanced;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.rogoshum.magickcore.api.render.RenderHelper;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.Queue;
import java.util.function.Consumer;

public class ModelInstanceRenderer extends InstanceVertexRenderer {

    public ModelInstanceRenderer(Consumer<FloatBuffer> bufferConsumer) {
        super(VertexFormat.Mode.QUADS, new MainVertexBuffer(0), bufferConsumer, new ModelUpdateVertex(4));
    }

    public static ModelInstanceRenderer fromVertexAttrib(Queue<RenderHelper.VertexAttribute> queue) {
        return new ModelInstanceRenderer((buffer -> {
            buffer.position(0);
            for(RenderHelper.VertexAttribute va : queue) {
                buffer.put(va.posX);
                buffer.put(va.posY);
                buffer.put(va.posZ);
                buffer.put(va.normalX);
                buffer.put(va.normalY);
                buffer.put(va.normalZ);
                buffer.put(va.texU);
                buffer.put(va.texV);
                buffer.put(va.alpha);
            }
            buffer.flip();
            GL15.glBufferData(34962, buffer, 35044);
        }));
    }
}
