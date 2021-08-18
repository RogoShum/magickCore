package com.rogoshum.magickcore.magick;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.enums.EnumManaType;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.function.Function;

public class ElementFunction {
    private final HashMap<EnumManaType, Function<ReleaseAttribute, Boolean>> functionMap = new HashMap<>();

    private ElementFunction(){};

    public static ElementFunction create(){
        return new ElementFunction();
    }

    public ElementFunction add(EnumManaType type, Function<ReleaseAttribute, Boolean> function){
        this.functionMap.put(type, function);
        return this;
    }

    public boolean applyElementFunction(EnumManaType type, ReleaseAttribute attribute)
    {
        if(functionMap.containsKey(type))
        {
            ElementEvent.ElementFunctionApply event = new ElementEvent.ElementFunctionApply(type, attribute);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled())
                return functionMap.get(type).apply(event.getReleaseAttribute());
        }

        return false;
    }
}
