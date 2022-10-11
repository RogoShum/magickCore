package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.magick.materials.*;

import java.util.HashMap;

public class ManaMaterials {
    private static final HashMap<String, Material> material = new HashMap();
    public static final Material NONE = new Material();

    public static void init() {
        registerMaterial(NONE);
        registerMaterial(new OriginMaterial());
        registerMaterial(new EnderDragonMaterial());
        registerMaterial(new NetherStarMaterial());
        registerMaterial(new ThunderMaterial());
    }

    public static void registerMaterial(Material manaLimit) {
        if(!material.containsKey(manaLimit.getName()))
            material.put(manaLimit.getName(), manaLimit);
        else try {
            throw new Exception("Containing same input on the map = [" + manaLimit.getName() +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Material getMaterial(String s) {
        if(material.containsKey(s))
            return material.get(s);

        return NONE;
    }

    public static Material getLastMaterial() {
        Material[] els = new Material[material.size()];
        material.values().toArray(els);
        return els[material.size() - 1];
    }

    public static Material getMaterialRandom() {
        Material[] els = new Material[material.size()];
        material.values().toArray(els);
        return els[MagickCore.rand.nextInt(els.length)];
    }
}
