package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.common.api.itemstack.IManaData;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.vector.Matrix4f;

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
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
    }

    @Override
    public void update() {
        super.update();
        if(entity.getItem().getItem() instanceof IManaData && entity.getItem().getItem().showDurabilityBar(entity.getItem())) {
            render = true;
            ItemManaData data = ExtraDataUtil.itemManaData(entity.getItem());
            color = data.spellContext().element.color();
            percentage = data.manaCapacity().getMana() / data.manaCapacity().getMaxMana();
        } else
            render = false;
    }

    public void render(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        matrixStackIn.push();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        matrixStackIn.translate(0, 0.5f, 0);
        this.draw(bufferbuilder, matrixStackIn.getLast().getMatrix(), 1.05f, 0.02f, 0, 0, 0);
        this.draw(bufferbuilder, matrixStackIn.getLast().getMatrix(), percentage, 0, color.r(), color.g(), color.b());
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        matrixStackIn.pop();
    }

    private void draw(BufferBuilder renderer, Matrix4f matrix, float width, float height, float red, float green, float blue) {
        width *= 0.25f;
        height += 0.03f;
        height *= 0.5;
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(matrix, -width, -height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        renderer.pos(matrix, -width, height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        renderer.pos(matrix, width, height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        renderer.pos(matrix, width, -height, 0.0f).color(red, green, blue, (float) 1.0).endVertex();
        Tessellator.getInstance().draw();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        if(!render) return null;
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
