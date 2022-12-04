package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiated.ConeEntity;
import com.rogoshum.magickcore.common.entity.radiated.SquareEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.*;
import java.util.function.Consumer;

public class ConeRadiateRenderer extends EasyRenderer<ConeEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripGlow(3);
    private List<Vector3d> vector3dList = new ArrayList<>();

    public ConeRadiateRenderer(ConeEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        Vector3d dir = Vector3d.ZERO;
        if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        Vector2f rota = getRotationFromVector(dir);
        params.matrixStack.rotate(Vector3f.YP.rotationDegrees(rota.x));
        params.matrixStack.rotate(Vector3f.ZP.rotationDegrees(rota.y));
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
            List<Vector3d> vector3dList = new ArrayList<>();
            for (int i = 1; i <= scale; ++i) {
                Vector3d[] vectors = ParticleUtil.drawCone(Vector3d.ZERO, new Vector3d(Direction.UP.toVector3f()).scale(scale), 4.5 * i, i * 2);
                if(i + 1 > scale) {
                    for (Vector3d vec : vectors) {
                        vector3dList.add(vec);
                        vector3dList.add(Vector3d.ZERO);
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
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.SLIME_SHADER), this::render);
        return map;
    }
}
