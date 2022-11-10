package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.client.item.ContextPointerRenderer;
import com.rogoshum.magickcore.common.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ContextPointerItem extends EntityItem {
    public ContextPointerItem() {
        super(properties().maxStackSize(32).setISTER(() -> ContextPointerRenderer::new));
    }

    @Override
    public void placeEntity(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItem();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItem(), world);
        ContextPointerEntity contextPointer = ModEntities.CONTEXT_POINTER.get().create(world);
        if(createEntity instanceof ContextPointerEntity)
            contextPointer = (ContextPointerEntity) createEntity;
        Vector3d pos = Vector3d.copyCentered(blockpos);
        contextPointer.setPosition(pos.x, pos.y - 0.5, pos.z);
        contextPointer.setOwner(playerentity);
        if (playerentity == null || !playerentity.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        world.addEntity(contextPointer);
    }
}
