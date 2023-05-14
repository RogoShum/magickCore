package com.rogoshum.magickcore.client.render.instanced;

import com.rogoshum.magickcore.client.render.instanced.attribute.GLFloatVertex;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

public class LightingUpdateVertex extends VertexAttrib {

    public LightingUpdateVertex(int index) {
        super (
                GLFloatVertex.createF1(index, "Normal"),
                GLFloatVertex.createF1(index+1, "Scale"),
                GLFloatVertex.createF4(index+2, "Color"),
                GLFloatVertex.createF3(index+3, "Pos")
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
