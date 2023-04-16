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
    public static final Color ORIGIN_COLOR_SEC = Color.create(1.0f, 1.0f, 1.0f);
    public static final Color SOLAR_COLOR = Color.create(1.0f, 0.6f, 0.3f);
    public static final Color SOLAR_COLOR_SEC = Color.create(1.0f, 0.8f, 0.7f);
    public static final Color VOID_COLOR = Color.create(0.25f, 0.0f, 1.0f);
    public static final Color VOID_COLOR_SEC = Color.create(0.5f, 0.3f, 1.0f);
    public static final Color ARC_COLOR = Color.create(0.3f, 0.8f, 1.0f);
    public static final Color ARC_COLOR_SEC = Color.create(0.68f, 1.0f, 1.0f);

    public static final Color STASIS_COLOR = Color.create(0.4f, 0.4f, 1.0f);
    public static final Color STASIS_COLOR_SEC = Color.create(0.6f, 0.6f, 1.0f);
    public static final Color WITHER_COLOR = Color.create(0.3f, 0.7f, 0.2f);
    public static final Color WITHER_COLOR_SEC = Color.create(0.7f, 0.8f, 0.3f);
    public static final Color TAKEN_COLOR = Color.create(0.5f, 0.5f, 0.5f);
    public static final Color TAKEN_COLOR_SEC = Color.create(0.6f, 0.7f, 0.7f);

    public static final Color PSI_COLOR = Color.create(0.5f, 0.7f, 1.0f);
    public static final Color PSI_COLOR_SEC = Color.create(0.7f, 0.9f, 1.0f);
    public static final Color BOTANIA_COLOR = Color.create(0.6f, 0.8f, 0.3f);
    public static final Color BOTANIA_COLOR_SEC = Color.create(0.6f, 0.9f, 0.55f);

    public static final MagickElement ORIGIN = new MagickElement(LibElements.ORIGIN, ORIGIN_COLOR, ORIGIN_COLOR_SEC, DamageSource.MAGIC);

    public static final MagickElement SOLAR = new MagickElement(LibElements.SOLAR, SOLAR_COLOR, SOLAR_COLOR_SEC, ModDamages.getSolarDamage());
    public static final MagickElement ARC = new MagickElement(LibElements.ARC, ARC_COLOR, ARC_COLOR_SEC, ModDamages.getArcDamage());
    public static final MagickElement VOID = new MagickElement(LibElements.VOID, VOID_COLOR, VOID_COLOR_SEC, ModDamages.getVoidDamage());

    public static final MagickElement STASIS = new MagickElement(LibElements.STASIS, STASIS_COLOR, STASIS_COLOR_SEC, ModDamages.getStasisDamage());
    public static final MagickElement WITHER = new MagickElement(LibElements.WITHER, WITHER_COLOR, WITHER_COLOR_SEC, ModDamages.getWitherDamage());
    public static final MagickElement TAKEN = new MagickElement(LibElements.TAKEN, TAKEN_COLOR, TAKEN_COLOR_SEC, ModDamages.getTakenDamage());
    public static final MagickElement PSI = new MagickElement(LibElements.PSI, PSI_COLOR, PSI_COLOR_SEC, ModDamages.getPsiDamage());
    public static final MagickElement BOTANIA = new MagickElement(LibElements.BOTANIA, BOTANIA_COLOR, BOTANIA_COLOR_SEC, ModDamages.getBotaniaDamage());

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
