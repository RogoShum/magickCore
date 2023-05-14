package com.rogoshum.magickcore.common.util;

import com.google.common.collect.Queues;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.init.CommonConfig;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class EntityLightSourceManager {
    private static final Queue<ILightSourceEntity> LIGHT_LIST = Queues.newConcurrentLinkedQueue();

    public static void tick() {
        if(LIGHT_LIST.isEmpty()) return;
        Iterator<ILightSourceEntity> it = LIGHT_LIST.iterator();
        while (it.hasNext()) {
            ILightSourceEntity entity = it.next();
            if(entity == null) {
                continue;
            }

            if(!entity.alive()) {
                it.remove();
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
        BlockEntity tile = entity.world().getBlockEntity(pos);
        if(tile instanceof GlowAirTileEntity) {
            ((GlowAirTileEntity) tile).setLight(entity);
            return;
        }
        BlockState state = entity.world().getBlockState(pos);
        Block block = state.getBlock();
        if (block.equals(Blocks.AIR)) {
            entity.world().setBlockAndUpdate(pos, ModBlocks.FAKE_AIR.get().withLight((int) entity.getSourceLight()));
        }

        if (block.equals(Blocks.CAVE_AIR)) {
            entity.world().setBlockAndUpdate(pos, ModBlocks.FAKE_CAVE_AIR.get().withLight((int) entity.getSourceLight()));
        }

        if (block.equals(Blocks.WATER)) {
            entity.world().setBlockAndUpdate(pos, ModBlocks.FAKE_WATER.get().withLightAndFluid((int) entity.getSourceLight(), entity.world().getBlockState(pos).getValue(LiquidBlock.LEVEL)));
        }
    }

    public static void addLightSource(ILightSourceEntity entity) {
        if (CommonConfig.ENTITY_LIGHTING.get() && !entity.world().isClientSide() && !LIGHT_LIST.contains(entity) && (entity.getSourceLight() > 0 || entity.getSourceLight() < 0)) {
            LIGHT_LIST.add(entity);
        }
    }

    public static Queue<ILightSourceEntity> getLightList() {
        return LIGHT_LIST;
    }

    public static void clear(){
        LIGHT_LIST.clear();
    }
}
