package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaAbility;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class MagickElement implements IManaElement {
    private String type = LibElements.ORIGIN;
    private ElementAbility ability;

    public MagickElement(String type, ElementAbility ability)
    {
        this.type = type;
        this.ability = ability;
    }

    public String getType()
    {
        return this.type;
    }

    @OnlyIn(Dist.CLIENT)
    public ElementRenderer getRenderer()
    {
        return MagickCore.proxy.getElementRender(this.type);
    }

    public ElementAbility getAbility()
    {
        return this.ability;
    }

    public static abstract class ElementAbility implements IManaAbility
    {
        private DamageSource damage;

        ElementAbility(DamageSource damage)
        {
            this.damage = damage;
        }

        public DamageSource getDamageSource() { return this.damage; }
    }
}
