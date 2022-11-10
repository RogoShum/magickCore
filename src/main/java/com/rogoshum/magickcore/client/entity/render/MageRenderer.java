package com.rogoshum.magickcore.client.entity.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MageRenderer extends VillagerRenderer {
    public MageRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, (IReloadableResourceManager) Minecraft.getInstance().getResourceManager());
    }
}
