package com.rogoshum.magickcore.mixin.fabric;

import com.rogoshum.magickcore.client.entity.render.*;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRender {

    @Shadow protected abstract <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> entityRenderer);

    @Inject(method = "registerRenderers", at = @At("RETURN"))
    private void registerRenderers(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, CallbackInfo ci) {
        registerEntityRenderingHandler(ModEntities.MANA_ORB.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.MANA_SHIELD.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.MANA_STAR.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.MANA_LASER.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.MANA_SPHERE.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.RADIANCE_WALL.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.CHAOS_REACH.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.THORNS_CARESS.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.SILENCE_SQUALL.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.ASCENDANT_REALM.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.ELEMENT_ORB.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.CONTEXT_CREATOR.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.MANA_CAPACITY.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.CONTEXT_POINTER.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.RAY_TRACE.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.ENTITY_CAPTURE.get(), EntityHunterRenderer::new);
        registerEntityRenderingHandler(ModEntities.CONE.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.SECTOR.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.SPHERE.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.SQUARE.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.RAY.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.BLOOD_BUBBLE.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.LAMP.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.ARROW.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.BUBBLE.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.LEAF.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.RED_STONE.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.SHADOW.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.WIND.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.JEWELRY_BAG.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.REPEATER.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.GRAVITY_LIFT.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.PLACEABLE_ENTITY.get(), PlaceableItemEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.MAGE.get(), MageRenderer::new);
        registerEntityRenderingHandler(ModEntities.PHANTOM.get(), ManaObjectRenderer::new);
        registerEntityRenderingHandler(ModEntities.ARTIFICIAL_LIFE.get(), ArtificialLifeEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.CHAIN.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.SPIN.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.CHARGE.get(), ManaEntityRenderer::new);
        registerEntityRenderingHandler(ModEntities.MULTI_RELEASE.get(), ManaEntityRenderer::new);
    }

    public <E extends Entity> void registerEntityRenderingHandler(EntityType<? extends E> entityType, Function<EntityRenderDispatcher, EntityRenderer<E>> entityRendererFactory) {
        this.register(entityType, entityRendererFactory.apply((EntityRenderDispatcher)(Object)this));
    }
}
