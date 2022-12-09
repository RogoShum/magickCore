package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.BloodBubbleEntity;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.function.Consumer;

public class BloodBubbleRenderer extends EasyRenderer<BloodBubbleEntity> {
    public BloodBubbleRenderer(BloodBubbleEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        entity.renderFrame(Minecraft.getInstance().getRenderPartialTicks());
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
