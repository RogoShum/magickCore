package com.rogoshum.magickcore.client.render.instanced;

import com.rogoshum.magickcore.client.render.instanced.attribute.GLFloatVertex;

import java.nio.FloatBuffer;
import java.util.function.Consumer;

public class ModelUpdateVertex extends VertexAttrib {

    public ModelUpdateVertex(int index) {
        super (
                GLFloatVertex.createF2(index, "Light"),
                GLFloatVertex.createF4(index+1, "UV"),
                GLFloatVertex.createF4(index+2, "Color"),
                GLFloatVertex.createF4(index+3, "ModelMat"),
                GLFloatVertex.createF4(index+4, "ModelMat"),
                GLFloatVertex.createF4(index+5, "ModelMat"),
                GLFloatVertex.createF4(index+6, "ModelMat")
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
