package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ElementItem extends BaseItem{
    private final String element;
    public ElementItem(String element) {
        super(BaseItem.properties.maxStackSize(8));
        this.element = element;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            IEntityState state = playerIn.getCapability(MagickCore.entityState).orElse(null);
            state.setElement(ModElements.getElement(element));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
