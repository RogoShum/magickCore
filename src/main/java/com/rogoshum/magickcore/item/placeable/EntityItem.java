package com.rogoshum.magickcore.item.placeable;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.item.BaseItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public abstract class EntityItem extends BaseItem {
    public EntityItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ActionResultType actionresulttype = this.tryPlace(new BlockItemUseContext(context));
        return !actionresulttype.isSuccessOrConsume() && this.isFood() ? this.onItemRightClick(context.getWorld(), context.getPlayer(), context.getHand()).getType() : actionresulttype;
    }

    public ActionResultType tryPlace(BlockItemUseContext context) {
        if (!context.canPlace()) {
            return ActionResultType.FAIL;
        } else {
            if (!context.getWorld().isAirBlock(context.getPos())) {
                return ActionResultType.FAIL;
            } else {
                placeEntity(context);
                return ActionResultType.func_233537_a_(context.getWorld().isRemote());
            }
        }
    }

    public abstract void placeEntity(BlockItemUseContext context);
}
