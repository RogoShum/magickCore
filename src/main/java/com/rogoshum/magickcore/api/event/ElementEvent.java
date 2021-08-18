package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.magick.ElementFunction;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraftforge.eventbus.api.Event;

public class ElementEvent {
    public static class ElementFunctionRegistryEvent extends Event{
        private final ElementFunction function;

        public ElementFunctionRegistryEvent(ElementFunction function)
        {
            this.function = function;
        }

        public ElementFunction getFunction()
        {
            return function;
        }
    }

    public static class ElementFunctionApply extends Event{
        private final EnumManaType type;
        private final ReleaseAttribute attribute;

        public ElementFunctionApply(EnumManaType type, ReleaseAttribute attribute)
        {
            this.type = type;
            this.attribute = attribute;
        }

        public EnumManaType getManaType()
        {
            return type;
        }

        public ReleaseAttribute getReleaseAttribute()
        {
            return attribute;
        }

        public boolean isCancelable()
        {
            return true;
        }
    }
}
