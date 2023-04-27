package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.api.magick.context.SpellContext;

public interface IQuadrantEntity {
    int range();
    void magnify(SpellContext context);
}
