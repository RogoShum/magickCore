package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.entity.easyrender.laser.ThornsCaressLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.ThornsCaressRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ThornsCaressEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/thorns_caress.png");
    public ThornsCaressEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ThornsCaressRenderer(this));
        MagickCore.proxy.addRenderer(() -> new ThornsCaressLaserRenderer(this));
    }

    @Override
    public void releaseMagick() {
        if(this.ticksExisted % 2 ==0) {
            List<Entity> livings = this.findEntity((entity -> entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity)));
            for (Entity entity : livings) {
                MagickContext context = new MagickContext(world).noCost().caster(this.getOwner()).projectile(this).victim(entity).tick(100).force(1).applyType(ApplyType.DE_BUFF);
                MagickReleaseHelper.releaseMagick(context);
            }
        }
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void doClientTask() {
        {
            Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
            this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.2F, 0.02F));
        }
        super.doClientTask();
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(ModSounds.wither_spawn.get(), 2.0F, 1.0F + this.rand.nextFloat() / 3);
        }
        if(this.ticksExisted % 13 == 0)
        {
            this.playSound(ModSounds.wither_ambience.get(), 0.7F, 0.85F - this.rand.nextFloat() / 5);
        }
    }

    protected void applyParticle() {
        if(this.ticksExisted % 2 == 0){
            LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(this.getPosX()
                    , this.getPosY() + this.getHeight() / 2
                    , this.getPosZ())
                    , 0.45f, 0.45f, this.rand.nextFloat(), 60, this.spellContext().element.getRenderer());
            par.setGlow();
            //par.setParticleGravity(0);
            par.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05);
            MagickCore.addMagickParticle(par);
        }
        if(this.ticksExisted % 5 == 0){
            LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , this.rand.nextFloat() * this.getWidth(), this.rand.nextFloat() * this.getWidth(), 0.6f + 0.4f * this.rand.nextFloat(), this.spellContext().element.getRenderer().getParticleRenderTick() / 4, this.spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(35.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(16), predicate);
    }
}
