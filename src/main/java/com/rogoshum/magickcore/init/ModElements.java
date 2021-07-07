package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.element.*;
import net.minecraft.util.DamageSource;

import java.util.HashMap;

public class ModElements {
    private static HashMap<String, IManaElement> elements = new HashMap<String, IManaElement>();
    private static MagickElement ORIGIN = new OriginElement(LibElements.ORIGIN, new OriginElement.OriginAbility(DamageSource.MAGIC));

    private static MagickElement SOLAR = new SolarElement(LibElements.SOLAR, new SolarElement.SolarAbility(ModDamage.getSolarDamage()));
    private static MagickElement ARC = new ArcElement(LibElements.ARC, new ArcElement.ArcAbility(ModDamage.getArcDamage()));
    private static MagickElement VOID = new VoidElement(LibElements.VOID, new VoidElement.VoidAbility(ModDamage.getVoidDamage()));

    private static MagickElement STASIS = new StasisElement(LibElements.STASIS, new StasisElement.StasisAbility(ModDamage.getStasisDamage()));
    private static MagickElement WITHER = new WitherElement(LibElements.WITHER, new WitherElement.WitherAbility(ModDamage.getWitherDamage()));
    private static MagickElement TAKEN = new TakenElement(LibElements.TAKEN, new TakenElement.TakenAbility(ModDamage.getTakenDamage()));

    public static void putElementsIn()
    {
        ModElements.putElementIn(LibElements.ORIGIN, ORIGIN);
        ModElements.putElementIn(LibElements.SOLAR, SOLAR);
        ModElements.putElementIn(LibElements.ARC, ARC);
        ModElements.putElementIn(LibElements.VOID, VOID);
        ModElements.putElementIn(LibElements.STASIS, STASIS);
        ModElements.putElementIn(LibElements.WITHER, WITHER);
        ModElements.putElementIn(LibElements.TAKEN, TAKEN);
    }

    public static void putElementIn(String name, IManaElement element)
    {
        elements.put(name, element);
    }

    public static IManaElement getElement(String name)
    {
        if (elements.containsKey(name))
            return elements.get(name);
            return elements.get(LibElements.ORIGIN);
    }

    public static IManaElement getElementRandom()
    {
        IManaElement[] els = new IManaElement[elements.size()];
        elements.values().toArray(els);
        return els[MagickCore.rand.nextInt(els.length)];
    }
}
