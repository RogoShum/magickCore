package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class PsiSpellContext extends ItemContext {
    public static final Type<PsiSpellContext> TYPE = new Type<>(LibContext.PSI_SPELL);
    public int manaCost;

    public static PsiSpellContext create() {
        return new PsiSpellContext();
    }

    public static PsiSpellContext create(ItemStack stack, int manaCost) {
        PsiSpellContext context = new PsiSpellContext();
        context.itemStack = stack;
        context.manaCost = manaCost;
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        super.serialize(tag);
        tag.putInt("manaCost", manaCost);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        new NBTTagHelper(tag).ifContainInt("manaCost", (manaCost) -> this.manaCost = manaCost);
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public Type<PsiSpellContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return itemStack.getDisplayName().getString() + " Mana: " + manaCost;
    }
}
