package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.client.item.ManaCapacityRenderer;
import com.rogoshum.magickcore.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.item.buff.EntityItem;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ManaCapacityItem extends EntityItem {
    public ManaCapacityItem() {
        super(properties().maxStackSize(1).setISTER(() -> ManaCapacityRenderer::new));
    }

    @Override
    public void placeEntity(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItem();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItem(), world);
        ManaCapacityEntity manaCapacity = ModEntities.mana_capacity.get().create(world);
        if(createEntity instanceof ManaCapacityEntity)
            manaCapacity = (ManaCapacityEntity) createEntity;
        Vector3d pos = Vector3d.copyCentered(blockpos);
        manaCapacity.setPosition(pos.x, pos.y - 0.5, pos.z);
        manaCapacity.setOwner(playerentity);
        if (playerentity == null || !playerentity.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        world.addEntity(manaCapacity);
    }
}
