package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.lib.LibBuff;
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

public class RadianceWellEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/radiance_well.png");
    public RadianceWellEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.ticksExisted <= 10)
            return;
        initial = true;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), predicate);
    }

    @Override
    public void releaseMagick() {
        List<Entity> livings = findEntity((living) -> living instanceof LivingEntity && MagickReleaseHelper.sameLikeOwner(this.getOwner(), living));
        livings.forEach(living -> ModBuff.applyBuff(living, LibBuff.RADIANCE_WELL, 20, 3, true));
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void doClientTask() {
        super.doClientTask();
        if(this.ticksExisted % 2 == 0)
        {
            Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
            this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.06F, 0.01F));
        }
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(ModSounds.wall_spawn.get(), 2.0F, 0.7F + this.rand.nextFloat());
        }

        if(this.ticksExisted % 10 == 0)
        {
            this.playSound(ModSounds.wall_ambience.get(), 1.0F, 0.8F - this.rand.nextFloat() / 4);
        }

        if(this.ticksExisted == this.spellContext().tick - 5)
            this.playSound(ModSounds.wall_dissipate.get(), 2.0F, 1.0F + this.rand.nextFloat());
    }

    @Override
    protected void applyParticle()
    {
        if(this.world.isRemote() && this.spellContext().element != null)
        {
            if(this.ticksExisted % 5 ==0) {
                LitParticle cc = new LitParticle(this.world, this.spellContext().element.getRenderer().getRingTexture()
                        , new Vector3d(this.getPosX()
                        , this.getPosY() + this.getHeight()
                        , this.getPosZ())
                        , 0.7f, 0.7f, 0.4f, 60, this.spellContext().element.getRenderer());
                cc.setGlow();
                cc.setParticleGravity(0);
                MagickCore.addMagickParticle(cc);
            }
            for(int i = 0; i < 5; ++i) {
                LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 5
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.1f, 0.1f, this.rand.nextFloat(), 60, this.spellContext().element.getRenderer());
                par.setGlow();
                par.setParticleGravity(-0.15f);
                par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.01);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }
}
