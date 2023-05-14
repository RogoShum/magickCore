package com.rogoshum.magickcore.client.render.instanced;

import com.rogoshum.magickcore.client.render.instanced.attribute.GLFloatVertex;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

public class ParticleUpdateVertex extends VertexAttrib {

    public ParticleUpdateVertex(int index) {
        super (
                GLFloatVertex.createF1(index, "Shake"),
                GLFloatVertex.createF2(index+1, "Scale"),
                GLFloatVertex.createF4(index+2, "UV"),
                GLFloatVertex.createF4(index+3, "Color"),
                GLFloatVertex.createF3(index+4, "Position")
        );
    }

    public void addAttrib(Consumer<FloatBuffer> bufferConsumer) {
        try {
            bufferConsumer.accept(this.buffer);
        } catch (Exception e) {
            this.buffer.position(0);
        }
    }

    @Override
    public void init(Consumer<FloatBuffer> bufferConsumer) {}

    @Override
    public boolean needUpdate() {
        return true;
    }
}
