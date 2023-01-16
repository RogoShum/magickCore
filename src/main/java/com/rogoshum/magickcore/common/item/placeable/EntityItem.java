package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.common.item.BaseItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;

public abstract class EntityItem extends BaseItem {
    public EntityItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult actionresulttype = this.tryPlace(new BlockPlaceContext(context));
        return !actionresulttype.consumesAction() && this.isEdible() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : actionresulttype;
    }

    public InteractionResult tryPlace(BlockPlaceContext context) {
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            if (!context.getLevel().isEmptyBlock(context.getClickedPos())) {
                return InteractionResult.FAIL;
            } else {
                placeEntity(context);
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }
        }
    }

    public abstract void placeEntity(BlockPlaceContext context);
}
