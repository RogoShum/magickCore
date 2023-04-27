package com.rogoshum.magickcore.api.registry.elementmap;

import com.rogoshum.magickcore.api.registry.ElementMap;
import com.rogoshum.magickcore.client.render.RenderParams;

import java.util.function.Consumer;

public class RenderFunctions extends ElementMap<String, Consumer<RenderParams>> {
    private RenderFunctions() {
    }

    public static RenderFunctions create() {
        return new RenderFunctions();
    }

    public boolean apply(String type, RenderParams attribute) {
        if (elementMap.containsKey(type)) {
            elementMap.get(type).accept(attribute);

            return true;
        }

        return false;
    }
}
