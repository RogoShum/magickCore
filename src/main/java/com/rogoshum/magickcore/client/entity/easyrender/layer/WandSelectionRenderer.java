package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class WandSelectionRenderer extends EasyRenderer<PlayerEntity> {
    Color color = Color.ORIGIN_COLOR;
    protected final HashMap<Vector3d, VoxelShape> shapes = new HashMap<>();
    protected final HashSet<Vector3d> posSet = new HashSet<>();
    protected static final RenderType BLOCK_TYPE = RenderHelper.getLineStripPC(3);

    public WandSelectionRenderer(PlayerEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        shapes.clear();
        posSet.clear();
        if(entity.getHeldItemMainhand().getItem() instanceof WandItem) {
            HashSet<Vector3d> vector3ds = NBTTagHelper.getVectorSet(entity.getHeldItemMainhand().getOrCreateChildTag(WandItem.SET_KEY));
            for(Vector3d vec : vector3ds) {
                addPos(vec, cam);
            }
        }
        int rLimit = 120;
        int rHalf = rLimit / 2;
        int gLimit = 240;
        int gHalf = gLimit / 2;
        int bLimit = 360;
        int bHalf = bLimit / 2;
        int r = MagickCore.proxy.getRunTick() % rLimit;
        int g = MagickCore.proxy.getRunTick() % gLimit;
        int b = MagickCore.proxy.getRunTick() % bLimit;
        if(r > rHalf)
            r = rHalf - (r-rHalf);
        if(g > gHalf)
            g = gHalf - (g-gHalf);
        if(b > bHalf)
            b = bHalf - (b-bHalf);
        color = Color.create(r/(float)(rLimit-1), g/(float)(gLimit-1), b/(float)(bLimit-1));
    }

    public void addPos(Vector3d vec, Vector3d cam) {
        BlockPos pos = new BlockPos(vec);
        BlockState state = entity.world.getBlockState(pos);
        VoxelShape blockShape = state.getShape(entity.world, pos);
        VoxelShape shape = VoxelShapes.fullCube();
        if(!blockShape.isEmpty())
            shape = blockShape;
        Vector3d offsetPos = new Vector3d(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        posSet.add(offsetPos);
        shapes.put(offsetPos, shape);
    }

    public void renderBlock(RenderParams params) {
        for (Vector3d pos : posSet) {
            if(shapes.containsKey(pos)) {
                VoxelShape shape = shapes.get(pos);
                RenderHelper.drawShape(params.matrixStack, params.buffer, shape, pos.x, pos.y, pos.z, color.r(), color.g(), color.b(), 1.0F);
            }
        }
    }

    @Override
    public boolean forceRender() {
        return entity.getHeldItemMainhand().getItem() instanceof WandItem;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(!(entity.getHeldItemMainhand().getItem() instanceof WandItem) || posSet.isEmpty() || shapes.isEmpty()) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(BLOCK_TYPE), this::renderBlock);
        return map;
    }
}
