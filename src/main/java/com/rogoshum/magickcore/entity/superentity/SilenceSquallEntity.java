package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.SilenceSqualRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaEntity;
import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.magick.context.MagickContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SilenceSquallEntity extends ManaEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/silence_squall.png");
    public SilenceSquallEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(new SilenceSqualRenderer(this));
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(ModSounds.squal_spawn.get(), 1.0F, 1.0F + this.rand.nextFloat() / 3);
        }

        if(this.rand.nextBoolean())
        {
            this.playSound(SoundEvents.UI_TOAST_OUT, 1.0F, 1.0F + this.rand.nextFloat());
        }
        if(this.ticksExisted % 12 == 0)
        {
            this.playSound(ModSounds.squal_ambience.get(), 1.0F, 1.3F - this.rand.nextFloat() / 3);
        }
    }

    protected void applyParticle() {
        for(int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * 18 + this.getPosX()
                    , MagickCore.getNegativeToOne() * 18 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * 18 + this.getPosZ())
                    , MagickCore.getNegativeToOne() / 2, MagickCore.getNegativeToOne() / 2, 0.6f, 50, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setTraceTarget(this);
            par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
            MagickCore.addMagickParticle(par);
        }

        for(int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getTrailTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * 18 + this.getPosX()
                    , MagickCore.getNegativeToOne() * 18 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * 18 + this.getPosZ())
                    , MagickCore.getNegativeToOne() / 4, MagickCore.getNegativeToOne() / 8, 1.0f, 100, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setTraceTarget(this);
            par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
            MagickCore.addMagickParticle(par);
        }

        for(int i = 0; i < 1; ++i) {
            LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * 8 + this.getPosX()
                    , MagickCore.getNegativeToOne() * 6 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * 8 + this.getPosZ())
                    , this.rand.nextFloat() * this.getWidth() * 1.5f, this.rand.nextFloat() * this.getWidth() * 1.5f, 0.3f, this.spellContext().element.getRenderer().getParticleRenderTick(), this.spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(15.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15);
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }

    @Override
    public void releaseMagick() {
        Entity cloest = null;

        List<Entity> entities = findEntity();
        for (Entity entity : entities) {
            if(entity == null)
                return;
            if(!MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity)) {
                if(cloest == null || this.getDistance(entity) < this.getDistance(cloest))
                    cloest = entity;
                if(this.getDistance(entity) <= 9.5) {
                    MagickContext context = new MagickContext(this.world).noCost().caster(this.getOwner()).projectile(this).victim(entity).tick(200).force(4f).applyType(ApplyType.HIT_ENTITY);
                    MagickReleaseHelper.releaseMagick(context);
                }
                if(this.getDistance(entity) <= 3) {
                    MagickContext context = new MagickContext(this.world).noCost().caster(this.getOwner()).projectile(this).victim(entity).tick(200).force(1f).applyType(ApplyType.DE_BUFF);
                    MagickReleaseHelper.releaseMagick(context);
                    if(this.ticksExisted % 20 == 0) {
                        context = new MagickContext(this.world).noCost().caster(this.getOwner()).projectile(this).victim(entity).tick(20).force(1f).applyType(ApplyType.ATTACK);
                        MagickReleaseHelper.releaseMagick(context);
                        entity.hurtResistantTime = 0;
                    }
                }
            }
            //}
        }

        if(cloest != null && cloest.isAlive()) {
            Vector3d vec = cloest.getPositionVec().add(0, 2, 0).subtract(this.getPositionVec());
            this.setMotion(vec.normalize().scale(0.1));
        }
        else if(this.getOwner() != null)
        {
            Vector3d vec = this.getOwner().getPositionVec().add(0, 2, 0).subtract(this.getPositionVec());
            this.setMotion(vec.normalize().scale(0.1));
        }
        this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();
        this.setPosition(this.getPosX() + this.getMotion().x, this.getPosY() + this.getMotion().y, this.getPosZ() + this.getMotion().z);
        this.setMotion(this.getMotion().scale(0.9));
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(16), predicate);
    }
}
