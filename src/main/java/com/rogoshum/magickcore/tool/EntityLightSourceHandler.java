package com.rogoshum.magickcore.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLightSourceHandler {
    private static final List<ILightSourceEntity> lightList = new ArrayList<>();
    private static final ConcurrentHashMap<BlockPos, ILightSourceEntity> posMap = new ConcurrentHashMap<>();

    public static void tick(){
        for(int i = 0; i < lightList.size(); ++i){
            ILightSourceEntity entity = lightList.get(i);
            BlockPos pos = entityPos(entity);
            if(!entity.isAlive() || (entity.getEntityWorld().isRemote && entity.getEntityWorld() != Minecraft.getInstance().world))
                lightList.remove(entity);
            else if(!posMap.containsKey(pos) || posMap.get(pos).getSourceLight() < entity.getSourceLight()) {
                posMap.put(pos, entity);
                tryAddLightSource(entity, pos);
            }
        }

        Iterator<BlockPos> it = posMap.keySet().iterator();
        while (it.hasNext()){
            BlockPos pos = it.next();
            ILightSourceEntity entity = posMap.get(pos);
            if(!pos.equals(entityPos(entity)) || !entity.isAlive() || (entity.getEntityWorld().isRemote && entity.getEntityWorld() != Minecraft.getInstance().world)){
                tryRemoveLightSource(entity, pos);
                it.remove();
            }
        }
    }

    public static boolean isPosLighting(BlockPos pos){
        return posMap.containsKey(pos);
    }

    public static BlockPos entityPos(ILightSourceEntity entity){
        return new BlockPos(entity.getPositionVec().add(0, entity.getEyeHeight(), 0));
    }

    private static void tryAddLightSource(ILightSourceEntity entity, BlockPos pos){
        Block block = entity.getEntityWorld().getBlockState(pos).getBlock();
        if(block.equals(Blocks.AIR) || block.equals(ModBlocks.fake_air.get()))
            entity.getEntityWorld().setBlockState(pos, ModBlocks.fake_air.get().withLight(entity.getSourceLight()));

        if(block.equals(Blocks.CAVE_AIR) || block.equals(ModBlocks.fake_cave_air.get()))
            entity.getEntityWorld().setBlockState(pos, ModBlocks.fake_cave_air.get().withLight(entity.getSourceLight()));

        if(block.equals(Blocks.WATER) || block.equals(ModBlocks.fake_water.get()))
            entity.getEntityWorld().setBlockState(pos, ModBlocks.fake_water.get().withLight(entity.getSourceLight()));
    }

    private static void tryRemoveLightSource(ILightSourceEntity entity, BlockPos pos){
        Block block = entity.getEntityWorld().getBlockState(pos).getBlock();

        if(block.equals(ModBlocks.fake_air.get()))
            entity.getEntityWorld().setBlockState(pos, Blocks.AIR.getDefaultState());

        if(block.equals(ModBlocks.fake_cave_air.get()))
            entity.getEntityWorld().setBlockState(pos, Blocks.CAVE_AIR.getDefaultState());

        if(block.equals(ModBlocks.fake_water.get()))
            entity.getEntityWorld().setBlockState(pos, Blocks.WATER.getDefaultState());
    }

    public static void addLightSource(ILightSourceEntity entity){
        if(entity.getEntityWorld() instanceof ServerWorld && !lightList.contains(entity)){
            lightList.add(entity);
        }
    }
}
