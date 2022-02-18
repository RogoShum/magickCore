package com.rogoshum.magickcore.tool;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.block.FakeAirBlock;
import com.rogoshum.magickcore.block.FakeFluidBlock;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModBlocks;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLightSourceHandler {
    private static final List<ILightSourceEntity> lightList = new ArrayList<>();
    private static final ConcurrentHashMap<DimensionType, ConcurrentHashMap<BlockPos, ILightSourceEntity>> posMap = new ConcurrentHashMap<>();
    private static final HashMap<Chunk, BlockPos> updateChunkMap = new HashMap<>();
    private static final HashMap<DimensionType, HashMap<BlockPos, Color>> colorCache = new HashMap<>();

    public static void tick(LogicalSide side) {
        if(side.isClient() && Minecraft.getInstance().world == null) return;

        for (int i = 0; i < lightList.size(); ++i) {
            ILightSourceEntity entity = lightList.get(i);

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

                boolean flag = tryAddLightSource(entity, pos);
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

                    boolean flag = tryRemoveLightSource(entity, pos);

                    //if(side.isClient() && flag)
                        //notifyBlockUpdate(entity.world(), pos, entity.getSourceLight());
                    posIterator.remove();
                }
            }
        }

        //if(side.isClient())
            //reRenderChunk(Minecraft.getInstance().world);
    }

    /*@OnlyIn(Dist.CLIENT)
    public static void reRenderChunk(World world){
        if(Minecraft.getInstance().world == null || world.getDimensionType() != Minecraft.getInstance().world.getDimensionType()) return;
        Object object = ObfuscationReflectionHelper.getPrivateValue(ClientWorld.class, Minecraft.getInstance().world, "field_217430_d");
        if (object instanceof WorldRenderer) {
            WorldRenderer renderer = (WorldRenderer) object;
            Object object1 = ObfuscationReflectionHelper.getPrivateValue(WorldRenderer.class, renderer, "field_175008_n");
            if (object1 instanceof ViewFrustum) {
                updateChunkMap.forEach(((chunk, pos) -> {
                    BlockState state = world.getBlockState(pos);
                    renderer.notifyBlockUpdate(world, pos, state, state, 8);
                }));
                updateChunkMap.clear();
                //MagickCore.LOGGER.debug(" " + updateChunkMap.keySet().size());
            }
        }
    }*/

    public static void renderLightColor(MatrixStack matrixStack, BufferBuilder builder) {
        /*for (int i = 0; i < lightList.size(); ++i) {
            ILightSourceEntity entity = lightList.get(i);
            matrixStack.push();
            double x = entity.positionVec().x;
            double y = entity.positionVec().y;
            double z = entity.positionVec().z;

            Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
            double camX = cam.x, camY = cam.y, camZ = cam.z;
            matrixStack.translate(x - camX, y - camY + entity.eyeHeight() / 2, z - camZ);

            matrixStack.scale(-entity.getSourceLight(), -entity.getSourceLight(), -entity.getSourceLight());
            RenderHelper.renderSphere(BufferPackage.create(matrixStack, builder,
                    RenderHelper.getTexedSphere(RenderHelper.blankTex)).useShader(LibShaders.bloom)
                    , 8, 1.0f, entity.getColor(), RenderHelper.renderLight);
            matrixStack.pop();
        }*/
    }

    @OnlyIn(Dist.CLIENT)
    public static void notifyBlockUpdate(World world, BlockPos pos, int range){
        if(Minecraft.getInstance().world == null || world.getDimensionType() != Minecraft.getInstance().world.getDimensionType()) return;
        for(int x = -range; x <= range; x+=range){
            for(int y = -range; y <= range; y+=range) {
                for (int z = -range; z <= range; z+=range) {
                    updateChunkMap.put(world.getChunkAt(pos.add(x, y, z)), pos.add(x, y, z));
                }
            }
        }
    }

    public static boolean isPosLighting(World world, BlockPos pos) {
        return posMap.containsKey(world.getDimensionType()) && posMap.get(world.getDimensionType()).containsKey(pos);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean shouldRenderColor(World world, Vector3d pos) {
        if (posMap.containsKey(world.getDimensionType())) {
            ConcurrentHashMap<BlockPos, ILightSourceEntity> map = posMap.get(world.getDimensionType());
            Iterator<BlockPos> posIterator = map.keySet().iterator();
            while (posIterator.hasNext()) {
                BlockPos pos1 = posIterator.next();
                if (!(world.getBlockState(pos1).getBlock() instanceof FakeFluidBlock) && !(world.getBlockState(pos1).getBlock() instanceof FakeAirBlock))
                    continue;

                ILightSourceEntity entity = map.get(pos1);
                if(entity == null) continue;
                float distance = (float) pos.distanceTo(Vector3d.copyCentered(pos1));
                float maxDis = entity.getSourceLight();
                if (distance <= maxDis + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static float[] getLightColor(World world, Vector3d pos, float r, float g, float b, int combinedLightsIn) {
        float[] color = {0f, 0f, 0f};
        int count = 0;
        if (posMap.containsKey(world.getDimensionType())) {
            ConcurrentHashMap<BlockPos, ILightSourceEntity> map = posMap.get(world.getDimensionType());
            Iterator<BlockPos> posIterator = map.keySet().iterator();
            while (posIterator.hasNext()) {
                BlockPos pos1 = posIterator.next();
                TileEntity tileEntity = world.getTileEntity(pos1);
                if (!(world.getBlockState(pos1).getBlock() instanceof FakeFluidBlock)
                        && !(world.getBlockState(pos1).getBlock() instanceof FakeAirBlock)
                        && !(tileEntity instanceof ILightSourceEntity))
                    continue;

                BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(Vector3d.copyCentered(pos1), pos, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null));
                if(result.getType() == RayTraceResult.Type.BLOCK) continue;

                ILightSourceEntity entity = map.get(pos1);
                if(entity == null) continue;
                float distance = (float) pos.distanceTo(Vector3d.copyCentered(pos1));
                float maxDis = entity.getSourceLight();
                if (distance <= maxDis) {
                    float bright = (1f - distance / maxDis) * 0.65f;

                    color[0] = blend(color[0], (entity.getColor()[0] * bright), count);
                    color[1] = blend(color[1], (entity.getColor()[1] * bright), count);
                    color[2] = blend(color[2], (entity.getColor()[2] * bright), count);

                    count += 1;
                }
            }
        }

        color = packagedColorValue(color, r, g, b, combinedLightsIn);
        return color;
    }

    private static float blend(float baseColor, float blendColor, int count) {
        float scale = (float) count / ((float) count + 1.0f);
        return Math.min(1, scale * baseColor + (1 - scale) * blendColor);
    }

    private static float[] packagedColorValue(float[] color, float r, float g, float b, int combinedLightsIn) {
        float[] packagedColor = {0, 0, 0};

        float[] lightingHSB = Color.RGBtoHSB((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255), null);
        float[] blockHSB = Color.RGBtoHSB((int) (r * 255), (int) (g * 255), (int) (b * 255), null);
        float scale = (combinedLightsIn / (float) (RenderHelper.renderLight));
        scale *= lightingHSB[2];
        if(scale < 0 ) scale = 0;

        blockHSB[0] = lightingHSB[0] * 1.5f * scale + blockHSB[0] * (1 - scale);
        blockHSB[1] = lightingHSB[1] * scale + blockHSB[1] * (1 - scale);

        Color newColor = new Color(Color.HSBtoRGB(blockHSB[0], blockHSB[1], blockHSB[2]));
        packagedColor[0] = newColor.getRed() / 255F;
        packagedColor[1] = newColor.getGreen() / 255F;
        packagedColor[2] = newColor.getBlue() / 255F;

        return packagedColor;
    }

    public static BlockPos entityPos(ILightSourceEntity entity) {
        return new BlockPos(entity.positionVec().add(0, entity.eyeHeight(), 0));
    }

    private static boolean tryAddLightSource(ILightSourceEntity entity, BlockPos pos) {
        if (!(entity.world() instanceof ServerWorld)) return false;
        Block block = entity.world().getBlockState(pos).getBlock();
        if (block.equals(Blocks.AIR))
            return entity.world().setBlockState(pos, ModBlocks.fake_air.get().withLight(entity.getSourceLight()));

        if (block.equals(Blocks.CAVE_AIR))
            return entity.world().setBlockState(pos, ModBlocks.fake_cave_air.get().withLight(entity.getSourceLight()));

        if (block.equals(Blocks.WATER))
            return entity.world().setBlockState(pos, ModBlocks.fake_water.get().withLightAndFluid(entity.getSourceLight(), entity.world().getBlockState(pos).get(FlowingFluidBlock.LEVEL)));

        return false;
    }

    private static boolean tryRemoveLightSource(ILightSourceEntity entity, BlockPos pos) {
        if (!(entity.world() instanceof ServerWorld)) return false;
        Block block = entity.world().getBlockState(pos).getBlock();

        if (block.equals(ModBlocks.fake_air.get()))
            return entity.world().setBlockState(pos, Blocks.AIR.getDefaultState());

        if (block.equals(ModBlocks.fake_cave_air.get()))
            return entity.world().setBlockState(pos, Blocks.CAVE_AIR.getDefaultState());

        if (block.equals(ModBlocks.fake_water.get()))
            return entity.world().setBlockState(pos, Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, entity.world().getBlockState(pos).get(FlowingFluidBlock.LEVEL)));

        return false;
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
        colorCache.clear();
        updateChunkMap.clear();
    }
}
