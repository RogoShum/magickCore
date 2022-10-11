package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ElementItem extends BaseItem implements IManaMaterial {
    private final String element;
    public ElementItem(String element) {
        super(BaseItem.properties().maxStackSize(8));
        this.element = element;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            ExtraDataHelper.entityData(playerIn).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                if(!state.getElement().type().equals(element)) {
                    state.setElement(MagickRegistry.getElement(element));
                    playerIn.getHeldItem(handIn).shrink(1);
                    playerIn.world.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, playerIn.getSoundCategory(), 1.5f, 1.0f);
                }
            });
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public boolean disappearAfterRead() {
        return true;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 50;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(!data.spellContext().element.type().equals(element)){
            data.spellContext().element(MagickRegistry.getElement(element));
            return true;
        }
        return false;
    }
}
