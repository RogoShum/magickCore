package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.block.MagickCraftingBlock;
import com.rogoshum.magickcore.client.item.SpiritCrystalItemRenderer;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.item.material.ManaEnergyItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class SpiritCrystalItem extends PlaceableEntityItem {
    public static String[][] CRAFTING_RECIPE = {
            {"s", "", "s"},
            {"", "", ""},
            {"s", "", "s"}
    };
    public SpiritCrystalItem() {
        super(BaseItem.properties().setISTER(() -> SpiritCrystalItemRenderer::new), 0.25f, 0.25f);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.getThrowerId() == null) return false;
        PlayerEntity player = entity.world.getPlayerByUuid(entity.getThrowerId());
        if(player == null) return false;
        NBTTagHelper.PlayerData playerData = NBTTagHelper.PlayerData.playerData(player);
        List<ItemEntity> entities = entity.world.getEntitiesWithinAABB(EntityType.ITEM, entity.getBoundingBox().grow(1), Entity::isAlive);
        for(ItemEntity item : entities) {
            if(playerData.getLimit() < 6 && item.getItem().getItem() == Items.DIAMOND) {
                item.getItem().shrink(1);
                entity.getItem().shrink(1);
                playerData.setLimit(Math.min(playerData.getLimit() + 1, 6));
                entity.world.playSound((PlayerEntity)null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModSounds.soft_buildup.get(), SoundCategory.PLAYERS, 0.25F, 2.0F);
                ParticleUtil.spawnRaiseParticle(entity.world, player.getPositionVec(), 2, ModElements.ORIGIN, ParticleType.PARTICLE);
                ParticleUtil.spawnBlastParticle(entity.world, entity.getPositionVec(), 1, ModElements.ORIGIN, ParticleType.PARTICLE);
            } else if(playerData.getLimit() < 9 && item.getItem().getItem() == Items.MAGMA_CREAM) {
                item.getItem().shrink(1);
                entity.getItem().shrink(1);
                playerData.setLimit(Math.min(playerData.getLimit() + 1, 9));
                entity.world.playSound((PlayerEntity)null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModSounds.soft_buildup.get(), SoundCategory.PLAYERS, 0.25F, 2.0F);
                ParticleUtil.spawnRaiseParticle(entity.world, player.getPositionVec(), 2, ModElements.ORIGIN, ParticleType.PARTICLE);
                ParticleUtil.spawnBlastParticle(entity.world, entity.getPositionVec(), 1, ModElements.ORIGIN, ParticleType.PARTICLE);
            } else if(playerData.getLimit() < 12 && item.getItem().getItem() == Items.CHORUS_FRUIT) {
                item.getItem().shrink(1);
                entity.getItem().shrink(1);
                playerData.setLimit(Math.min(playerData.getLimit() + 1, 12));
                entity.world.playSound((PlayerEntity)null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), ModSounds.soft_buildup.get(), SoundCategory.PLAYERS, 0.25F, 2.0F);
                ParticleUtil.spawnRaiseParticle(entity.world, player.getPositionVec(), 2, ModElements.ORIGIN, ParticleType.PARTICLE);
                ParticleUtil.spawnBlastParticle(entity.world, entity.getPositionVec(), 1, ModElements.ORIGIN, ParticleType.PARTICLE);
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if(entityIn instanceof ServerPlayerEntity)
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) entityIn, LibAdvancements.ELEMENT_CRYSTAL);
    }

    @Override
    public void placeEntity(BlockItemUseContext context) {
        Vector3d pos = context.getHitVec();
        Direction direction = context.getFace();
        Vector3d offset = Vector3d.copy(direction.getDirectionVec()).scale(WIDTH);
        if(direction.getAxis().isVertical()) {
            if(direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE))
                offset = Vector3d.ZERO;
            else
                offset = Vector3d.copy(direction.getDirectionVec()).scale(HEIGHT);
        }
        pos.add(offset);
        PlaceableItemEntity entity = placeEntity(context.getWorld(), context.getItem(), direction, pos);
        if(entity != null)
            entity.world.addEntity(entity);
        BlockPos crafting = validCrafting(context.getWorld(), new BlockPos(pos), false);
        if(crafting != null) {
            context.getWorld().setBlockState(crafting, ModBlocks.MAGICK_CRAFTING.get().getDefaultState());
            if(context.getPlayer() instanceof ServerPlayerEntity) {
                entity.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 0.5f, 0.0f);
                AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) context.getPlayer(), LibAdvancements.MAGICK_CRAFTING);
            }
        }
    }

    public static BlockPos validCrafting(World world, BlockPos pos, boolean center) {
        if(center) {
            if(validCrafting(world, pos.add(-1, 0, -1)))
                return pos;
            return null;
        } else if(validCrafting(world, pos)) {
            return pos.add(1, 0, 1);
        } else if(validCrafting(world, pos.add(-2, 0, 0))) {
            return pos.add(-1, 0, 1);
        } else if(validCrafting(world, pos.add(0, 0, -2))) {
            return pos.add(1, 0, -1);
        } else if(validCrafting(world, pos.add(-2, 0, -2))) {
            return pos.add(-1, 0, -1);
        }

        return null;
    }

    private static boolean validCrafting(World world, BlockPos pos) {
        for(int i = 0; i < 3; ++i) {
            for (int c = 0; c < 3; ++c) {
                BlockPos offset = pos.add(i, 0, c);
                if(!world.isAirBlock(offset) && !(world.getBlockState(offset).getBlock() instanceof MagickCraftingBlock))
                    return false;
                String pattern = CRAFTING_RECIPE[i][c];
                if(pattern.equals("s")) {
                    if(world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(offset), (entity) -> entity instanceof PlaceableItemEntity && ((PlaceableItemEntity) entity).getItemStack().getItem() instanceof SpiritCrystalItem).isEmpty())
                        return false;
                }
            }
        }
        return true;
    }
}
