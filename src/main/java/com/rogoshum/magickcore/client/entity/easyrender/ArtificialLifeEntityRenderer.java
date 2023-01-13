package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.living.ArtificialLifeEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class ArtificialLifeEntityRenderer extends EasyRenderer<ArtificialLifeEntity> {
    protected Color color;
    protected final HashMap<Vector3d, VoxelShape> shapes = new HashMap<>();
    protected final HashSet<Vector3d> posSet = new HashSet<>();
    protected static final RenderType BLOCK_TYPE = RenderHelper.getLineStripPC(5);
    protected static final ResourceLocation LASER_TOP = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_top.png");
    protected static final RenderType LASER_TYPE = RenderHelper.getTexedLaser(LASER_TOP);
    public ArtificialLifeEntityRenderer(ArtificialLifeEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        if(Minecraft.getInstance().crosshairPickEntity != entity) return;
        color = entity.spellContext().element.color();
        Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        shapes.clear();
        posSet.clear();
        if(entity.getVectorSet().isEmpty()) {
            addPos(entity.getTracePos(), cam);
        } else {
            for(Vector3d vec : entity.getVectorSet()) {
                addPos(vec, cam);
            }
        }
    }

    public void addPos(Vector3d vec, Vector3d cam) {
        BlockPos pos = new BlockPos(vec);
        BlockState state = entity.level.getBlockState(pos);
        VoxelShape blockShape = state.getShape(entity.level, pos);
        VoxelShape shape = VoxelShapes.block();
        if(!blockShape.isEmpty())
            shape = blockShape;
        Vector3d offsetPos = new Vector3d(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        posSet.add(offsetPos);
        shapes.put(offsetPos, shape);
    }

    @Override
    public boolean forceRender() {
        return Minecraft.getInstance().crosshairPickEntity == entity && entity.isFocus();
    }

    public void renderLaser(RenderParams params) {
        baseOffset(params.matrixStack);
        float scale = 0.25f;
        Vector3d direction = Vector3d.atLowerCornerOf(entity.getDirection().getOpposite().getNormal());
        Vector2f rota = EasyRenderer.getRotationFromVector(direction);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        params.matrixStack.translate(0, 0.4, 0);
        params.matrixStack.scale(scale, scale, scale);
        RenderHelper.renderLaserBottom(
                BufferContext.create(params.matrixStack, params.buffer, LASER_TYPE),
                new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight),
                80
        );
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
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(Minecraft.getInstance().crosshairPickEntity != entity || !entity.isFocus() || posSet.isEmpty() || shapes.isEmpty())
            return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(BLOCK_TYPE), this::renderBlock);
        //map.put(new RenderMode(LASER_TYPE), this::renderLaser);
        return map;
    }
}
