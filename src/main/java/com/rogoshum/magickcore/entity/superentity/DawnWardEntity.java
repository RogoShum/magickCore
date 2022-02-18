package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModSounds;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.magick.element.MagickElement;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DawnWardEntity extends ManaPointEntity implements ISuperEntity {
    private float hue = 0;

    public DawnWardEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public DawnWardEntity(EntityType<?> entityTypeIn, World worldIn, MagickElement element) {
        super(entityTypeIn, worldIn, element);
    }

    public float[] getColor() {
        Color colorInstance = Color.getHSBColor(hue, 1, 1);
        return new float[]{(float) colorInstance.getRed() / 255f, (float) colorInstance.getGreen() / 255f, (float) colorInstance.getBlue() / 255f};
    }

    @Override
    public void tick() {
        super.tick();
        if(hue < 1)
            hue +=0.01;
        else
            hue = 0;

        if(this.ticksExisted <= 25)
            return;
        initial = true;

        Iterator<Integer> iter = hitReactions.keySet().iterator();
        while (iter.hasNext()) {
            VectorHitReaction reaction = hitReactions.get(iter.next());
            //MagickCore.LOGGER.info("isInvalid " + reaction.isInvalid());
            if (reaction.isInvalid()) {
                iter.remove();
            }
            reaction.tick();
        }

        if(this.ticksExisted % 2 == 0 && this.world.isRemote) {
            LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , 0.2f, 0.2f, 0.9f, 50, this.getElement().getRenderer());
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

        Vector3d vec = new Vector3d(entityIn.getPosX() - this.getPosX(), (entityIn.getPosY() + entityIn.getHeight() / 2) - (this.getPosY() + this.getHeight() / 2), entityIn.getPosZ() - this.getPosZ());
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
    public int getSourceLight() {
        return 15;
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5F;
    }
}
