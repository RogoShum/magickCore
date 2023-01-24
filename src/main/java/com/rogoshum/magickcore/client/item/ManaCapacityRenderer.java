package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ManaCapacityRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation taken = new ResourceLocation("magickcore:textures/entity/takensphere.png");
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedEntityGlint(RenderHelper.blankTex, 1f, 0f);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedEntityGlint(taken, 1f, 0f);
    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        Entity entity = NBTTagHelper.createEntityByItem(stack, Minecraft.getInstance().level);
        ManaCapacityEntity capacity = ModEntities.MANA_CAPACITY.get().create(Minecraft.getInstance().level);
        if(entity instanceof ManaCapacityEntity)
            capacity = (ManaCapacityEntity) entity;
        matrixStackIn.pushPose();
        float scale = capacity.manaCapacity().getMana() / capacity.manaCapacity().getMaxMana();
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.6f, capacity.spellContext().element.color(), combinedLight));
        matrixStackIn.popPose();
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), RENDER_TYPE_0)
                , new RenderHelper.RenderContext(0.1f, capacity.spellContext().element.color(), combinedLight));
        matrixStackIn.popPose();
    }
}
