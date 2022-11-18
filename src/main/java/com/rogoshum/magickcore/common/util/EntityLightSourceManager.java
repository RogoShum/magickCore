package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;
import java.util.List;

public class EntityLightSourceManager {
    private static final List<ILightSourceEntity> lightList = new ArrayList<>();

    public static void tick(LogicalSide side) {
        if(side.isClient() && Minecraft.getInstance().world == null) return;

        for (int i = 0; i < lightList.size(); ++i) {
            ILightSourceEntity entity = lightList.get(i);
            if(entity == null) {
                continue;
            }

            if(!entity.alive()) {
                lightList.remove(entity);
                continue;
            }

            BlockPos pos = entityPos(entity);
            tryAddLightSource(entity, pos);
        }
    }

    public static ILightSourceEntity getPosLighting(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof GlowAirTileEntity) {
            return ((GlowAirTileEntity) tile).getLight();
        }
        return null;
    }

    public static BlockPos entityPos(ILightSourceEntity entity) {
        return new BlockPos(entity.positionVec().add(0, entity.eyeHeight(), 0));
    }

    public static void tryAddLightSource(ILightSourceEntity entity, BlockPos pos) {
        if (!(entity.world() instanceof ServerWorld)) return;
        BlockState state = entity.world().getBlockState(pos);
        Block block = state.getBlock();
        boolean done = false;
        if (block.equals(Blocks.AIR)) {
            done = true;
            entity.world().setBlockState(pos, ModBlocks.fake_air.get().withLight((int) entity.getSourceLight()));
        }

        if (block.equals(Blocks.CAVE_AIR)) {
            done = true;
            entity.world().setBlockState(pos, ModBlocks.fake_cave_air.get().withLight((int) entity.getSourceLight()));
        }

        if (block.equals(Blocks.WATER)) {
            done = true;
            entity.world().setBlockState(pos, ModBlocks.fake_water.get().withLightAndFluid((int) entity.getSourceLight(), entity.world().getBlockState(pos).get(FlowingFluidBlock.LEVEL)));
        }

        if(done) {
            TileEntity tile = entity.world().getTileEntity(pos);
            if(tile instanceof GlowAirTileEntity) {
                ((GlowAirTileEntity) tile).setLight(entity);
                ((GlowAirTileEntity) tile).setState(state);
            }
        }
    }

    public static void addLightSource(ILightSourceEntity entity) {
        if(true)return;
        if (!lightList.contains(entity) && (entity.getSourceLight() > 0 || entity.getSourceLight() < 0)) {
            lightList.add(entity);
        }
    }

    public static List<ILightSourceEntity> getLightList() {
        return lightList;
    }

    public static void clear(){
        lightList.clear();
    }
}
