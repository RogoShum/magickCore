package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.registry.elementmap.ElementFunctions;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;

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

    private static final MagickElement SOLAR = new MagickElement(LibElements.SOLAR, SOLAR_COLOR, ModDamage.getSolarDamage());
    private static final MagickElement ARC = new MagickElement(LibElements.ARC, ARC_COLOR, ModDamage.getArcDamage());
    private static final MagickElement VOID = new MagickElement(LibElements.VOID, VOID_COLOR, ModDamage.getVoidDamage());

    private static final MagickElement STASIS = new MagickElement(LibElements.STASIS, STASIS_COLOR, ModDamage.getStasisDamage());
    private static final MagickElement WITHER = new MagickElement(LibElements.WITHER, WITHER_COLOR, ModDamage.getWitherDamage());
    private static final MagickElement TAKEN = new MagickElement(LibElements.TAKEN, TAKEN_COLOR, ModDamage.getTakenDamage());
    private static final MagickElement AIR = new MagickElement(LibElements.AIR, AIR_COLOR, ModDamage.getTakenDamage());

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
        elements.register(AIR.type(), AIR);

        ObjectRegistry<ElementFunctions> function = new ObjectRegistry<>(LibRegistry.ELEMENT_FUNCTION);
        elements.registry().forEach( (elementType,v) -> {
            ModElements.elements.add(elementType);
            function.register(elementType, ElementFunctions.create());
        });
    }
}
