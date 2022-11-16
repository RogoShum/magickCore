package com.rogoshum.magickcore.client.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

public class SingleBuffer implements IRenderTypeBuffer {
    protected final Function<ResourceLocation, RenderType> type;
    protected final ResourceLocation texture;
    protected final boolean useDefaultTexture;
    protected RenderType lastRenderType;
    protected BufferBuilder GlintBuffer = new BufferBuilder(256);

    public SingleBuffer(Function<ResourceLocation, RenderType> renderType, ResourceLocation defaultTexture) {
        this.type = renderType;
        this.texture = defaultTexture;
        useDefaultTexture = false;
    }

    public SingleBuffer(Function<ResourceLocation, RenderType> renderType, ResourceLocation defaultTexture, boolean useDefaultTexture) {
        this.type = renderType;
        this.texture = defaultTexture;
        this.useDefaultTexture = useDefaultTexture;
    }

    @Nonnull
    public IVertexBuilder getBuffer(@Nonnull RenderType p_getBuffer_1_) {
        if(p_getBuffer_1_ == RenderType.getGlintDirect()) {
            if(!GlintBuffer.isDrawing())
                GlintBuffer.begin(p_getBuffer_1_.getDrawMode(), p_getBuffer_1_.getVertexFormat());
            return GlintBuffer;
        }

        Class<RenderType> clazz = null;
        RenderType temp = null;
        if(useDefaultTexture) {
            temp = type.apply(this.texture);
        } else {
            try {
                clazz = (Class<RenderType>) Class.forName("net.minecraft.client.renderer.RenderType$Type");
                if(p_getBuffer_1_.getClass().equals(clazz)) {
                    Object o = ObfuscationReflectionHelper.getPrivateValue(clazz, p_getBuffer_1_, "field_228668_S_");
                    if(o instanceof RenderType.State) {
                        RenderType.State state = (RenderType.State) o;
                        RenderState.TextureState textureState = ObfuscationReflectionHelper.getPrivateValue(RenderType.State.class, state, "field_228677_a_");
                        Optional<ResourceLocation> texture = ObfuscationReflectionHelper.getPrivateValue(RenderState.TextureState.class, textureState, "field_228602_Q_");
                        if(texture.isPresent()) {
                            temp = type.apply(texture.get());
                        } else
                            temp = type.apply(this.texture);
                    }
                }
            } catch (Exception I) {

            }
        }

        if(temp != null)
            begin(temp.getDrawMode() == p_getBuffer_1_.getDrawMode() && temp.getVertexFormat().equals(p_getBuffer_1_.getVertexFormat()) ? temp : p_getBuffer_1_);
        else
            begin(p_getBuffer_1_);
        return Tessellator.getInstance().getBuffer();
    }

    public void begin(RenderType renderType) {
        if(this.lastRenderType != null) {
            finishTessellator();
        } else if(Tessellator.getInstance().getBuffer().isDrawing())
            Tessellator.getInstance().getBuffer().finishDrawing();
        this.lastRenderType = renderType;
        if(!Tessellator.getInstance().getBuffer().isDrawing())
            Tessellator.getInstance().getBuffer().begin(renderType.getDrawMode(), renderType.getVertexFormat());
    }

    public void finish() {
        if(lastRenderType != null)
            lastRenderType.finish(Tessellator.getInstance().getBuffer(), 0, 0, 0);
        if(GlintBuffer.isDrawing()) {
            RenderType.getGlintDirect().finish(GlintBuffer, 0, 0, 0);
        }
    }

    public void finishTessellator() {
        if(lastRenderType != null)
            lastRenderType.finish(Tessellator.getInstance().getBuffer(), 0, 0, 0);
    }
}
