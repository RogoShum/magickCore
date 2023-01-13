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
        if(p_getBuffer_1_ == RenderType.glintDirect()
                || p_getBuffer_1_ == RenderType.glint()
                || p_getBuffer_1_ == RenderType.armorGlint()
                || p_getBuffer_1_ == RenderType.armorEntityGlint()
                || p_getBuffer_1_ == RenderType.entityGlintDirect()) {
            if(!GlintBuffer.building())
                GlintBuffer.begin(p_getBuffer_1_.mode(), p_getBuffer_1_.format());
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
                    Object o = ObfuscationReflectionHelper.getPrivateValue(clazz, p_getBuffer_1_, "state");
                    if(o instanceof RenderType.State) {
                        RenderType.State state = (RenderType.State) o;
                        RenderState.TextureState textureState = ObfuscationReflectionHelper.getPrivateValue(RenderType.State.class, state, "textureState");
                        Optional<ResourceLocation> texture = ObfuscationReflectionHelper.getPrivateValue(RenderState.TextureState.class, textureState, "texture");
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
            begin(temp.mode() == p_getBuffer_1_.mode() && temp.format().equals(p_getBuffer_1_.format()) ? temp : p_getBuffer_1_);
        else
            begin(p_getBuffer_1_);
        return Tessellator.getInstance().getBuilder();
    }

    public void begin(RenderType renderType) {
        if(this.lastRenderType != null) {
            finishTessellator();
        } else if(Tessellator.getInstance().getBuilder().building())
            Tessellator.getInstance().getBuilder().end();
        this.lastRenderType = renderType;
        if(!Tessellator.getInstance().getBuilder().building())
            Tessellator.getInstance().getBuilder().begin(renderType.mode(), renderType.format());
    }

    public void finish() {
        if(lastRenderType != null)
            lastRenderType.end(Tessellator.getInstance().getBuilder(), 0, 0, 0);
        if(GlintBuffer.building()) {
            RenderType.glintDirect().end(GlintBuffer, 0, 0, 0);
        }
    }

    public void finishTessellator() {
        if(lastRenderType != null)
            lastRenderType.end(Tessellator.getInstance().getBuilder(), 0, 0, 0);
    }
}
