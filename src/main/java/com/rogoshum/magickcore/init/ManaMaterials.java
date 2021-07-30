package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaLimit;
import com.rogoshum.magickcore.lib.LibMaterial;
import com.rogoshum.magickcore.magick.materials.EnderDragonMaterial;
import com.rogoshum.magickcore.magick.materials.NetherStarMaterial;
import com.rogoshum.magickcore.magick.materials.OriginMaterial;
import com.rogoshum.magickcore.magick.materials.ThunderMaterial;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ManaMaterials {
    private static final HashMap<String, IManaLimit> material = new HashMap();
    public static final IManaLimit NONE = new IManaLimit() {
        @Override
        public String getName() {
            return "none";
        }

        @Override
        public float getForce() {
            return 0;
        }

        @Override
        public int getTick() {
            return 0;
        }

        @Override
        public int getRange() {
            return 0;
        }

        @Override
        public int getMana() {
            return 0;
        }
    };

    public static void init()
    {
        putMaterial(NONE);
        putMaterial(new OriginMaterial());
        putMaterial(new EnderDragonMaterial());
        putMaterial(new NetherStarMaterial());
        putMaterial(new ThunderMaterial());
    }

    public static void putMaterial(IManaLimit manaLimit)
    {
        if(!material.containsKey(manaLimit.getName()))
            material.put(manaLimit.getName(), manaLimit);
        else try {
            throw new Exception("Containing same input on the map = [" + manaLimit.getName() +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IManaLimit getMaterial(String s)
    {
        if(material.containsKey(s))
            return material.get(s);

        return NONE;
    }

    public static IManaLimit getLastMaterial()
    {
        IManaLimit[] els = new IManaLimit[material.size()];
        material.values().toArray(els);
        return els[material.size() - 1];
    }

    public static IManaLimit getMaterialRandom()
    {
        IManaLimit[] els = new IManaLimit[material.size()];
        material.values().toArray(els);
        return els[MagickCore.rand.nextInt(els.length)];
    }
}
