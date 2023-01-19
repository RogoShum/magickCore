package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.common.magick.context.MagickContext;
public class ElementEvent {
    public static class ElementFunctionApply extends Event {
        private final MagickContext attribute;

        public ElementFunctionApply(MagickContext attribute) {
            this.attribute = attribute;
        }

        public MagickContext getMagickContext() {
            return attribute;
        }

        public boolean isCancelable()
        {
            return true;
        }
    }
}
