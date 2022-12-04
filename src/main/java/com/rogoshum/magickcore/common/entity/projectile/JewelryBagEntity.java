package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.JewelryBagRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Objects;

public class JewelryBagEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.7f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/ray.png");
    public JewelryBagEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(spellContext().containChild(LibContext.ITEM)) return;
        List<ItemEntity> items = this.world.getEntitiesWithinAABB(EntityType.ITEM, this.getBoundingBox().grow(0.5), Objects::nonNull);
        if(items.size() > 0) {
            ItemEntity itemEntity = items.get(0);
            if(itemEntity.isAlive()) {
                ItemContext context = ItemContext.create(itemEntity.getItem());
                itemEntity.remove();
                spellContext().addChild(context);
            }
        }
    }

    @Override
    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.world.isRemote) return;
        Entity entity = getOwner();
        if(entity != null && entity.isAlive()) {
            Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
            Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

            double length = maxMotion * 0.3;
            Vector3d motion = goal.subtract(self).normalize().scale(Math.max(length * 0.2, 0.02));
            this.setMotion(motion.add(this.getMotion().scale(0.8)));
        }
    }

    @Override
    public void remove() {
        if(!world.isRemote && spellContext().containChild(LibContext.ITEM)) {
            ItemContext context = this.spellContext().getChild(LibContext.ITEM);
            if(context.valid()) {
                ItemEntity entity = new ItemEntity(world, getPosX(), getPosY() + getHeight() * 0.5, getPosZ(), context.itemStack);
                world.addEntity(entity);
            }
        }
        super.remove();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new JewelryBagRenderer(this));
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosY() + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.25 + this.getPosZ())
                , (MagickCore.getRandFloat() * this.getWidth()) * 0.3f, (MagickCore.getRandFloat() * this.getWidth()) * 0.3f, 1.0f
                , spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setShakeLimit(15.0f);
        litPar.setLimitScale();
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(this.lastTickPosX + (this.getPosX() - this.lastTickPosX) * partialTicks
                , this.lastTickPosY + (this.getPosY() - this.lastTickPosY) * partialTicks + this.getHeight() * 0.5
                , this.lastTickPosZ + (this.getPosZ() - this.lastTickPosZ) * partialTicks)
                , 0.2f * this.getWidth(), 0.2f * this.getWidth(), 1.0f, 5, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }
}
