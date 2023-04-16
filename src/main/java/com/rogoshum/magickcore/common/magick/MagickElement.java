package com.rogoshum.magickcore.common.magick;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagickElement {
    private final String type;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final DamageSource damage;
    private final float cardinality;

    public MagickElement(String type, Color primaryColor, Color secondaryColor, DamageSource damage) {
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.damage = damage;
        this.cardinality = 1.0f;
    }

    public MagickElement(String type, Color primaryColor, Color secondaryColor, DamageSource damage, float cardinality) {
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.damage = damage;
        this.cardinality = cardinality;
    }

    public Color primaryColor() {
        return primaryColor;
    }

    public Color secondaryColor() {
        return secondaryColor;
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
