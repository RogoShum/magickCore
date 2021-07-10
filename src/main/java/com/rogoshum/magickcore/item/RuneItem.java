package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.entity.ManaRuneEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RuneItem extends BaseItem{
    public RuneItem() {
        super(BaseItem.properties.maxStackSize(8));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if(!context.getWorld().isRemote) {
            IEntityState state = context.getPlayer().getCapability(MagickCore.entityState).orElse(null);
            ManaRuneEntity orb = new ManaRuneEntity(ModEntites.mana_rune, context.getWorld());
            orb.setPosition(context.getPos().getX() + 0.5, context.getPos().getY()+1, context.getPos().getZ() + 0.5);
            orb.setElement(state.getElement());
            orb.setTickTime(10000);
            orb.setOwner(context.getPlayer());
            context.getWorld().addEntity(orb);
            context.getItem().shrink(1);
        }
        return super.onItemUse(context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.RUNE_D));
    }
}
