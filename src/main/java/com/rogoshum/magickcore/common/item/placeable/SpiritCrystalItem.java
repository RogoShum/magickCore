package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.common.block.MagickCraftingBlock;
import com.rogoshum.magickcore.client.item.SpiritCrystalItemRenderer;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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
