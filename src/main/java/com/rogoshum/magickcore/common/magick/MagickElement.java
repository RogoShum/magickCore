package com.rogoshum.magickcore.common.magick;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagickElement {
    private final String type;
    private final Color color;
    private final DamageSource damage;
    private final float cardinality;

    public MagickElement(String type, Color color, DamageSource damage) {
        this.type = type;
        this.color = color;
        this.damage = damage;
        this.cardinality = 1.0f;
    }

    public MagickElement(String type, Color color, DamageSource damage, float cardinality) {
        this.type = type;
        this.color = color;
        this.damage = damage;
        this.cardinality = cardinality;
    }

    public Color color() {
        return color;
    }

    public DamageSource damageType() {
        return damage;
    }

    public String type() {
        return type;
    }
    public float cardinality() {
        return cardinality;
    }

    @OnlyIn(Dist.CLIENT)
    public ElementRenderer getRenderer() {
        return MagickCore.proxy.getElementRender(type);
    }
}
