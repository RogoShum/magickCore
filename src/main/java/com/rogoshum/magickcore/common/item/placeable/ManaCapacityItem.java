package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.client.item.ManaCapacityRenderer;
import com.rogoshum.magickcore.common.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ManaCapacityItem extends EntityItem {
    public ManaCapacityItem() {
        super(properties().setISTER(() -> ManaCapacityRenderer::new));
    }

    @Override
    public void placeEntity(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        Player playerentity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItemInHand(), world);
        ManaCapacityEntity manaCapacity = ModEntities.MANA_CAPACITY.get().create(world);
        if(createEntity instanceof ManaCapacityEntity)
            manaCapacity = (ManaCapacityEntity) createEntity;
        Vec3 pos = Vec3.atCenterOf(blockpos);
        manaCapacity.setPos(pos.x, pos.y - 0.5, pos.z);
        manaCapacity.setOwner(playerentity);
        if (playerentity == null || !playerentity.abilities.instabuild) {
            itemstack.shrink(1);
        }
        world.addFreshEntity(manaCapacity);
    }
}
