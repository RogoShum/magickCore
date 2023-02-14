package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.ObjectRegistry;
import com.rogoshum.magickcore.common.registry.elementmap.ElementFunctions;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class ModElements {
    public static final Color ORIGIN_COLOR = Color.create(1.0f, 1.0f, 1.0f);
    public static final Color SOLAR_COLOR = Color.create(1.0f, 0.6f, 0.3f);
    public static final Color VOID_COLOR = Color.create(0.25f, 0.0f, 1.0f);
    public static final Color ARC_COLOR = Color.create(0.68f, 1.0f, 1.0f);

    public static final Color STASIS_COLOR = Color.create(0.6f, 0.6f, 1.0f);
    public static final Color WITHER_COLOR = Color.create(0.3f, 0.7f, 0.2f);
    public static final Color TAKEN_COLOR = Color.create(0.5f, 0.5f, 0.5f);

    public static final Color AIR_COLOR = Color.create(0.8f, 0.8f, 1.0f);

    public static final MagickElement ORIGIN = new MagickElement(LibElements.ORIGIN, ORIGIN_COLOR, DamageSource.MAGIC);

    public static final MagickElement SOLAR = new MagickElement(LibElements.SOLAR, SOLAR_COLOR, ModDamages.getSolarDamage());
    public static final MagickElement ARC = new MagickElement(LibElements.ARC, ARC_COLOR, ModDamages.getArcDamage());
    public static final MagickElement VOID = new MagickElement(LibElements.VOID, VOID_COLOR, ModDamages.getVoidDamage());

    public static final MagickElement STASIS = new MagickElement(LibElements.STASIS, STASIS_COLOR, ModDamages.getStasisDamage());
    public static final MagickElement WITHER = new MagickElement(LibElements.WITHER, WITHER_COLOR, ModDamages.getWitherDamage());
    public static final MagickElement TAKEN = new MagickElement(LibElements.TAKEN, TAKEN_COLOR, ModDamages.getTakenDamage());

    public static final List<String> elements = new ArrayList<>();

    public static void registerElement() {
        ObjectRegistry<MagickElement> elements = new ObjectRegistry<>(LibRegistry.ELEMENT);
        elements.register(ORIGIN.type(), ORIGIN);
        elements.register(SOLAR.type(), SOLAR);
        elements.register(ARC.type(), ARC);
        elements.register(VOID.type(), VOID);
        elements.register(STASIS.type(), STASIS);
        elements.register(WITHER.type(), WITHER);
        elements.register(TAKEN.type(), TAKEN);

        ObjectRegistry<ElementFunctions> function = new ObjectRegistry<>(LibRegistry.ELEMENT_FUNCTION);
        elements.registry().forEach( (elementType,v) -> {
            ModElements.elements.add(elementType);
            function.register(elementType, ElementFunctions.create());
        });
    }
}
