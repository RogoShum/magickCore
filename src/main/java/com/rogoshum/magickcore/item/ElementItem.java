package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
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
            if(!state.getElement().getType().equals(element)) {
                state.setElement(ModElements.getElement(element));
                playerIn.getHeldItem(handIn).shrink(1);
                playerIn.world.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, playerIn.getSoundCategory(), 1.5f, 1.0f);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
