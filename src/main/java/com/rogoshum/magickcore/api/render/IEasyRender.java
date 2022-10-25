package com.rogoshum.magickcore.api.render;

import com.rogoshum.magickcore.api.entity.IPositionEntity;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

public interface IEasyRender extends IPositionEntity {
    HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction();
    void update();
    boolean alive();

    default boolean forceRender() {
        return false;
    }
}
