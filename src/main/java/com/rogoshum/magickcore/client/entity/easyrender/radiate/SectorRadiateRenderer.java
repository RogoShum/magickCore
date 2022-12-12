package com.rogoshum.magickcore.client.entity.easyrender.radiate;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiated.SectorEntity;
import com.rogoshum.magickcore.common.entity.radiated.SquareEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class SectorRadiateRenderer extends EasyRenderer<SectorEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripGlow(3);
    private List<Vector3d> vector3dList = new ArrayList<>();

    public SectorRadiateRenderer(SectorEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vector3d dir = Vector3d.ZERO;
        if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        Vector2f rota = getRotationFromVector(dir);
        params.matrixStack.rotate(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.rotate(Vector3f.ZP.rotationDegrees(rota.y + 90));
        if(!vector3dList.isEmpty())
            RenderHelper.renderPoint(
                    BufferContext.create(params.matrixStack, params.buffer, TYPE)
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
        if(this.vector3dList.isEmpty()) {
            scale = entity.getRange();
            scale = Math.max(3, scale);
            List<Vector3d> vector3dList = new ArrayList<>();
            Vector3d[] vectors = ParticleUtil.drawCone(Vector3d.ZERO, new Vector3d(Direction.UP.toVector3f()).scale(scale), 90.0, (int) scale * 2);
            for (Vector3d vec : vectors) {
                vector3dList.add(vec);
                vector3dList.add(Vector3d.ZERO);
                vector3dList.add(vec);
            }
            vector3dList.add(vectors[0]);
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