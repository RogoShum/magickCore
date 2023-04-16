package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaItemDurationBarRenderer extends EasyRenderer<ItemEntity> {
    Color color = Color.ORIGIN_COLOR;
    float percentage;
    boolean render;

    public ManaItemDurationBarRenderer(ItemEntity entity) {
        super(entity);
    }

    @Override
    public boolean alive() {
        return super.alive() && (entity.tickCount < 5 || entity.getItem().getItem() instanceof IManaData);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
    }

    @Override
    public void update() {
        super.update();

        if(entity.getItem().getItem() instanceof IManaData && entity.getItem().getItem().isBarVisible(entity.getItem())) {
            render = true;
            ItemManaData data = ExtraDataUtil.itemManaData(entity.getItem());
            color = data.spellContext().element.primaryColor();
            percentage = data.manaCapacity().getMana() / data.manaCapacity().getMaxMana();
            if(RenderHelper.showDebug())
                updateSpellContext();
        } else
            render = false;
    }

    public void render(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        matrixStackIn.pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        matrixStackIn.translate(0, 0.5f, 0);
        this.draw(bufferbuilder, matrixStackIn.last().pose(), 1.05f, 0.02f, 0, 0, 0);
        this.draw(bufferbuilder, matrixStackIn.last().pose(), percentage, 0, color.r(), color.g(), color.b());
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        matrixStackIn.popPose();
    }

    private void draw(BufferBuilder renderer, Matrix4f matrix, float width, float height, float red, float green, float blue) {
        width *= 0.25f;
        height += 0.03f;
        height *= 0.5;
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(matrix, -width, -height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        renderer.vertex(matrix, -width, height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        renderer.vertex(matrix, width, height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        renderer.vertex(matrix, width, -height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        Tesselator.getInstance().end();
    }

    @Override
    protected void renderDebug(RenderParams renderParams) {
        if(entity.getItem().getItem() instanceof IManaData) {
            renderSpellContext(renderParams);
        }
    }

    @Override
    protected void updateSpellContext() {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        Vec3 offset = cam.subtract(x, y, z).normalize().scale(entity.getBbWidth() * 0.5);
        debugX = x - camX + offset.x;
        debugY = y - camY + entity.getBbHeight() + offset.y;
        debugZ = z - camZ + offset.z;

        String information = ExtraDataUtil.itemManaData(entity.getItem()).spellContext().toString();
        if(information.isEmpty())  {
            debugSpellContext = null;
            return;
        }
        debugSpellContext = information.split("\n");
        contextLength = 0;
        if(debugSpellContext.length < 1) return;
        for (String s : debugSpellContext) {
            if (s.length() > contextLength)
                contextLength = s.length();
        }
        debugY += debugSpellContext.length * 0.1;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(!render) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getDebugFunction() {
        if(!(entity.getItem().getItem() instanceof IManaData)) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::renderDebug);
        return map;
    }
}
