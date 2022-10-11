package com.rogoshum.magickcore.magick.extradata.item;

import com.rogoshum.magickcore.api.IManaCapacity;
import com.rogoshum.magickcore.api.IManaContextItem;
import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.magick.ManaCapacity;
import com.rogoshum.magickcore.magick.ManaData;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.extradata.ItemExtraData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemManaData extends ItemExtraData implements ISpellContext, IManaCapacity {
    private final ManaCapacity capacity = ManaCapacity.create(5000);
    private final SpellContext spellContext = SpellContext.create();
    private final ContextCore contextCore = new ContextCore();

    @Override
    public boolean isItemSuitable(ItemStack item) {
        return item.getItem() instanceof IManaData;
    }

    @Override
    public void read(CompoundNBT nbt) {
        spellContext.deserialize(nbt);
        capacity.deserialize(nbt);
        contextCore.deserialize(nbt);
    }

    @Override
    public void write(CompoundNBT nbt) {
        spellContext.serialize(nbt);
        capacity.serialize(nbt);
        contextCore.serialize(nbt);
    }

    @Override
    public void fixData(ItemStack stack) {

    }

    @Override
    public ManaCapacity manaCapacity() {
        return capacity;
    }
    public ContextCore contextCore() {
        return contextCore;
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }

    public static class ContextCore {
        private boolean disable;
        private boolean haveMagickContext;
        public void setDisable(boolean disable) {
            this.disable = disable;
        }

        public void setHave(boolean haveMagickContext) {
            this.haveMagickContext = haveMagickContext;
        }


        public boolean haveMagickContext() {
            return this.haveMagickContext;
        }
        public boolean isDisable() {
            return this.disable;
        }

        public void serialize(CompoundNBT tag) {
            tag.putBoolean("haveMagickContext", haveMagickContext);
            tag.putBoolean("disable", disable);
        }

        public void deserialize(CompoundNBT tag) {
            haveMagickContext = tag.getBoolean("haveMagickContext");
            disable = tag.getBoolean("disable");
        }
    }
}
