package com.rogoshum.magickcore.client.render.instanced;

import com.rogoshum.magickcore.client.render.instanced.attribute.GLFloatVertex;
import com.rogoshum.magickcore.client.render.instanced.attribute.GLVertex;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

public class ParticleVertexBuffer extends VertexAttrib {

    public ParticleVertexBuffer(int index) {
        super(
                GLFloatVertex.createF3(index, "Position"),
                GLFloatVertex.createF2(index+1, "UV")
        );
    }

    public void addAttrib(Consumer<FloatBuffer> bufferConsumer) {}

    @Override
    public void init(Consumer<FloatBuffer> bufferConsumer) {
        bufferConsumer.accept(this.buffer);
        int count = 0;
        for(GLVertex vertex : vertices) {
            count += vertex.size();
        }
        setVertexCount(this.buffer.limit()/count);
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
