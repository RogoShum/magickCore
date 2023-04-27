package com.rogoshum.magickcore.api.magick;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagickElement {
    private final String type;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final DamageSource damage;
    private final float cardinality;
    private final boolean displayOnly;

    public MagickElement(String type, Color primaryColor, Color secondaryColor, DamageSource damage) {
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.damage = damage;
        this.cardinality = 1.0f;
        this.displayOnly = false;
    }

    public MagickElement(String type, Color primaryColor, Color secondaryColor, DamageSource damage, boolean displayOnly) {
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.damage = damage;
        this.cardinality = 1.0f;
        this.displayOnly = displayOnly;
    }

    public MagickElement(String type, Color primaryColor, Color secondaryColor, DamageSource damage, float cardinality) {
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.damage = damage;
        this.cardinality = cardinality;
        this.displayOnly = false;
    }

    public MagickElement(String type, Color primaryColor, Color secondaryColor, DamageSource damage, float cardinality, boolean displayOnly) {
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.damage = damage;
        this.cardinality = cardinality;
        this.displayOnly = displayOnly;
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

    public boolean onlyForDisplay() {
        return displayOnly;
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
