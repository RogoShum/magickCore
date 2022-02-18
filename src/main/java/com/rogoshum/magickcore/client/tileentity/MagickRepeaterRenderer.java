package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.CanSeeTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.event.RenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class MagickRepeaterRenderer extends CanSeeTileEntityRenderer<MagickRepeaterTileEntity> {
    private final ModelRenderer bone;
    private final ModelRenderer cube_r1;
    private final ModelRenderer bone2;
    private final ModelRenderer cube_r2;
    private final ModelRenderer bone3;
    private final ModelRenderer cube_r3;
    private final ModelRenderer bone4;
    private final ModelRenderer cube_r4;

    private ResourceLocation magick_repeater = new ResourceLocation(MagickCore.MOD_ID +":textures/tileentity/magick_repeater.png");
    private final int textureWidth;
    private final int textureHeight;

    public MagickRepeaterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        textureWidth = 64;
        textureHeight = 32;

        bone = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        bone.setRotationPoint(0.0F, 16.0F, 0.0F);
        bone.setTextureOffset(0, 0).addBox(-6.0F, 6.0F, 6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        bone.setTextureOffset(0, 0).addBox(-8.0F, -8.0F, 6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

        cube_r1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        cube_r1.setRotationPoint(0.0F, 8.0F, 0.0F);
        bone.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.0F, 0.0F, 1.5708F);
        cube_r1.setTextureOffset(0, 0).addBox(-14.0F, 6.0F, 6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        cube_r1.setTextureOffset(0, 0).addBox(-16.0F, -8.0F, 6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

        bone2 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        bone2.setRotationPoint(0.0F, 16.0F, 0.0F);
        bone2.setTextureOffset(0, 0).addBox(-6.0F, 6.0F, -8.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        bone2.setTextureOffset(0, 0).addBox(-8.0F, -8.0F, -8.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

        cube_r2 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        cube_r2.setRotationPoint(0.0F, 8.0F, -14.0F);
        bone2.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, 0.0F, 1.5708F);
        cube_r2.setTextureOffset(0, 0).addBox(-14.0F, 6.0F, 6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        cube_r2.setTextureOffset(0, 0).addBox(-16.0F, -8.0F, 6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);

        bone3 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        bone3.setRotationPoint(0.0F, 16.0F, 0.0F);
        setRotationAngle(bone3, -1.5708F, 0.0F, 0.0F);


        cube_r3 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        cube_r3.setRotationPoint(0.0F, 8.0F, -14.0F);
        bone3.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0F, 0.0F, 1.5708F);
        cube_r3.setTextureOffset(0, 0).addBox(-14.0F, 6.0F, 6.0F, 12.0F, 2.0F, 2.0F, 0.0F, false);
        cube_r3.setTextureOffset(0, 0).addBox(-14.0F, -8.0F, 6.0F, 12.0F, 2.0F, 2.0F, 0.0F, false);

        bone4 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        bone4.setRotationPoint(0.0F, 16.0F, 0.0F);
        setRotationAngle(bone4, -1.5708F, 0.0F, 0.0F);


        cube_r4 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        cube_r4.setRotationPoint(0.0F, 8.0F, 0.0F);
        bone4.addChild(cube_r4);
        setRotationAngle(cube_r4, 0.0F, 0.0F, 1.5708F);
        cube_r4.setTextureOffset(0, 0).addBox(-14.0F, 6.0F, 6.0F, 12.0F, 2.0F, 2.0F, 0.0F, false);
        cube_r4.setTextureOffset(0, 0).addBox(-14.0F, -8.0F, 6.0F, 12.0F, 2.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void render(MagickRepeaterTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        matrixStackIn.push();
        matrixStackIn.translate(0.5f, -0.5f, 0.5f);
        RenderType type = RenderHelper.getTexedOrbSolid(magick_repeater);
        IVertexBuilder buffer = bufferIn.getBuffer(type);
        bone.render(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY);
        bone2.render(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY);
        bone3.render(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY);
        bone4.render(matrixStackIn, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY);
        ((IRenderTypeBuffer.Impl)bufferIn).finish(type);
        if (tileEntityIn.getLifeRepeater() != null && tileEntityIn.getLifeRepeater().dropItem() != null) {
            matrixStackIn.translate(0, 0.875, 0);
            Vector3d vec = Minecraft.getInstance().player.getPositionVec()
                    .add(0, Minecraft.getInstance().player.getEyeHeight(Minecraft.getInstance().player.getPose()), 0)
                    .subtract(Vector3d.copyCentered(tileEntityIn.getPos().up()));
            Vector2f rota = EasyRenderer.getRotationFromVector(vec);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rota.x - 90));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(rota.y + 45));
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tileEntityIn.getLifeRepeater().dropItem(), tileEntityIn.getWorld(), (LivingEntity) null);
            Minecraft.getInstance().getItemRenderer().renderItem(tileEntityIn.getLifeRepeater().dropItem(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel);
        }
        matrixStackIn.pop();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
