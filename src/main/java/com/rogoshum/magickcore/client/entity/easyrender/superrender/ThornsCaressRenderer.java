package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.ThornsCaressEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class ThornsCaressRenderer extends EasyRenderer<ThornsCaressEntity> {
    float preRotate;
    float postRotate;
    float rotate;
    private static final ElementRenderer originRender = ModElements.ORIGIN.getRenderer();
    Queue<Queue<RenderHelper.VertexAttribute>> SPHERE_0;
    Queue<Queue<RenderHelper.VertexAttribute>> SPHERE_1;
    Queue<Queue<RenderHelper.VertexAttribute>> CYLINDER_0;
    Queue<Queue<RenderHelper.VertexAttribute>> CYLINDER_1;
    private static final RenderType CYLINDER = RenderHelper.getTexedCylinderGlint(cylinder_rotate, 1f, 0f);
    private static final RenderType BLANK = RenderHelper.getTexedSphereGlow(blank, 1f, 0f);
    private static final RenderType SPHERE = RenderHelper.getTexedSphereGlow(sphere_rotate, 1f, 0f);
    float degrees;

    public ThornsCaressRenderer(ThornsCaressEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        preRotate = entity.ticksExisted % 11;
        postRotate = (entity.ticksExisted + 1) % 11;
        rotate = MathHelper.lerp(Minecraft.getInstance().getRenderPartialTicks(), preRotate, postRotate);
        degrees = 360f * (rotate / 10);
        Color color = entity.spellContext().element.color();
        SPHERE_0 = RenderHelper.drawSphere(6, new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.renderLight), new RenderHelper.VertexContext(entity.getHitReactions(), 2.10f));
        SPHERE_1 = RenderHelper.drawSphere(6, new RenderHelper.RenderContext(0.9f, color, RenderHelper.renderLight), new RenderHelper.VertexContext(entity.getHitReactions(), 2.10f));
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(1.0f, 1.0f, 1, 1.0f, 8
                , 0.2f, 0.7f, 0.3f, color);
        CYLINDER_0 = RenderHelper.drawCylinder(context, entity.getHitReactions(), 0.5f);
        context = new RenderHelper.CylinderContext(1.0f, 1.0f, 1, 1.0f, 8
                , 0.4f, 1.0f, 0.3f, originRender.getColor());
        CYLINDER_1 = RenderHelper.drawCylinder(context, entity.getHitReactions(), 0.5f);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
    }

    public void renderSphereSlime(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, BLANK), SPHERE_0);
    }

    public void renderSphereOpacity(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, SPHERE), SPHERE_1);
    }

    public void renderCylinderSlime(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(degrees));
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, CYLINDER)
                , CYLINDER_0);
    }

    public void renderCylinderOpacity(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(degrees));
        matrixStackIn.scale(1.1f, 1.1f, 1.1f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, CYLINDER)
                , CYLINDER_1);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(BLANK, LibShaders.slime), this::renderSphereSlime);
        map.put(new RenderMode(SPHERE, LibShaders.opacity), this::renderSphereOpacity);
        map.put(new RenderMode(CYLINDER, LibShaders.slime), this::renderCylinderSlime);
        //map.put(new RenderMode(CYLINDER, LibShaders.opacity), this::renderCylinderOpacity);
        return map;
    }
}
