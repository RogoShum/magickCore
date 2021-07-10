package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.client.element.ElementRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IManaElement {
    public String getType();
    @OnlyIn(Dist.CLIENT)
    public ElementRenderer getRenderer();

    public IManaAbility getAbility();
}
