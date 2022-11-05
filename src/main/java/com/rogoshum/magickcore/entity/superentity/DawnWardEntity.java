package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.DawnWardRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class DawnWardEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/dawn_ward.png");

    public DawnWardEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(new DawnWardRenderer(this));
    }

    @Override
    public void tick() {
        super.tick();

        if(this.ticksExisted <= 15)
            return;
        initial = true;
    }

    @Override
    protected void applyParticle() {
        if(this.ticksExisted % 2 == 0 && this.world.isRemote) {
            LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , 0.2f, 0.2f, 0.9f, 50, this.spellContext().element.getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(ModSounds.dawnward_spawn.get(), 2.0F, 1.0F - this.rand.nextFloat());
        }

        if(this.ticksExisted % 10 == 0)
        {
            this.playSound(ModSounds.wall_ambience.get(), 0.2F, 1.0F - this.rand.nextFloat());
        }
    }

    @Override
    public boolean canCollide(Entity entity) {
        return !(entity instanceof PlayerEntity) && testBoundingBox(entity);
    }

    @Override
    public boolean func_241845_aY() {
        return false;
    }

    @Override
    protected void collideWithNearbyEntities() {
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(2));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);

                if(testBoundingBox(entity))
                    this.applyEntityCollision(entity);
            }
        }
    }

    public boolean testBoundingBox(Entity entityIn) {
        AxisAlignedBB box = entityIn.getBoundingBox();

        if(testBoundingBoxPoint(box.minX, box.minY, box.minZ)) return true;

        if(testBoundingBoxPoint(box.minX, box.minY, box.maxZ)) return true;

        if(testBoundingBoxPoint(box.maxX, box.minY, box.maxZ)) return true;

        if(testBoundingBoxPoint(box.maxX, box.minY, box.minZ)) return true;

        if(testBoundingBoxPoint(box.minX, box.maxY, box.minZ)) return true;

        if(testBoundingBoxPoint(box.minX, box.maxY, box.maxZ)) return true;

        if(testBoundingBoxPoint(box.maxX, box.maxY, box.maxZ)) return true;

        return testBoundingBoxPoint(box.maxX, box.maxY, box.minZ);
    }

    public boolean testBoundingBoxPoint(double x, double y, double z) {
        boolean flag = false;
        Vector3d vec = new Vector3d(x, y, z);
        Vector3d center = new Vector3d(this.getPosX(), this.getPosY() + this.getHeight() / 2, this.getPosZ());

        if(vec.subtract(center).length() <= (this.getWidth() / 2) + 0.25)
            flag = true;
        return flag;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {

        if(MagickReleaseHelper.sameLikeOwner(this.getOwner(), entityIn))
        {
            if(entityIn instanceof LivingEntity) {
                ModBuff.applyBuff(entityIn, LibBuff.LIGHT, 300, 1, true);
                ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.ABSORPTION, 20, 8));
            }
            return;
        }

        Vector3d vec = new Vector3d(this.getPosX() - entityIn.getPosX(), (this.getPosY() + this.getHeight() / 2) - (entityIn.getPosY() + entityIn.getHeight() / 2), this.getPosZ() - entityIn.getPosZ());
        hitReactions.put(entityIn.getEntityId(), new VectorHitReaction(vec.normalize()));
        double d0 = entityIn.getPosX() - this.getPosX();
        double d1 = entityIn.getPosZ() - this.getPosZ();
        double d2 = MathHelper.absMax(d0, d1);
        if (d2 >= (double)0.01F) {
            d2 = (double)MathHelper.sqrt(d2);
            d0 = d0 / d2;
            d1 = d1 / d2;
            double d3 = 1.0D / d2;
            if (d3 > 1.0D) {
                d3 = 1.0D;
            }
            this.entityCollisionReduction = -100;
            d0 = d0 * d3;
            d1 = d1 * d3;
            d0 = d0 * (double)0.05F;
            d1 = d1 * (double)0.05F;
            d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
            d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
            if (!this.isBeingRidden()) {
                this.addVelocity(-d0, 0.0D, -d1);
            }

            if (!entityIn.isBeingRidden()) {
                entityIn.addVelocity(d0, 0.0D, d1);
                this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_FALL, 2.0F, 1.0F - this.rand.nextFloat());
            }
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5F;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return new ArrayList<>();
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }
}
