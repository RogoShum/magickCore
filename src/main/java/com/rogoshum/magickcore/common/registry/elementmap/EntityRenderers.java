package com.rogoshum.magickcore.common.registry.elementmap;

import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.registry.ElementMap;

import java.util.function.Consumer;

public class EntityRenderers extends ElementMap<String, Consumer<RenderParams>> {
    private EntityRenderers() {
    }

    public static EntityRenderers create() {
        return new EntityRenderers();
    }

    public boolean apply(String type, RenderParams attribute) {
        if (elementMap.containsKey(type)) {
            elementMap.get(type).accept(attribute);

            return true;
        }

        return false;
    }
}
