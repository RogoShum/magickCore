package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.api.magick.context.SpellContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Material {
    public String getName() {
        return "none";
    }

    public float getForce() {
        return 0;
    }

    public int getTick() {
        return 0;
    }

    public float getRange() {
        return 0;
    }

    public int getMana() {
        return 0;
    }

    public Item getItem() {
        return Items.AIR;
    }

    public void limit(SpellContext spellContext) {
        if(spellContext.range > getRange())
            spellContext.range(getRange());
        if(spellContext.force > getForce())
            spellContext.force(getForce());
        if(spellContext.tick > getTick())
            spellContext.tick(getTick());
    }
}
