package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.tool.SpiritCrystalStaffItem;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.ForgeModelBakery;

import javax.annotation.Nullable;

public class StaffRenderer extends BlockEntityWithoutLevelRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    private static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");
    protected static final ResourceLocation QUARTZ = new ResourceLocation( "minecraft:textures/block/quartz_block_top.png");
    private static final RenderType RENDER_TYPE_0 = RenderType.entityTranslucent(new ResourceLocation( MagickCore.MOD_ID + ":textures/items/staff_side.png"));
    private static final RenderType RENDER_TYPE_1 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));

    public StaffRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        if(p_239207_2_ == ItemTransforms.TransformType.GUI) {
            matrixStack.pushPose();
            RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
            BakedModel bakedmodel = stack.getItem() instanceof SpiritCrystalStaffItem ?
                    Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(RenderHelper.getItemModelResource().get("staff_crystal")) :
                    Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(RenderHelper.getItemModelResource().get("staff"));
            bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, bakedmodel, p_239207_2_, false);
            VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(bufferIn, renderType, true, stack.hasFoil());
            Minecraft.getInstance().getItemRenderer().renderModelLists(bakedmodel, stack, combinedLight, combinedOverlay, matrixStack, vertexconsumer);
            matrixStack.translate(0.525, 0.525, 0);
            matrixStack.translate(0.255, 0.255, 0);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees((MagickCore.proxy.getRunTick()%180)*2));
            matrixStack.translate(-0.525, -0.525, 0);
            //matrixStack.translate(0.185, 0.185, 0);
            //matrixStack.translate(0.185, 0.185, 0);
            BakedModel bakedmodel1 = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(RenderHelper.getItemModelResource().get("mana_energy"));
            bakedmodel1 = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStack, bakedmodel1, p_239207_2_, false);
            bakedmodel1 = bakedmodel1.getOverrides().resolve(bakedmodel1, stack, null, null, 0);
            Minecraft.getInstance().getItemRenderer().renderModelLists(bakedmodel1, stack, combinedLight, combinedOverlay, matrixStack, vertexconsumer);
            matrixStack.popPose();
            Lighting.setupForFlatItems();
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.55, 0.55, 0.5);
        matrixStack.scale(0.65f, 0.65f, 0.65f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(135));

        VertexConsumer buffer = bufferIn.getBuffer(RENDER_TYPE_1);
        RenderHelper.queueMode = true;
        if(buffer instanceof BufferBuilder builder) {
            matrixStack.pushPose();
            matrixStack.translate(0, 0.5, 0);
            matrixStack.scale(0.1f, -1.2f, 0.1f);
            RenderHelper.renderCubeCache(BufferContext.create(matrixStack, builder, RENDER_TYPE_1)
                    , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
            matrixStack.popPose();

            matrixStack.pushPose();
            matrixStack.translate(0, -0.1, 0);
            matrixStack.scale(0.2f, -0.1f, 0.2f);
            RenderHelper.renderCubeCache(BufferContext.create(matrixStack, builder, RENDER_TYPE_1)
                    , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
            matrixStack.popPose();
        }
        RenderHelper.queueMode = false;
        matrixStack.translate(0, -0.3, 0);

        float c = MagickCore.proxy.getRunTick() % 100;
        float angle = 360f * (c / 99);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));

        ItemManaData data = p_239207_2_ == ItemTransforms.TransformType.GUI || p_239207_2_ == ItemTransforms.TransformType.GROUND ?
                ExtraDataUtil.itemManaData(stack, 0):ExtraDataUtil.itemManaData(stack, 2);
        if(data.contextCore().haveMagickContext()) {
            matrixStack.pushPose();
            matrixStack.scale(0.4f, 0.4f, 0.4f);
            if(p_239207_2_ == ItemTransforms.TransformType.GUI || p_239207_2_ == ItemTransforms.TransformType.GROUND) {
                ManaEnergyRenderer.renderSimpleEnergy(data.spellContext(), matrixStack, bufferIn, combinedLight);
            } else {
                ItemStack core = new ItemStack(ModItems.MAGICK_CORE.get());
                ExtraDataUtil.itemManaData(core).spellContext().copy(data.spellContext());
                BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(core, null, null, 0);
                Minecraft.getInstance().getItemRenderer().render(core, ItemTransforms.TransformType.GUI, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            }
            matrixStack.popPose();
        }

        if(stack.getItem() instanceof SpiritCrystalStaffItem) {
            buffer = bufferIn.getBuffer(RENDER_TYPE_0);
            if(buffer instanceof BufferBuilder builder) {
                RenderHelper.queueMode = true;
                matrixStack.scale(0.35f, -0.35f, 0.35f);
                //matrixStack.mulPose(Vector3f.YN.rotationDegrees(angle));
                //matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
                //matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
                RenderHelper.renderCubeCache(BufferContext.create(matrixStack, builder, RENDER_TYPE_1)
                        , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
                RenderHelper.queueMode = false;
            }
        }

        matrixStack.popPose();
    }
}
