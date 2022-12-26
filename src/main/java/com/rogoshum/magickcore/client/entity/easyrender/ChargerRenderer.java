package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ChargeEntity;
import com.rogoshum.magickcore.common.entity.radiated.SectorEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ChargerRenderer extends EasyRenderer<ChargeEntity> {
    float scale;
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/charge_circle.png");
    private static final RenderType TYPE = RenderHelper.getTexedOrbGlow(ICON);

    public ChargerRenderer(ChargeEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vector3d dir = entity.getLookVec();

        if(entity.getTarget() != null) {
            dir = getEntityRenderVector(entity.getTarget(), params.partialTicks).add(0, entity.getTarget().getHeight() * 0.5, 0)
                    .subtract(getEntityRenderVector(params.partialTicks).add(0, entity.getHeight() * 0.5, 0));
        } else if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        Vector2f rota = getRotationFromVector(dir);
        params.matrixStack.rotate(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.rotate(Vector3f.ZP.rotationDegrees(rota.y));
        params.matrixStack.push();
        params.matrixStack.translate(0, 0.5, 0);
        params.matrixStack.rotate(Vector3f.YP.rotationDegrees(entity.ticksExisted % 60 * 6));
        params.matrixStack.scale(scale, scale, scale);
        BufferContext bufferContext = BufferContext.create(params.matrixStack, params.buffer, TYPE);
        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight);
        params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderStaticParticle(bufferContext, renderContext);
        params.matrixStack.pop();
        params.matrixStack.rotate(Vector3f.YP.rotationDegrees(entity.ticksExisted % 60 * -6));
        params.matrixStack.push();
        params.matrixStack.translate(0, -0.5, 0);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderStaticParticle(bufferContext, renderContext);
        params.matrixStack.pop();

        if(entity.getTarget() != null) {
            double length = entity.getTarget().getPositionVec().distanceTo(entity.getPositionVec()) - entity.getTarget().getWidth();
            params.matrixStack.push();
            params.matrixStack.translate(0, -length, 0);
            params.matrixStack.scale(scale, scale, scale);
            params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
            RenderHelper.renderStaticParticle(bufferContext, renderContext);
            params.matrixStack.pop();

            for(double dis = length * 0.05; dis / length < scale; dis+=dis) {
                params.matrixStack.rotate(Vector3f.YP.rotationDegrees(entity.ticksExisted % 10 * -36));
                params.matrixStack.push();
                params.matrixStack.translate(0, -dis, 0);
                params.matrixStack.scale(scale, scale, scale);
                params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
                RenderHelper.renderStaticParticle(bufferContext, renderContext);
                params.matrixStack.pop();
                params.matrixStack.scale(0.8f, 0.8f, 0.8f);
            }
        } else {
            int i = 1;
            int count = 1;
            for(double dis = 1.; dis < scale+3; dis+=1) {
                float rotateScale = (1f/ count);
                params.matrixStack.rotate(Vector3f.XP.rotationDegrees(entity.ticksExisted % 360 * rotateScale));
                params.matrixStack.rotate(Vector3f.ZP.rotationDegrees(entity.ticksExisted % 720 * 0.5f * rotateScale));
                params.matrixStack.rotate(Vector3f.YP.rotationDegrees(entity.ticksExisted % 10 * -36 * rotateScale));
                params.matrixStack.scale(0.8f, 0.8f, 0.8f);
                params.matrixStack.push();
                params.matrixStack.translate(0, -dis*i, 0);
                params.matrixStack.scale(scale, scale, scale);
                params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
                RenderHelper.renderStaticParticle(bufferContext, renderContext);
                params.matrixStack.pop();
                i = i - 2*i;
                count++;
            }
        }
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        scale = Math.max(0.1f, entity.ticksExisted / (float)entity.spellContext().tick * 0.001f * entity.spellContext().force * entity.ticksExisted) * 1.5f;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
