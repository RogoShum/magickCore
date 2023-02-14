package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.client.item.ContextPointerRenderer;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class ContextPointerItem extends EntityItem {
    public ContextPointerItem() {
        super(properties().stacksTo(32));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new ContextPointerRenderer();
            }
        });
    }

    @Override
    public void placeEntity(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level world = context.getLevel();
        Player playerentity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        Entity createEntity = NBTTagHelper.createEntityByItem(context.getItemInHand(), world);
        ContextPointerEntity contextPointer = ModEntities.CONTEXT_POINTER.get().create(world);
        if(createEntity instanceof ContextPointerEntity)
            contextPointer = (ContextPointerEntity) createEntity;
        Vec3 pos = Vec3.atCenterOf(blockpos);
        contextPointer.setPos(pos.x, pos.y - 0.5, pos.z);
        contextPointer.setOwner(playerentity);
        if (playerentity == null || !playerentity.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        world.addFreshEntity(contextPointer);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.CONTEXT_POINTER);
        }
    }
}
