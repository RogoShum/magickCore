package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.client.item.ContextPointerRenderer;
import com.rogoshum.magickcore.common.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ContextPointerItem extends EntityItem {
    public ContextPointerItem() {
        super(properties().stacksTo(32).setISTER(() -> ContextPointerRenderer::new));
    }

    @Override
    public void placeEntity(BlockItemUseContext context) {
        BlockPos blockpos = context.getClickedPos();
        World world = context.getLevel();
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItemInHand(), world);
        ContextPointerEntity contextPointer = ModEntities.CONTEXT_POINTER.get().create(world);
        if(createEntity instanceof ContextPointerEntity)
            contextPointer = (ContextPointerEntity) createEntity;
        Vector3d pos = Vector3d.atCenterOf(blockpos);
        contextPointer.setPos(pos.x, pos.y - 0.5, pos.z);
        contextPointer.setOwner(playerentity);
        if (playerentity == null || !playerentity.abilities.instabuild) {
            itemstack.shrink(1);
        }
        world.addFreshEntity(contextPointer);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.CONTEXT_POINTER);
        }
    }
}
