package com.rogoshum.magickcore.client.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Environment(EnvType.CLIENT)
public class MageRenderer extends VillagerRenderer {
    public MageRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, (ReloadableResourceManager) Minecraft.getInstance().getResourceManager());
    }
}
