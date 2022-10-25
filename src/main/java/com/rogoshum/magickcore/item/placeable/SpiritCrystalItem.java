package com.rogoshum.magickcore.item.placeable;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.MagickCraftingBlock;
import com.rogoshum.magickcore.client.item.SpiritCrystalItemRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.init.ModBlocks;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.item.BaseItem;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.block.AirBlock;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
        placeEntity(context.getWorld(), new ItemStack(this), direction, pos);
        BlockPos crafting = validCrafting(context.getWorld(), new BlockPos(pos), false);
        if(crafting != null) {
            context.getWorld().setBlockState(crafting, ModBlocks.magick_crafting.get().getDefaultState());
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
