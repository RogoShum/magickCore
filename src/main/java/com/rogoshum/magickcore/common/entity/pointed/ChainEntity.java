package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ChainEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/chain.png");
    private static final EntityDataAccessor<Integer> POST = SynchedEntityData.defineId(ChainEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VICTIM = SynchedEntityData.defineId(ChainEntity.class, EntityDataSerializers.INT);
    protected Entity postEntity;
    protected Entity victimEntity;
    public ChainEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.entityData.define(POST, -1);
        this.entityData.define(VICTIM, -1);
    }

    public void setPostEntity(int entityId) {
        this.entityData.set(POST, entityId);
    }

    public void setVictimEntity(int entityId) {
        this.entityData.set(VICTIM, entityId);
    }

    public int getPostEntity() {
        return this.entityData.get(POST);
    }

    public int getVictimEntity() {
        return this.entityData.get(VICTIM);
    }

    public void setPostEntity(Entity entity) {
        postEntity = entity;
    }

    @Override
    public void reSize() {
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.postEntity == null ? -1 : this.postEntity.getId());
        buffer.writeInt(this.victimEntity == null ? -1 : this.victimEntity.getId());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        int post = additionalData.readInt();
        int victim = additionalData.readInt();
        if(post != -1)
            postEntity = level.getEntity(post);
        if(victim != -1)
            victimEntity = level.getEntity(victim);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        postEntity = null;
        victimEntity = null;
    }

    @Override
    public void tick() {
        super.tick();

        if(victimEntity == null && !level.isClientSide) {
            List<Entity> entities = level.getEntities(this, getBoundingBox(), Entity::isAlive);
            for (Entity entity : entities) {
                if(suitableEntity(entity)) {
                    victimEntity = entity;
                    break;
                }
            }
        } else if(victimEntity != null && !victimEntity.isAlive())
            victimEntity = null;

        if(!level.isClientSide) {
            if(postEntity != null)
                setPostEntity(postEntity.getId());
            else
                setPostEntity(-1);
            if(victimEntity != null)
                setVictimEntity(victimEntity.getId());
            else
                setVictimEntity(-1);
        } else {
            postEntity = level.getEntity(getPostEntity());
            victimEntity = level.getEntity(getVictimEntity());
        }

        if(postEntity != null && !postEntity.isAlive()) {
            if(victimEntity != null)
                setPos(postEntity.getX(), postEntity.getY() + postEntity.getBbHeight() * 0.5 - 0.25, postEntity.getZ());
            postEntity = null;
        }

        if(!fixedPosition() && victimEntity != null) {
            setPos(victimEntity.getX(), victimEntity.getY() + victimEntity.getBbHeight() * 0.5 - 0.25, victimEntity.getZ());
        }
        if(level.isClientSide && !(victimEntity instanceof Player)) return;
        float range = spellContext().range();
        float force = spellContext().force();
        if(victimEntity != null && postEntity != null) {
            if(victimEntity.distanceToSqr(postEntity) >= range * range) {
                Entity slower = victimEntity;
                Entity faster = postEntity;
                Vec3 vicPos = victimEntity.position().add(0,  victimEntity.getBbHeight() * 0.5, 0);
                Vec3 postPos = postEntity.position().add(0,  postEntity.getBbHeight() * 0.5, 0);
                Vec3 fasterPos = postPos;
                Vec3 direction = postPos.subtract(vicPos).normalize();
                if(spellContext().containChild(LibContext.TRACE)) {
                    slower = postEntity;
                    faster = victimEntity;
                    fasterPos = vicPos;
                    direction = vicPos.subtract(postPos).normalize();
                }
                if(slower instanceof LivingEntity)
                    direction = direction.scale(0.03);
                else
                    direction = direction.scale(0.2);
                slower.push(direction.x, direction.y, direction.z);
                if(slower.getDeltaMovement().y <= 0.0D && slower instanceof LivingEntity) {
                    ((LivingEntity) slower).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 1, false, false, false));
                }
                if(victimEntity.distanceToSqr(postEntity) >= (range + force) * (range + force) && slower.getDeltaMovement().normalize().dot(direction.normalize()) < 0) {
                    if(slower == postEntity)
                        postEntity = null;
                    else
                        victimEntity = null;
                }
            }
        } else if(victimEntity != null) {
            if(victimEntity.distanceToSqr(this) >= range * range) {
                Vec3 direction = this.position().add(0,  this.getBbHeight() * 0.5, 0).subtract(victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0));
                if(victimEntity instanceof LivingEntity)
                    direction = direction.scale(0.03);
                else
                    direction = direction.scale(0.2);
                victimEntity.push(direction.x, direction.y, direction.z);
                if(victimEntity.getDeltaMovement().y <= 0.0D && victimEntity instanceof LivingEntity) {
                    ((LivingEntity) victimEntity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 1, false, false, false));
                }
                if(victimEntity.distanceToSqr(this) >= (range + force) * (range + force) && victimEntity.getDeltaMovement().normalize().dot(direction.normalize()) < 0) {
                    victimEntity = null;
                }
            }
        }
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return Collections.emptyList();
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
    protected void applyParticle() {
        Vec3 self = position().add(0, getBbHeight() * 0.5, 0);
        Vec3 target = self;
        if(victimEntity != null && postEntity != null) {
            self = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0);
            target = postEntity.position().add(0, postEntity.getBbHeight() * 0.5, 0);
        } else if(victimEntity != null)
            target = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0);
        Vec3 end = self;
        Vec3 start = target;

        LitParticle par = new LitParticle(this.level, spellContext().element().getRenderer().getRuneTexture()
                , self, 0.1f, 0.1f, 1.0f, 1, spellContext().element().getRenderer());
        par.setParticleGravity(0);
        par.setLimitScale();
        par.setGlow();
        par.useShader(LibShaders.BITS);
        MagickCore.addMagickParticle(par);

        par = new LitParticle(this.level, spellContext().element().getRenderer().getRuneTexture()
                , end, 0.1f, 0.1f, 1.0f, 1, spellContext().element().getRenderer());
        par.setParticleGravity(0);
        par.setLimitScale();
        par.setGlow();
        par.useShader(LibShaders.BITS);
        MagickCore.addMagickParticle(par);

        double dis = Math.max(start.subtract(end).length() * 10, 1);
        float scale = 0.10f;
        for (int i = 0; i < dis; i++) {
            double trailFactor = i / Math.max((dis - 1.0D), 1);
            Vec3 pos = ParticleUtil.drawLine(start, end, trailFactor);
            par = new LitParticle(this.level, spellContext().element().getRenderer().getParticleTexture()
                    , new Vec3(pos.x, pos.y, pos.z), scale, scale, 0.5f, 5, spellContext().element().getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    protected boolean fixedPosition() {
        return postEntity == null;
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        super.beforeJoinWorld(context);
        victimEntity = context.victim;
        if(context.separator instanceof IManaEntity)
            victimEntity = context.separator;
        MagickContext magickContext = MagickContext.create(level, spellContext().postContext())
                .caster(getCaster()).projectile(this)
                .victim(victimEntity).noCost();
        if(spellContext().containChild(LibContext.DIRECTION))
            magickContext.<MagickContext>replenishChild(spellContext().getChild(LibContext.DIRECTION));
        else if(victimEntity != null) {
            Vec3 dir = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0).subtract((this).position());
            magickContext.<MagickContext>replenishChild(DirectionContext.create(dir));
        }
        MagickReleaseHelper.releaseMagick(beforeCast(magickContext));
    }
}
