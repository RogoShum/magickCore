package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.entity.easyrender.ContextPointerRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.DawnWardRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class DawnWardEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/dawn_ward.png");

    public DawnWardEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new DawnWardRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.tickCount <= 15)
            return;
        initial = true;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    protected void applyParticle() {
        if(this.tickCount % 2 == 0 && this.level.isClientSide) {
            LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getZ())
                    , 0.2f, 0.2f, 0.9f, 50, this.spellContext().element.getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected void makeSound() {
        if(this.tickCount == 1)
        {
            this.playSound(ModSounds.dawnward_spawn.get(), 2.0F, 1.0F - this.random.nextFloat());
        }

        if(this.tickCount % 10 == 0)
        {
            this.playSound(ModSounds.wall_ambience.get(), 0.2F, 1.0F - this.random.nextFloat());
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !(entity instanceof Player) && testBoundingBox(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void collideWithNearbyEntities() {
        if(!initial) return;
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(2));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);

                if(testBoundingBox(entity))
                    this.push(entity);
            }
        }
    }

    public boolean testBoundingBox(Entity entityIn) {
        AABB box = entityIn.getBoundingBox();

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
        Vec3 vec = new Vec3(x, y, z);
        Vec3 center = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ());

        if(vec.subtract(center).length() <= (this.getBbWidth() / 2) + 0.25)
            flag = true;
        return flag;
    }

    @Override
    public void push(Entity entityIn) {
        if(MagickReleaseHelper.sameLikeOwner(this.getOwner(), entityIn)) {
            if(entityIn instanceof LivingEntity) {
                MagickContext context = new MagickContext(level).noCost().caster(this.getOwner()).projectile(this).victim(entityIn).tick(300).force(5).applyType(ApplyType.BUFF);
                MagickReleaseHelper.releaseMagick(context);
                ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20, 8));
            }
            return;
        }

        Vec3 vec = new Vec3(this.getX() - entityIn.getX(), (this.getY() + this.getBbHeight() / 2) - (entityIn.getY() + entityIn.getBbHeight() / 2), this.getZ() - entityIn.getZ());
        hitReactions.put(entityIn.getId(), new VectorHitReaction(vec.normalize()));
        double d0 = entityIn.getX() - this.getX();
        double d1 = entityIn.getZ() - this.getZ();
        double d2 = Mth.absMax(d0, d1);
        if (d2 >= (double)0.01F) {
            d2 = (double)Mth.sqrt((float) d2);
            d0 = d0 / d2;
            d1 = d1 / d2;
            double d3 = 1.0D / d2;
            if (d3 > 1.0D) {
                d3 = 1.0D;
            }
            d0 *= d3;
            d1 *= d3;
            d0 *= (double)0.05F;
            d1 *= (double)0.05F;
            if (!this.isVehicle()) {
                this.push(-d0, 0.0D, -d1);
            }

            if (!entityIn.isVehicle()) {
                entityIn.push(d0, 0.0D, d1);
                this.playSound(SoundEvents.SLIME_BLOCK_FALL, 2.0F, 1.0F - this.random.nextFloat());
            }
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
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
