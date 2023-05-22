package com.rogoshum.magickcore.api.registry.elementmap;

import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.registry.ElementMap;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Function;

public class ElementFunctions extends ElementMap<ApplyType, Function<MagickContext, Boolean>> {
    private ElementFunctions(){}

    public static ElementFunctions create(){
        return new ElementFunctions();
    }

    public boolean applyElementFunction(MagickContext context) {
        if(elementMap.containsKey(context.applyType())) {
            ElementEvent.ElementFunctionApply event = new ElementEvent.ElementFunctionApply(context);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled())
                return elementMap.get(context.applyType()).apply(event.getMagickContext());
        }

        return false;
    }
}
