package com.rogoshum.magickcore.client.entity.render.living;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.living.TimeManagerEntity;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;

public class TimeManagerRenderer extends EntityRenderer<TimeManagerEntity> {

    protected TimeManagerRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(TimeManagerEntity p_114482_) {
        return null;
    }
}
