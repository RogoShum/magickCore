package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class ManaCapacityRenderer extends EasyItemRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation taken = new ResourceLocation("magickcore:textures/entity/takensphere.png");
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedEntityGlint(RenderHelper.blankTex, 1f, 0f);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedEntityGlint(taken, 1f, 0f);

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        Entity entity = NBTTagHelper.createEntityByItem(stack, Minecraft.getInstance().level);
        ManaCapacityEntity capacity = ModEntities.MANA_CAPACITY.get().create(Minecraft.getInstance().level);
        if(entity instanceof ManaCapacityEntity)
            capacity = (ManaCapacityEntity) entity;
        matrixStackIn.pushPose();
        float scale = capacity.manaCapacity().getMana() / capacity.manaCapacity().getMaxMana();
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.6f, capacity.spellContext().element.color(), combinedLight));
        matrixStackIn.popPose();
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE_0)
                , new RenderHelper.RenderContext(0.1f, capacity.spellContext().element.color(), combinedLight));
        matrixStackIn.popPose();
    }
}
