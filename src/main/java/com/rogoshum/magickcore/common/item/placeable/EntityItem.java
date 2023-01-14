package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.common.item.BaseItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

import net.minecraft.item.Item.Properties;

public abstract class EntityItem extends BaseItem {
    public EntityItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ActionResultType actionresulttype = this.tryPlace(new BlockItemUseContext(context));
        return !actionresulttype.consumesAction() && this.isEdible() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : actionresulttype;
    }

    public ActionResultType tryPlace(BlockItemUseContext context) {
        if (!context.canPlace()) {
            return ActionResultType.FAIL;
        } else {
            if (!context.getLevel().isEmptyBlock(context.getClickedPos())) {
                return ActionResultType.FAIL;
            } else {
                placeEntity(context);
                return ActionResultType.sidedSuccess(context.getLevel().isClientSide());
            }
        }
    }

    public abstract void placeEntity(BlockItemUseContext context);
}
