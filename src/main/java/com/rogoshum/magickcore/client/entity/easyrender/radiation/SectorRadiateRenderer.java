package com.rogoshum.magickcore.client.entity.easyrender.radiation;

import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiation.SectorEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class SectorRadiateRenderer extends EasyRenderer<SectorEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripGlow(3);
    private List<Vec3> vector3dList = new ArrayList<>();

    public SectorRadiateRenderer(SectorEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vec3 dir = Vec3.ZERO;
        if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        Vec2 rota = getRotationFromVector(dir);
        params.matrixStack.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rota.y + 90));
        if(!vector3dList.isEmpty())
            RenderHelper.renderPoint(
                    BufferContext.create(params.matrixStack, params.buffer, TYPE)
                    , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight)
                    , vector3dList);
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        if(this.vector3dList.isEmpty()) {
            scale = entity.getRange();
            List<Vec3> vector3dList = new ArrayList<>();
            Vec3[] vectors = ParticleUtil.drawCone(Vec3.ZERO, new Vec3(Direction.UP.step()).scale(scale), 90.0, (int) Math.max(3, scale) * 2);
            for (Vec3 vec : vectors) {
                vector3dList.add(vec);
                vector3dList.add(Vec3.ZERO);
                vector3dList.add(vec);
            }
            int left = vector3dList.size()%4;
            for (int i = 0; i < left; ++i) {
                vector3dList.add(vectors[0]);
            }
            this.vector3dList = vector3dList;
        }
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::render);
        return map;
    }
}
