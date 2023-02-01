package com.rogoshum.magickcore.api.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ILayersRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    List<RenderLayer<T, M>> getLayers();

    void invokeScale(T livingEntity, PoseStack poseStack, float f);
}
