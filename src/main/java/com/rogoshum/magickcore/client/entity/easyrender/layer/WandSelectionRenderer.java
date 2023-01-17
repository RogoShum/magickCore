package com.rogoshum.magickcore.client.entity.easyrender.layer;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class WandSelectionRenderer extends EasyRenderer<Player> {
    Color color = Color.ORIGIN_COLOR;
    protected final HashMap<Vec3, VoxelShape> shapes = new HashMap<>();
    protected final HashSet<Vec3> posSet = new HashSet<>();
    protected static final RenderType BLOCK_TYPE = RenderHelper.getLineStripPC(3);

    public WandSelectionRenderer(Player entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        shapes.clear();
        posSet.clear();
        if(entity.getMainHandItem().getItem() instanceof WandItem) {
            HashSet<Vec3> vector3ds = NBTTagHelper.getVectorSet(entity.getMainHandItem().getOrCreateTagElement(WandItem.SET_KEY));
            for(Vec3 vec : vector3ds) {
                addPos(vec, cam);
            }
        }
        color = RenderHelper.getRGB();
    }

    public void addPos(Vec3 vec, Vec3 cam) {
        BlockPos pos = new BlockPos(vec);
        BlockState state = entity.level.getBlockState(pos);
        VoxelShape blockShape = state.getShape(entity.level, pos);
        VoxelShape shape = Shapes.block();
        if(!blockShape.isEmpty())
            shape = blockShape;
        Vec3 offsetPos = new Vec3(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        posSet.add(offsetPos);
        shapes.put(offsetPos, shape);
    }

    public void renderBlock(RenderParams params) {
        for (Vec3 pos : posSet) {
            if(shapes.containsKey(pos)) {
                VoxelShape shape = shapes.get(pos);
                params.matrixStack.pushPose();
                RenderHelper.drawShape(params.matrixStack, params.buffer, shape, pos.x, pos.y, pos.z, color.r(), color.g(), color.b(), 1.0F);
                params.matrixStack.popPose();
            }
        }
    }

    @Override
    public boolean forceRender() {
        return entity.getMainHandItem().getItem() instanceof WandItem;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(!(entity.getMainHandItem().getItem() instanceof WandItem) || posSet.isEmpty() || shapes.isEmpty()) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(BLOCK_TYPE), this::renderBlock);
        return map;
    }
}
