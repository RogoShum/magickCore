package com.rogoshum.magickcore.client.render.instanced;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rogoshum.magickcore.api.render.RenderHelper;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.util.Queue;

public class LightingInstanceRenderer extends InstanceVertexRenderer {

    public LightingInstanceRenderer() {
        super(VertexFormat.Mode.QUADS, new LightingVertexBuffer(0), LightingInstanceRenderer::init, new LightingUpdateVertex(1));
    }

    private static void init(FloatBuffer buffer) {
        Queue<RenderHelper.VertexAttribute> queue = Queues.newArrayDeque();
        for(int i=0; i<6; ++i)
            for(int j=0; j<4; ++j) {
                float[] pos = RenderHelper.vertex_list[RenderHelper.index_list[i][j]];
                queue.add(RenderHelper.VertexAttribute.create().pos(pos[0], pos[1], pos[2]));
            }
        buffer.position(0);
        for(RenderHelper.VertexAttribute va : queue) {
            buffer.put(va.posX);
            buffer.put(va.posY);
            buffer.put(va.posZ);
        }
        buffer.flip();
        GL15.glBufferData(34962, buffer, 35044);
    }
}
