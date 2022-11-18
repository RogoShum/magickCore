package com.rogoshum.magickcore.common.extradata.item;

import com.rogoshum.magickcore.common.api.mana.IManaCapacity;
import com.rogoshum.magickcore.common.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.ManaCapacity;
import com.rogoshum.magickcore.common.magick.context.ItemContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.extradata.ItemExtraData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemManaData extends ItemExtraData implements ISpellContext, IManaCapacity {
    private final ManaCapacity capacity;
    private final SpellContext spellContext;
    private final ContextCore contextCore;

    public ItemManaData(ItemStack stack) {
        super(stack);
        spellContext = ItemContext.create(stack);
        contextCore = new ContextCore(stack);
        capacity = new ItemManaCapacity(stack);
        if(stack.hasTag() && stack.getTag().contains(LibRegistry.ITEM_DATA))
            read(stack.getTag().getCompound(LibRegistry.ITEM_DATA));
    }

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
        private final ItemStack stack;

        public ContextCore(ItemStack stack) {
            this.stack = stack;
        }

        public void setDisable(boolean disable) {
            this.disable = disable;
            save();
        }

        public void setHave(boolean haveMagickContext) {
            this.haveMagickContext = haveMagickContext;
            save();
        }

        private CompoundNBT getDataTag() {
            if(stack.hasTag() && stack.getTag().contains(LibRegistry.ITEM_DATA))
                return stack.getTag().getCompound(LibRegistry.ITEM_DATA);
            CompoundNBT tag = new CompoundNBT();
            stack.getOrCreateTag().put(LibRegistry.ITEM_DATA, tag);
            return tag;
        }

        public void save() {
            serialize(getDataTag());
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
            if(tag.contains("haveMagickContext"))
                haveMagickContext = tag.getBoolean("haveMagickContext");
            if(tag.contains("disable"))
                disable = tag.getBoolean("disable");
            save();
        }
    }
}
