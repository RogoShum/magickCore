package com.rogoshum.magickcore.client.entity.easyrender.radiation;

import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiation.ConeEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

public class ConeRadiateRenderer extends EasyRenderer<ConeEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripGlow(3);
    private List<Vec3> vector3dList = new ArrayList<>();

    public ConeRadiateRenderer(ConeEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vec3 dir = Vec3.ZERO;
        if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        Vec2 rota = getRotationFromVector(dir);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        if(!vector3dList.isEmpty())
            RenderHelper.renderPoint(
                BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(RenderMode.ShaderList.SLIME_SHADER)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight)
                , vector3dList);
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getRange();
        if(this.vector3dList.isEmpty()) {
            List<Vec3> vector3dList = new ArrayList<>();
            for (int i = 1; i <= scale; ++i) {
                Vec3[] vectors = ParticleUtil.drawCone(Vec3.ZERO, new Vec3(Direction.UP.step()).scale(scale), 4.5 * i, i * 2);
                if(i + 1 > scale) {
                    for (Vec3 vec : vectors) {
                        vector3dList.add(vec);
                        vector3dList.add(Vec3.ZERO);
                        vector3dList.add(vec);
                    }
                } else
                    Collections.addAll(vector3dList, vectors);
            }
            this.vector3dList = vector3dList;
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
