package com.rogoshum.magickcore.client.render.instanced;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rogoshum.magickcore.api.render.RenderHelper;

import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.Queue;

public class ParticleInstanceRenderer extends InstanceVertexRenderer {

    public ParticleInstanceRenderer() {
        super(VertexFormat.Mode.TRIANGLES, new ParticleVertexBuffer(0), ParticleInstanceRenderer::init, new ParticleUpdateVertex(2));
    }

    private static void init(FloatBuffer buffer) {
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;
        float u05 = 0.5f;
        float v05 = 0.5f;
        Queue<RenderHelper.VertexAttribute> queue = Queues.newArrayDeque();
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[0]).uv(u05, v05));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[1]).uv(u1, v1));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[2]).uv(u1, v0));

        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[0]).uv(u05, v05));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[2]).uv(u1, v0));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[3]).uv(u0, v0));

        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[0]).uv(u05, v05));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[3]).uv(u0, v0));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[4]).uv(u0, v1));

        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[0]).uv(u05, v05));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[4]).uv(u0, v1));
        queue.add(RenderHelper.VertexAttribute.create().pos(RenderHelper.FAN_PARTICLE[1]).uv(u1, v1));
        buffer.position(0);
        for(RenderHelper.VertexAttribute va : queue) {
            buffer.put(va.posX);
            buffer.put(va.posY);
            buffer.put(va.posZ);
            buffer.put(va.texU);
            buffer.put(va.texV);
        }
        buffer.flip();
        GL15.glBufferData(34962, buffer, 35044);
    }
}
