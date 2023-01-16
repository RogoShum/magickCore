package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.client.item.ManaCapacityRenderer;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
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

public class ArtificialLifeItem extends EntityItem {
    public ArtificialLifeItem() {
        super(properties());
    }

    @Override
    public void placeEntity(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        Player playerentity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItemInHand(), world);
        ArtificialLifeEntity artificialLifeEntity = ModEntities.ARTIFICIAL_LIFE.get().create(world);
        if(createEntity instanceof ArtificialLifeEntity)
            artificialLifeEntity = (ArtificialLifeEntity) createEntity;
        Vec3 pos = Vec3.atCenterOf(blockpos);
        artificialLifeEntity.setPos(pos.x, pos.y - 0.5, pos.z);
        if (playerentity == null || !playerentity.abilities.instabuild) {
            itemstack.shrink(1);
        }
        world.addFreshEntity(artificialLifeEntity);
    }
}
