package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ElementCrystalBlock extends CropsBlock{
    public ElementCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockState withAge(int age) {
        return super.withAge(age);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElementCrystalTileEntity();
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return ModItems.element_crystal_seeds.get();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof ILightSourceEntity){
            return (int) ((ILightSourceEntity) tile).getSourceLight();
        }
        return 0;
    }
}
