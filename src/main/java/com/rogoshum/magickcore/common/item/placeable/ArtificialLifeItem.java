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
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItem();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItem(), world);
        ArtificialLifeEntity artificialLifeEntity = ModEntities.ARTIFICIAL_LIFE.get().create(world);
        if(createEntity instanceof ArtificialLifeEntity)
            artificialLifeEntity = (ArtificialLifeEntity) createEntity;
        Vector3d pos = Vector3d.copyCentered(blockpos);
        artificialLifeEntity.setPosition(pos.x, pos.y - 0.5, pos.z);
        if (playerentity == null || !playerentity.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }
        world.addEntity(artificialLifeEntity);
    }
}
