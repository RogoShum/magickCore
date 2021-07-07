package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.capability.IManaItemData;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IManaItem {
    @Nullable
    public IManaItemData getItemData(ItemStack stack);

    public IManaElement getElement(ItemStack stack);
    public void setElement(ItemStack stack, IManaElement manaElement);

    public float getRange(ItemStack stack);
    public void setRange(ItemStack stack, float range);

    public float getForce(ItemStack stack);
    public void setForce(ItemStack stack, float force);

    public float getMana(ItemStack stack);
    public float receiveMana(ItemStack stack, float mana);
    public void setMana(ItemStack stack, float mana);

    public boolean getTrace(ItemStack stack);
    public void setTrace(ItemStack stack, boolean trace);

    public EnumManaType getManaType(ItemStack stack);
    public void setManaType(ItemStack stack, EnumManaType manaType);

    public int getTickTime(ItemStack stack);
    public void setTickTime(ItemStack stack, int tick);

    public float getMaxMana(ItemStack stack);
}
