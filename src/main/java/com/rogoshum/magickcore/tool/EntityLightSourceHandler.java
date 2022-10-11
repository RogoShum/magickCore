package com.rogoshum.magickcore.tool;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.block.FakeAirBlock;
import com.rogoshum.magickcore.block.FakeFluidBlock;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLightSourceHandler {
    private static final List<ILightSourceEntity> lightList = new ArrayList<>();
    private static final ConcurrentHashMap<DimensionType, ConcurrentHashMap<BlockPos, ILightSourceEntity>> posMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<PosState, World> updateBlock = new ConcurrentHashMap<>();

    public static void tick(LogicalSide side) {
        if(side.isClient() && Minecraft.getInstance().world == null) return;

        for (int i = 0; i < lightList.size(); ++i) {
            ILightSourceEntity entity = lightList.get(i);
            if(entity == null) {
                continue;
            }

            BlockPos pos = entityPos(entity);
            if (!entity.alive() || (side.isClient() && entity.world().getDimensionType() != Minecraft.getInstance().world.getDimensionType()))
                lightList.remove(entity);
            else {
                ConcurrentHashMap<BlockPos, ILightSourceEntity> map = posMap.get(entity.world().getDimensionType());
                boolean containKey = false;
                for (BlockPos pos1 : map.keySet()) {
                    try{
                        if (pos1.equals(pos)){
                            containKey = true;
                            if(map.get(pos1) != null && map.get(pos1).getSourceLight() < entity.getSourceLight())
                                map.put(pos, entity);
                        }
                    }
                    catch (Exception ignored) {

                    }
                }

                if (!containKey) {
                    map.put(pos, entity);
                }

                tryAddLightSource(entity, pos);
                /*boolean flag1 = false;

                if(!colorCache.containsKey(entity.world().getDimensionType()))
                    colorCache.put(entity.world().getDimensionType(), new HashMap<>());

                HashMap<BlockPos, Color> colorHashMap = colorCache.get(entity.world().getDimensionType());
                for (BlockPos pos1 : colorHashMap.keySet()) {
                    if (pos1.equals(pos)) {
                        flag1 = true;
                        Color color = colorHashMap.get(pos1);
                        if(!color.equals(new Color(entity.getColor()[0], entity.getColor()[1], entity.getColor()[2]))) {
                            colorHashMap.put(pos, new Color(entity.getColor()[0], entity.getColor()[1], entity.getColor()[2]));
                            flag = true;
                        }
                    }
                }

                if(!flag1){
                    colorHashMap.put(pos, new Color(entity.getColor()[0], entity.getColor()[1], entity.getColor()[2]));
                }

                if(side.isClient() && flag)
                    notifyBlockUpdate(entity.world(), pos, entity.getSourceLight());

               */
            }
        }

        for (DimensionType type : posMap.keySet()) {
            ConcurrentHashMap<BlockPos, ILightSourceEntity> map = posMap.get(type);

            Iterator<BlockPos> posIterator = map.keySet().iterator();
            while (posIterator.hasNext()) {
                BlockPos pos = posIterator.next();

                ILightSourceEntity entity = map.get(pos);
                if(entity == null){
                    posIterator.remove();
                    continue;
                }

                if (!pos.equals(entityPos(entity)) || !entity.alive() || (side.isClient() && entity.world().getDimensionType() != Minecraft.getInstance().world.getDimensionType())) {

                    tryRemoveLightSource(entity, pos);
                    posIterator.remove();
                }
            }
        }
    }

    public static boolean isPosLighting(World world, BlockPos pos) {
        return posMap.containsKey(world.getDimensionType()) && posMap.get(world.getDimensionType()).containsKey(pos);
    }

    public static BlockPos entityPos(ILightSourceEntity entity) {
        return new BlockPos(entity.positionVec().add(0, entity.eyeHeight(), 0));
    }

    public static void updateBlockState() {
        updateBlock.forEach(((posState, world) -> world.setBlockState(posState.pos, posState.state)));
        updateBlock.clear();
    }

    private static void tryAddLightSource(ILightSourceEntity entity, BlockPos pos) {
        if (!(entity.world() instanceof ServerWorld)) return;
        Block block = entity.world().getBlockState(pos).getBlock();

        if (block.equals(Blocks.AIR))
            updateBlock.put(new PosState(pos, ModBlocks.fake_air.get().withLight((int) entity.getSourceLight())), entity.world());

        if (block.equals(Blocks.CAVE_AIR))
            updateBlock.put(new PosState(pos, ModBlocks.fake_cave_air.get().withLight((int) entity.getSourceLight())), entity.world());

        if (block.equals(Blocks.WATER))
            updateBlock.put(new PosState(pos, ModBlocks.fake_water.get().withLightAndFluid((int) entity.getSourceLight(), entity.world().getBlockState(pos).get(FlowingFluidBlock.LEVEL))), entity.world());
    }

    private static void tryRemoveLightSource(ILightSourceEntity entity, BlockPos pos) {
        if (!(entity.world() instanceof ServerWorld)) return;
        Block block = entity.world().getBlockState(pos).getBlock();

        if (block.equals(ModBlocks.fake_air.get()))
            updateBlock.put(new PosState(pos, Blocks.AIR.getDefaultState()), entity.world());

        if (block.equals(ModBlocks.fake_cave_air.get()))
            updateBlock.put(new PosState(pos, Blocks.CAVE_AIR.getDefaultState()), entity.world());

        if (block.equals(ModBlocks.fake_water.get()))
            updateBlock.put(new PosState(pos, Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, entity.world().getBlockState(pos).get(FlowingFluidBlock.LEVEL))), entity.world());
    }

    public static void addLightSource(ILightSourceEntity entity) {
        if (!lightList.contains(entity) && entity.getSourceLight() > 0) {
            lightList.add(entity);

            if (!posMap.containsKey(entity.world().getDimensionType()))
                posMap.put(entity.world().getDimensionType(), new ConcurrentHashMap<>());
        }
    }

    public static List<ILightSourceEntity> getLightList() {
        return lightList;
    }

    public static void clear(){
        lightList.clear();
        posMap.clear();
    }

    public static class PosState {
        public final BlockPos pos;
        public final BlockState state;

        public PosState(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            return pos.equals(o);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }
}
