package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.init.ManaMaterials;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.item.ContextCoreItem;
import com.rogoshum.magickcore.item.MagickContextItem;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.magick.materials.Material;
import com.rogoshum.magickcore.proxy.ClientProxy;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Queue;

public class ManaEnergyRenderer extends ItemStackTileEntityRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation TAKEN = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/taken_layer.png");
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedSphere(blank);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedSphereGlow(blank, 1f, 0f);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_0 = new RenderHelper.RenderContext(0.15f, ModElements.ORIGIN_COLOR, RenderHelper.halfLight);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_1 = new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.halfLight);
    private static final Queue<Queue<RenderHelper.VertexAttribute>> INNER_SPHERE_1 = RenderHelper.drawSphere(12, RENDER_CONTEXT_1 , RenderHelper.EmptyVertexContext);
    private static final Queue<Queue<RenderHelper.VertexAttribute>> INNER_SPHERE_0 = RenderHelper.drawSphere(12, RENDER_CONTEXT_0 , RenderHelper.EmptyVertexContext);

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        matrixStack.translate(0.5, 0.5, 0.5);
        ItemManaData itemManaData = ExtraDataHelper.itemManaData(stack);
        SpellContext spellContext = itemManaData != null ? itemManaData.spellContext() : new SpellContext();
        if(stack.getItem() instanceof ContextCoreItem) {
            RenderType RENDER_TYPE = RenderType.getEntityCutout(TAKEN);
            Color color = Color.create(0.3f, 0.2f, 0f);
            for (int i = 0; i < RenderHelper.vertex_list.length; ++i) {
                float[] vertex = RenderHelper.vertex_list[i];
                matrixStack.push();
                matrixStack.translate(vertex[0] * 0.6, vertex[1] * 0.6, vertex[2] * 0.6);
                matrixStack.scale(0.25f, 0.25f, 0.25f);
                RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RENDER_TYPE)
                        , new RenderHelper.RenderContext(1.0f, color, combinedLight));
                matrixStack.pop();
            }
        }
        matrixStack.scale(1.1f, 1.1f, 1.1f);
        matrixStack.push();
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0)
                , RenderHelper.drawSphere(12, new RenderHelper.RenderContext(0.1f, spellContext.element.color(), RenderHelper.halfLight)
                        , RenderHelper.EmptyVertexContext));
        matrixStack.scale(1.1f, 1.1f, 1.1f);
        if(stack.getItem() instanceof MagickContextItem)
            RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RenderHelper.getTexedSphere(TAKEN)), INNER_SPHERE_1);
        else
            RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0), INNER_SPHERE_0);
        matrixStack.pop();

        //tick
        int tick = spellContext.tick;
        int orbStack = tick / 20;
        float last = (tick % 20) / 20f;

        matrixStack.push();
        float angle = MagickCore.proxy.getRunTick() % 60;
        matrixStack.rotate(Vector3f.XP.rotationDegrees(360f * (angle / 59)));
        renderEnergy(matrixStack, combinedLight, 0, orbStack, last);

        //force
        float force = spellContext.force;
        orbStack = (int) (force);
        last = force % 1f;
        matrixStack.rotate(Vector3f.YP.rotationDegrees(360f * (angle / 59)));
        renderEnergy(matrixStack, combinedLight, 1, orbStack, last);

        //range
        float range = spellContext.range;
        orbStack = (int) (range);
        last = range % 1f;
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(360f * (angle / 59)));
        renderEnergy(matrixStack, combinedLight, 2, orbStack, last);
        matrixStack.pop();

        if(spellContext.containChild(LibContext.SPAWN)) {
            SpawnContext spawnContext = spellContext.getChild(LibContext.SPAWN);
            if(spawnContext.entityType != null) {
               renderEntity(spawnContext.entityType, matrixStack, bufferIn, combinedLight);
            }
        }

        Entity entity = NBTTagHelper.createEntityByItem(stack, Minecraft.getInstance().world);
        if(entity instanceof ContextCreatorEntity)
            renderMaterial(((ContextCreatorEntity) entity).getInnerManaData(), matrixStack, bufferIn, combinedLight);
        else if(entity instanceof IMaterialLimit)
            renderMaterial((IMaterialLimit) entity, matrixStack, bufferIn, combinedLight);
        else if(stack.hasTag() && stack.getTag().contains("mana_material")) {
            Material material = ManaMaterials.getMaterial(stack.getTag().getString("mana_material"));
            renderMaterial(material, matrixStack, bufferIn, combinedLight);
        }

        matrixStack.pop();
    }

    public void renderEnergy(MatrixStack matrixStack, int combinedLight, int energyType, int stack, float last) {
        float scale = 0.1f;
        float alpha = 0.2f;

        matrixStack.push();
        if(energyType == 0)
            matrixStack.translate(0, 0.15, 0);
        else if(energyType == 1)
            matrixStack.translate(0.15, -0.15, 0);
        else
            matrixStack.translate(-0.15, -0.15, 0);


        Color color = null;
        if(energyType == 0)
            color = Color.YELLOW_COLOR;
        else if(energyType == 1)
            color = Color.BLUE_COLOR;
        else if(energyType == 2)
            color = Color.RED_COLOR;
        matrixStack.scale(scale, scale, scale);
        matrixStack.push();
        stack = Math.min(stack, 20);
        for (int i = 1; i <= stack; i++) {
            renderOrb(matrixStack, combinedLight, color, 1, alpha);
            if(i % 5 == 0) {
                matrixStack.scale(0.1316f, 0.1316f, 0.1316f);
                if(alpha < 1)
                    alpha += 0.2f;
            }
        }
        if(last > 0) {
            renderOrb(matrixStack, combinedLight, color, last, alpha);
        }

        matrixStack.pop();
        matrixStack.pop();
    }

    public void renderOrb(MatrixStack matrixStack, int combinedLight, Color color, float scale, float alpha) {
        matrixStack.push();
        matrixStack.scale(scale, scale, scale);
        RenderHelper.renderSphere(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer()
                , RENDER_TYPE_1)
                , RenderHelper.drawSphere(4, new RenderHelper.RenderContext(alpha, color, combinedLight)
                        , RenderHelper.EmptyVertexContext));
        matrixStack.pop();
        scale = 1.5f;
        matrixStack.scale(scale, scale, scale);
        matrixStack.rotate(Vector3f.YP.rotation(45));
        matrixStack.rotate(Vector3f.ZP.rotation(45));
    }

    public void renderEntity(EntityType<?> entityType, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight) {
        Entity entity = entityType.create(Minecraft.getInstance().world);
        if(entity == null) return;

        matrixStack.push();
        if(entity instanceof IManaEntity) {
            IManaEntity manaEntity = (IManaEntity) entity;
            ResourceLocation icon = manaEntity.getEntityIcon();
            if(icon == null)
                icon = IManaEntity.orbTex;
            IVertexBuilder builder = bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(icon));
            Color color = manaEntity.spellContext().element.color();
            Matrix4f matrix4f = matrixStack.getLast().getMatrix();
            float alpha = 0.35f;
            float scale = 0.4f;
            matrixStack.scale(scale, scale, scale);
            matrixStack.rotate(Vector3f.YN.rotationDegrees(45));
            matrixStack.rotate(Vector3f.XN.rotationDegrees(30));

            builder.pos(matrix4f, -1.0f, -1.0f, 0.0f).color(color.r(), color.g(), color.b(), alpha).tex(1.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, -1.0f, 0.0f).endVertex();
            builder.pos(matrix4f, -1.0f, 1.0f, 0.0f).color(color.r(), color.g(), color.b(), alpha).tex(1.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 1.0f, 0.0f).endVertex();
            builder.pos(matrix4f, 1.0f, 1.0f, 0.0f).color(color.r(), color.g(), color.b(), alpha).tex(0.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 1.0f, 0.0f).endVertex();
            builder.pos(matrix4f, 1.0f, -1.0f, 0.0f).color(color.r(), color.g(), color.b(), alpha).tex(0.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, -1.0f, 0.0f).endVertex();
        } else {
            float scale = 0.4f;
            double xSize = entity.getRenderBoundingBox().getXSize();
            double ySize = entity.getRenderBoundingBox().getYSize();
            double zSize = entity.getRenderBoundingBox().getZSize();
            double longest = Math.max(xSize, Math.max(zSize, ySize));

            scale = (float) (scale / longest);
            //MagickCore.LOGGER.info(scale + " " + (scale * xSize) + " " + ySize + " " + (scale * ySize));
            matrixStack.scale(scale, scale, scale);

            matrixStack.translate(0, -ySize / 2, 0);
            Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0, 0, 0, 0
                    , 0, matrixStack
                    , bufferIn, combinedLight);
        }
        matrixStack.pop();
    }

    public void renderMaterial(IMaterialLimit material, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight) {
        renderMaterial(material.getMaterial(), matrixStack, bufferIn, combinedLight);
    }

    public void renderMaterial(Material material, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight) {
        matrixStack.push();
        ItemStack stack = new ItemStack(material.getItem());
        float f3 = ((float) MagickCore.proxy.getRunTick() + Minecraft.getInstance().getRenderPartialTicks()) / 100.0F;
        matrixStack.translate(0, -0.1, 0);
        matrixStack.rotate(Vector3f.YP.rotation(f3));
        IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, renderTypeBuffer, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        renderTypeBuffer.finish();
        matrixStack.pop();
    }
}
