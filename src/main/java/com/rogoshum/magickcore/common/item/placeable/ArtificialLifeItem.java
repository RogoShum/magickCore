package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.client.item.ManaCapacityRenderer;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ArtificialLifeItem extends EntityItem {
    public ArtificialLifeItem() {
        super(properties());
    }

    @Override
    public void placeEntity(BlockItemUseContext context) {
        BlockPos blockpos = context.getClickedPos();
        World world = context.getLevel();
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItemInHand(), world);
        ArtificialLifeEntity artificialLifeEntity = ModEntities.ARTIFICIAL_LIFE.get().create(world);
        if(createEntity instanceof ArtificialLifeEntity)
            artificialLifeEntity = (ArtificialLifeEntity) createEntity;
        Vector3d pos = Vector3d.atCenterOf(blockpos);
        artificialLifeEntity.setPos(pos.x, pos.y - 0.5, pos.z);
        if (playerentity == null || !playerentity.abilities.instabuild) {
            itemstack.shrink(1);
        }
        world.addFreshEntity(artificialLifeEntity);
    }
}
