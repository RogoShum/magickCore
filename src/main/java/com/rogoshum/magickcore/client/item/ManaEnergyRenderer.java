package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
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
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

public class ManaEnergyRenderer extends ItemStackTileEntityRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation TAKEN = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/taken_layer.png");
    private static final HashMap<ApplyType, ResourceLocation> APPLY_TYPE_TEXTURES = new HashMap<>();
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedSphere(blank);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedSphereGlow(TAKEN, 1, 0);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_0 = new RenderHelper.RenderContext(0.15f, ModElements.ORIGIN_COLOR, RenderHelper.halfLight);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_1 = new RenderHelper.RenderContext(0.15f, Color.ORIGIN_COLOR, RenderHelper.halfLight);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_2 = new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.halfLight);
    static {
        RENDER_CONTEXT_0.alpha = 0.15f;
        RENDER_CONTEXT_1.alpha = 0.15f;
        RENDER_CONTEXT_2.alpha = 0.15f;
    }
    private static final Queue<Queue<RenderHelper.VertexAttribute>> INNER_SPHERE_1 = RenderHelper.drawSphere(6, RENDER_CONTEXT_1 , RenderHelper.EmptyVertexContext);
    private static final Queue<Queue<RenderHelper.VertexAttribute>> INNER_SPHERE_2 = RenderHelper.drawSphere(6, RENDER_CONTEXT_2 , RenderHelper.EmptyVertexContext);
    private static final Queue<Queue<RenderHelper.VertexAttribute>> INNER_SPHERE_0 = RenderHelper.drawSphere(6, RENDER_CONTEXT_0 , RenderHelper.EmptyVertexContext);
    private static final HashSet<EntityType<?>> ERROR_TYPE = new HashSet<>();

    public static void addApplyTypeTexture(ApplyType type, ResourceLocation res) {
        APPLY_TYPE_TEXTURES.put(type, res);
    }

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        matrixStack.translate(0.5, 0.5, 0.5);
        ItemManaData itemManaData = ExtraDataUtil.itemManaData(stack);
        SpellContext spellContext = itemManaData.spellContext();
        if(stack.getItem() instanceof ContextCoreItem) {
            RenderType RENDER_TYPE = RenderHelper.getTexedEntity(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));
            for (int i = 0; i < RenderHelper.vertex_list.length; ++i) {
                float[] vertex = RenderHelper.vertex_list[i];
                matrixStack.push();
                matrixStack.translate(vertex[0] * 0.6, vertex[1] * 0.6, vertex[2] * 0.6);
                matrixStack.scale(0.25f, 0.25f, 0.25f);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(270));
                RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RENDER_TYPE)
                        , new RenderHelper.RenderContext(0.3f, Color.ORIGIN_COLOR, combinedLight));
                matrixStack.pop();
            }
        }
        matrixStack.scale(1.1f, 1.1f, 1.1f);
        renderSingleEnergy(matrixStack, bufferIn, buffer, spellContext, combinedLight, stack);
        SpellContext post = spellContext.postContext;
        float tick = 100;
        float scale = 0.4f;
        while (post != null) {
            float angle = MagickCore.proxy.getRunTick() % tick;
            tick-=1;
            matrixStack.push();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(360f * (angle / tick)));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(360f * (angle / tick)));
            matrixStack.rotate(Vector3f.ZN.rotationDegrees(360f * (angle / tick)));
            tick += 20;
            matrixStack.translate(0.32, 0.32, 0.32);
            matrixStack.scale(scale, scale, scale);
            scale *= 0.7f;
            renderSingleEnergy(matrixStack, bufferIn, buffer, post, combinedLight, stack);
            matrixStack.pop();
            post = post.postContext;
        }
        matrixStack.pop();
    }

    public void renderSingleEnergy(MatrixStack matrixStack, IRenderTypeBuffer bufferIn, BufferBuilder buffer, SpellContext spellContext, int combinedLight, ItemStack stack) {
        //tick
        int tick = spellContext.tick;
        int orbStack = tick / 20;
        float last = (tick % 20) / 20f;

        matrixStack.push();
        float angle = MagickCore.proxy.getRunTick() % 60;
        matrixStack.rotate(Vector3f.XP.rotationDegrees(360f * (angle / 59)));
        if(tick > 0)
            renderEnergy(matrixStack, combinedLight, 0, orbStack, last);

        //force
        float force = spellContext.force;
        orbStack = (int) (force);
        last = force % 1f;
        matrixStack.rotate(Vector3f.YP.rotationDegrees(360f * (angle / 59)));
        if(force > 0)
            renderEnergy(matrixStack, combinedLight, 1, orbStack, last);

        //range
        float range = spellContext.range;
        orbStack = (int) (range);
        last = range % 1f;
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(360f * (angle / 59)));
        if(range > 0)
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

        matrixStack.push();
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        if(spellContext.containChild(LibContext.POTION)) {
            PotionContext potionContext = spellContext.getChild(LibContext.POTION);
            int color = PotionUtils.getPotionColorFromEffectList(potionContext.effectInstances);
            RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_1)
                    , RenderHelper.drawSphere(6, new RenderHelper.RenderContext(0.3f, Color.create(color), combinedLight)
                            , RenderHelper.EmptyVertexContext));
        } else if(spellContext.element.color().equals(Color.ORIGIN_COLOR)) {
            RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0)
                    , RenderHelper.drawSphere(6, new RenderHelper.RenderContext(0.1f, spellContext.element.color(), combinedLight)
                            , RenderHelper.EmptyVertexContext));
        } else {
            RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0)
                    , RenderHelper.drawSphere(6, new RenderHelper.RenderContext(0.3f, spellContext.element.color(), combinedLight)
                            , RenderHelper.EmptyVertexContext));
        }

        matrixStack.scale(1.1f, 1.1f, 1.1f);
        if(stack.getItem() instanceof MagickContextItem) {
            if(!APPLY_TYPE_TEXTURES.containsKey(spellContext.applyType))
                RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_1), INNER_SPHERE_1);
            else {
                ResourceLocation res = APPLY_TYPE_TEXTURES.get(spellContext.applyType);
                RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RenderHelper.getTexedSphereGlow(res, 1, 0)), INNER_SPHERE_2);
            }
        }
        else
            RenderHelper.renderSphere(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0), INNER_SPHERE_0);
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
        stack = Math.min(stack, 10);
        alpha = 1;
        for (int i = 0; i <= stack; i++) {
            //renderOrb(matrixStack, combinedLight, color, 1, alpha);
            alpha += 0.1f;
            scale += 0.5;
            /*
            if(i % 5 == 0) {
                matrixStack.scale(0.1316f, 0.1316f, 0.1316f);
                if(alpha < 1)
                    alpha += 0.2f;
            }

             */
        }
        scale += last;
        if(alpha > 1)
            alpha = 1;
        renderOrb(matrixStack, combinedLight, color, scale, alpha);
        /*
        if(last > 0) {
            renderOrb(matrixStack, combinedLight, color, last, alpha);
        }
         */
        matrixStack.pop();
        matrixStack.pop();
    }

    public void renderOrb(MatrixStack matrixStack, int combinedLight, Color color, float scale, float alpha) {
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(alpha, color, combinedLight));
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

            BufferContext bufferContext = BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedOrbGlow(icon));
            Color color = manaEntity.spellContext().element.color();
            Matrix4f matrix4f = matrixStack.getLast().getMatrix();
            float alpha = 0.5f;
            float scale = 0.4f;
            matrixStack.scale(scale, scale, scale);
            matrixStack.rotate(Vector3f.YN.rotationDegrees(45));
            matrixStack.rotate(Vector3f.XN.rotationDegrees(30));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
            RenderHelper.renderStaticParticle(bufferContext, new RenderHelper.RenderContext(alpha, color, RenderHelper.renderLight));
        } else if(!ERROR_TYPE.contains(entity.getType())){
            float scale = 0.4f;
            double xSize = entity.getRenderBoundingBox().getXSize();
            double ySize = entity.getRenderBoundingBox().getYSize();
            double zSize = entity.getRenderBoundingBox().getZSize();
            double longest = Math.max(xSize, Math.max(zSize, ySize));

            scale = (float) (scale / longest);
            //MagickCore.LOGGER.info(scale + " " + (scale * xSize) + " " + ySize + " " + (scale * ySize));
            matrixStack.scale(scale, scale, scale);

            matrixStack.translate(0, -ySize / 2, 0);
            try {
                Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0, 0, 0, 0
                        , 0, matrixStack
                        , bufferIn, combinedLight);
            } catch (Exception e) {
                ERROR_TYPE.add(entity.getType());
            }
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
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        matrixStack.pop();
    }
}
