package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.List;

public class EntityLightSourceManager {
    private static final List<ILightSourceEntity> lightList = new ArrayList<>();

    public static void tick(LogicalSide side) {
        if(side.isClient() && Minecraft.getInstance().level == null) return;

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

    public static ILightSourceEntity getPosLighting(Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof GlowAirTileEntity) {
            return ((GlowAirTileEntity) tile).getLight();
        }
        return null;
    }

    public static BlockPos entityPos(ILightSourceEntity entity) {
        return new BlockPos(entity.positionVec().add(0, entity.eyeHeight(), 0));
    }

    public static void tryAddLightSource(ILightSourceEntity entity, BlockPos pos) {
        if (!(entity.world() instanceof ServerLevel)) return;
        BlockState state = entity.world().getBlockState(pos);
        Block block = state.getBlock();
        boolean done = false;
        if (block.equals(Blocks.AIR)) {
            done = true;
            entity.world().setBlockAndUpdate(pos, ModBlocks.FAKE_AIR.get().withLight((int) entity.getSourceLight()));
        }

        if (block.equals(Blocks.CAVE_AIR)) {
            done = true;
            entity.world().setBlockAndUpdate(pos, ModBlocks.FAKE_CAVE_AIR.get().withLight((int) entity.getSourceLight()));
        }

        if (block.equals(Blocks.WATER)) {
            done = true;
            entity.world().setBlockAndUpdate(pos, ModBlocks.FAKE_WATER.get().withLightAndFluid((int) entity.getSourceLight(), entity.world().getBlockState(pos).getValue(LiquidBlock.LEVEL)));
        }

        if(done) {
            BlockEntity tile = entity.world().getBlockEntity(pos);
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
