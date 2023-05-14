package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.PotionContext;
import com.rogoshum.magickcore.api.magick.context.child.PsiSpellContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.HashSet;

public class ManaEnergyRenderer extends BlockEntityWithoutLevelRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation TAKEN = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/taken_layer.png");
    protected static final ResourceLocation PSI = new ResourceLocation(MagickCore.MOD_ID + ":textures/items/spell_bullet.png");
    private static final HashMap<ApplyType, ResourceLocation> APPLY_TYPE_TEXTURES = new HashMap<>();
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexturedShaderItemTranslucent(blank);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexturedItemGlint(TAKEN, 0.25f, 0);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_0 = new RenderHelper.RenderContext(0.15f, Color.GREY_COLOR, RenderHelper.halfLight);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_2 = new RenderHelper.RenderContext(0.15f, Color.ORIGIN_COLOR, RenderHelper.halfLight);
    private static final HashSet<EntityType<?>> ERROR_TYPE = new HashSet<>();

    public ManaEnergyRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static void addApplyTypeTexture(ApplyType type, ResourceLocation res) {
        APPLY_TYPE_TEXTURES.put(type, res);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        matrixStack.translate(0.5, 0.5, 0.5);
        SpellContext spellContext = SpellContext.create(stack.getOrCreateTag().getCompound(LibRegistry.ITEM_DATA), 2);

        matrixStack.scale(1.1f, 1.1f, 1.1f);
        renderSingleEnergy(matrixStack, bufferIn, buffer, spellContext, combinedLight, stack);
        SpellContext post = spellContext.postContext;
        float tick = 100;
        float scale = 0.4f;

        while (post != null) {
            float angle = MagickCore.proxy.getRunTick() % tick;
            tick-=1;
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(360f * (angle / tick)));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(360f * (angle / tick)));
            matrixStack.mulPose(Vector3f.ZN.rotationDegrees(360f * (angle / tick)));
            tick += 20;
            matrixStack.translate(0.32, 0.32, 0.32);
            matrixStack.scale(scale, scale, scale);
            scale *= 0.7f;
            renderSingleEnergy(matrixStack, bufferIn, buffer, post, combinedLight, stack);
            matrixStack.popPose();
            post = post.postContext;
        }

        if(stack.getItem() instanceof ContextCoreItem) {
            RenderType RENDER_TYPE = RenderHelper.getTexturedShaderItemTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));
            for (int i = 0; i < RenderHelper.vertex_list.length; ++i) {
                float[] vertex = RenderHelper.vertex_list[i];
                matrixStack.pushPose();
                matrixStack.translate(vertex[0] * 0.6, vertex[1] * 0.6, vertex[2] * 0.6);
                matrixStack.scale(0.25f, 0.25f, 0.25f);
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(270));
                RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE)
                        , new RenderHelper.RenderContext(0.3f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
                matrixStack.popPose();
            }
        }

        matrixStack.popPose();
    }

    public void renderSingleEnergy(PoseStack matrixStack, MultiBufferSource bufferIn, BufferBuilder buffer, SpellContext spellContext, int combinedLight, ItemStack stack) {
        //tick
        int tick = spellContext.tick;
        int orbStack = tick / 20;
        float last = (tick % 20) / 20f;

        Entity entity = NBTTagHelper.createEntityByItem(stack, Minecraft.getInstance().level);
        if(entity instanceof ContextCreatorEntity)
            renderMaterial(((ContextCreatorEntity) entity).getInnerManaData(), matrixStack, bufferIn, combinedLight);
        else if(entity instanceof IMaterialLimit)
            renderMaterial((IMaterialLimit) entity, matrixStack, bufferIn, combinedLight);
        else if(stack.hasTag() && stack.getTag().contains("mana_material")) {
            Material material = ManaMaterials.getMaterial(stack.getTag().getString("mana_material"));
            renderMaterial(material, matrixStack, bufferIn, combinedLight);
        }

        if(spellContext.containChild(LibContext.SPAWN)) {
            SpawnContext spawnContext = spellContext.getChild(LibContext.SPAWN);
            if(spawnContext.entityType != null) {
                renderEntity(spawnContext.entityType, matrixStack, bufferIn, combinedLight);
            }
        }

        if(spellContext.containChild(PsiSpellContext.TYPE)) {
            matrixStack.pushPose();
            BufferContext bufferContext = BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RenderHelper.getTexturedShaderItemTranslucent(PSI));
            float alpha = 1.0f;
            float scale = 0.4f;
            matrixStack.scale(scale, scale, scale);
            matrixStack.mulPose(Vector3f.YN.rotationDegrees(45));
            matrixStack.mulPose(Vector3f.XN.rotationDegrees(30));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
            RenderHelper.renderStaticParticle(bufferContext, new RenderHelper.RenderContext(alpha, Color.GREY_COLOR, RenderHelper.renderLight));
            matrixStack.popPose();
        }

        if(bufferIn instanceof MultiBufferSource.BufferSource)
            ((MultiBufferSource.BufferSource) bufferIn).endBatch();

        matrixStack.pushPose();
        float angle = MagickCore.proxy.getRunTick() % 60;
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(360f * (angle / 59)));
        if(tick > 0)
            renderEnergy(matrixStack, combinedLight, 0, orbStack, last);

        //force
        float force = spellContext.force;
        orbStack = (int) (force);
        last = force % 1f;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(360f * (angle / 59)));
        if(force > 0)
            renderEnergy(matrixStack, combinedLight, 1, orbStack, last);

        //range
        float range = spellContext.range;
        orbStack = (int) (range);
        last = range % 1f;
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(360f * (angle / 59)));
        if(range > 0)
            renderEnergy(matrixStack, combinedLight, 2, orbStack, last);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.scale(0.6f, 0.6f, 0.6f);
        if(spellContext.containChild(LibContext.POTION)) {
            PotionContext potionContext = spellContext.getChild(LibContext.POTION);
            int color = PotionUtils.getColor(potionContext.effectInstances);
            RenderHelper.renderCubeCache(BufferContext.create(matrixStack, buffer, RENDER_TYPE_1)
                    , new RenderHelper.RenderContext(0.3f, Color.create(color), combinedLight));
        } else if(spellContext.element.primaryColor().equals(Color.ORIGIN_COLOR)) {
            RenderHelper.renderCubeCache(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0)
                    , new RenderHelper.RenderContext(0.12f, spellContext.element.primaryColor(), combinedLight));
        } else {
            RenderHelper.renderCubeCache(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0)
                    , new RenderHelper.RenderContext(0.3f, spellContext.element.primaryColor(), combinedLight));
        }
        matrixStack.scale(1.1f, 1.1f, 1.1f);
        if(stack.getItem() instanceof MagickContextItem) {
            if(!APPLY_TYPE_TEXTURES.containsKey(spellContext.applyType))
                RenderHelper.renderCubeCache(BufferContext.create(matrixStack, buffer, RENDER_TYPE_1)
                        , RENDER_CONTEXT_0);
            else {
                ResourceLocation res = APPLY_TYPE_TEXTURES.get(spellContext.applyType);
                RenderHelper.renderCubeCache(BufferContext.create(matrixStack, buffer, RenderHelper.getTexturedItemGlint( res, 0.5f,0f))
                        , RENDER_CONTEXT_2);
            }
        } else
            RenderHelper.renderCubeCache(BufferContext.create(matrixStack, buffer, RENDER_TYPE_0)
                    , RENDER_CONTEXT_0);
        matrixStack.popPose();
    }

    public void renderEnergy(PoseStack matrixStack, int combinedLight, int energyType, int stack, float last) {
        float scale = 0.2f;
        float alpha = 0.2f;

        matrixStack.pushPose();
        if(energyType == 0)
            matrixStack.translate(0, 0.15, 0);
        else if(energyType == 1)
            matrixStack.translate(0.15, -0.15, 0);
        else
            matrixStack.translate(-0.15, -0.15, 0);


        Color color = null;
        if(energyType == 0)
            color = Color.KHAKI_COLOR;
        else if(energyType == 1)
            color = Color.CYAN_COLOR;
        else if(energyType == 2)
            color = Color.SALMON_COLOR;
        matrixStack.scale(scale, scale, scale);
        matrixStack.pushPose();
        stack = Math.min(stack, 10);
        alpha = 1;
        for (int i = 0; i <= stack; i++) {
            //renderOrb(matrixStack, combinedLight, color, 1, alpha);
            alpha += 0.1f;
            scale += 0.15;
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
        matrixStack.popPose();
        matrixStack.popPose();
    }

    public void renderOrb(PoseStack matrixStack, int combinedLight, Color color, float scale, float alpha) {
        matrixStack.pushPose();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RenderHelper.getTexturedQuadsEnergy(RenderHelper.BLANK_TEX))
                , new RenderHelper.RenderContext(alpha, color, combinedLight));
        matrixStack.popPose();
        scale = 1.5f;
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(Vector3f.YP.rotation(45));
        matrixStack.mulPose(Vector3f.ZP.rotation(45));
    }

    public static void renderEntity(EntityType<?> entityType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight) {
        Entity entity = entityType.create(Minecraft.getInstance().level);
        if(entity == null) return;

        matrixStack.pushPose();
        if(entity instanceof IManaEntity) {
            IManaEntity manaEntity = (IManaEntity) entity;
            ResourceLocation icon = manaEntity.getEntityIcon();
            if(icon == null)
                icon = IManaEntity.orbTex;

            BufferContext bufferContext = BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RenderHelper.getTexturedShaderItemTranslucent(icon));
            float alpha = 1.0f;
            float scale = 0.4f;
            matrixStack.scale(scale, scale, scale);
            matrixStack.mulPose(Vector3f.YN.rotationDegrees(45));
            matrixStack.mulPose(Vector3f.XN.rotationDegrees(30));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
            RenderHelper.renderStaticParticle(bufferContext, new RenderHelper.RenderContext(alpha, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        } else if(!ERROR_TYPE.contains(entity.getType())) {
            float scale = 0.4f;
            double xSize = entity.getBoundingBoxForCulling().getXsize();
            double ySize = entity.getBoundingBoxForCulling().getYsize();
            double zSize = entity.getBoundingBoxForCulling().getZsize();
            double longest = Math.max(xSize, Math.max(zSize, ySize));

            scale = (float) (scale / longest);
            //MagickCore.LOGGER.info(scale + " " + (scale * xSize) + " " + ySize + " " + (scale * ySize));
            matrixStack.scale(scale, scale, scale);

            matrixStack.translate(0, -ySize / 2, 0);
            try {
                MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0, 0, 0, 0
                        , 0, matrixStack
                        , bufferIn, combinedLight);
                renderTypeBuffer.endBatch();
            } catch (Exception e) {
                ERROR_TYPE.add(entity.getType());
            }
        }
        matrixStack.popPose();
    }

    public void renderMaterial(IMaterialLimit material, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight) {
        renderMaterial(material.getMaterial(), matrixStack, bufferIn, combinedLight);
    }

    public void renderMaterial(Material material, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight) {
        matrixStack.pushPose();
        ItemStack stack = new ItemStack(material.getItem());
        float f3 = ((float) MagickCore.proxy.getRunTick() + Minecraft.getInstance().getFrameTime()) / 100.0F;
        matrixStack.translate(0, -0.1, 0);
        matrixStack.mulPose(Vector3f.YP.rotation(f3));
        BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GROUND, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        matrixStack.popPose();
    }
}
