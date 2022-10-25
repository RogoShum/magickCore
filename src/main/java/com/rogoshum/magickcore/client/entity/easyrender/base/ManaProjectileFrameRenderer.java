package com.rogoshum.magickcore.client.entity.easyrender.base;

import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaProjectileFrameRenderer extends EasyRenderer<ManaProjectileEntity>{
    public ManaProjectileFrameRenderer(ManaProjectileEntity entity) {
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
