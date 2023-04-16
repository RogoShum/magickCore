package com.rogoshum.magickcore.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.mixin.AccessorRenderType;
import com.rogoshum.magickcore.mixin.AccessorTexture;
import com.rogoshum.magickcore.mixin.AccessorTextureState;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class SingleBuffer implements MultiBufferSource {
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
    public VertexConsumer getBuffer(@Nonnull RenderType p_getBuffer_1_) {
        if(p_getBuffer_1_ == RenderType.glintDirect()
                || p_getBuffer_1_ == RenderType.glint()
                || p_getBuffer_1_ == RenderType.armorGlint()
                || p_getBuffer_1_ == RenderType.armorEntityGlint()
                || p_getBuffer_1_ == RenderType.entityGlintDirect()) {
            if(!GlintBuffer.building())
                GlintBuffer.begin(p_getBuffer_1_.mode(), p_getBuffer_1_.format());
            return GlintBuffer;
        }

        RenderType temp = null;
        if(useDefaultTexture) {
            temp = type.apply(this.texture);
        } else {
            try {
                RenderType.CompositeState state = ((AccessorRenderType)p_getBuffer_1_).getState();
                RenderStateShard.EmptyTextureStateShard textureState = ((AccessorTextureState)(Object)state).getTextureState();
                Optional<ResourceLocation> texture = ((AccessorTexture)textureState).getTexture();
                if(texture.isPresent()) {
                    temp = type.apply(texture.get());
                } else
                    temp = type.apply(this.texture);
            } catch (Exception ignored) {

            }
        }

        if(temp != null)
            begin(temp.mode() == p_getBuffer_1_.mode() && temp.format().equals(p_getBuffer_1_.format()) ? temp : p_getBuffer_1_);
        else
            begin(p_getBuffer_1_);
        return Tesselator.getInstance().getBuilder();
    }

    public void begin(RenderType renderType) {
        if(this.lastRenderType != null) {
            finishTessellator();
        } else if(Tesselator.getInstance().getBuilder().building())
            Tesselator.getInstance().getBuilder().end();
        this.lastRenderType = renderType;
        if(!Tesselator.getInstance().getBuilder().building())
            Tesselator.getInstance().getBuilder().begin(renderType.mode(), renderType.format());
    }

    public void finish() {
        if(lastRenderType != null)
            lastRenderType.end(Tesselator.getInstance().getBuilder(), 0, 0, 0);
        if(GlintBuffer.building()) {
            RenderType.glintDirect().end(GlintBuffer, 0, 0, 0);
        }
    }

    public void finishTessellator() {
        if(lastRenderType != null)
            lastRenderType.end(Tesselator.getInstance().getBuilder(), 0, 0, 0);
    }
}
