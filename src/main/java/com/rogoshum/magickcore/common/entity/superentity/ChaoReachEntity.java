package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.laser.ChaosReachLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.ChaosReachRenderer;
import com.rogoshum.magickcore.common.api.enums.ManaLimit;
import com.rogoshum.magickcore.common.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ChaoReachEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/chaos_reach.png");
    public ChaoReachEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(32), predicate);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ChaosReachRenderer(this));
        MagickCore.proxy.addRenderer(() -> new ChaosReachLaserRenderer(this));
    }

    @Override
    public void tick() {
        super.tick();
        if(this.ticksExisted <= 30)
            return;
        initial = true;
    }

    @Override
    protected void doClientTask() {
        Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.2F, 0.005F));
        super.doClientTask();
    }

    @Override
    public void releaseMagick() {
        if(!initial) return;
        List<Entity> livings = findEntity(entity -> entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity) && MagickReleaseHelper.canEntityTraceAnother(this, entity));
        boolean makeSound = false;
        for (Entity entity : livings) {
            makeSound = true;
            MagickContext context = new MagickContext(world).noCost().caster(this.getOwner()).projectile(this).victim(entity).tick(50).force(ManaLimit.FORCE.getValue()).applyType(ApplyType.DE_BUFF);
            MagickReleaseHelper.releaseMagick(context);
            context = new MagickContext(world).noCost().caster(this.getOwner()).projectile(this).victim(entity).tick(10).force(ManaLimit.FORCE.getValue()).applyType(ApplyType.ATTACK);
            MagickReleaseHelper.releaseMagick(context);
        }

        if(makeSound && this.ticksExisted % 2 == 0)
            this.playSound(ModSounds.chaos_attak.get(), 2.0F, 1.0F - this.rand.nextFloat() / 5);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(ModSounds.chaos_spawn.get(), 2.0F, 1.0F - this.rand.nextFloat());
        }

        if(this.ticksExisted > 20 && this.ticksExisted % 5 == 0)
        {
            this.playSound(ModSounds.chaos_ambience.get(), 1.0F, 1.0F + this.rand.nextFloat());
        }
    }

    protected void applyParticle()
    {
        for(int i = 0; i < 1; ++i) {
            LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                    , 0.15f, 0.15f, this.rand.nextFloat(), 60, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
            MagickCore.addMagickParticle(par);
        }
        for(int i = 0; i < 1; ++i) {
            LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                    , this.rand.nextFloat() * this.getWidth() * this.getWidth(), this.rand.nextFloat() * this.getWidth() * this.getWidth(), 0.8f + 0.2f * this.rand.nextFloat(), this.spellContext().element.getRenderer().getParticleRenderTick() / 2, this.spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(35.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
            MagickCore.addMagickParticle(litPar);
        }

        float scale = Math.max(this.getWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , scale, scale, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }
}
