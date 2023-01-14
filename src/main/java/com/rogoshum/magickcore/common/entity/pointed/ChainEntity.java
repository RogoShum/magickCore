package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.LeadItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ChainEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/chain.png");
    private static final DataParameter<Integer> POST = EntityDataManager.defineId(ChainEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> VICTIM = EntityDataManager.defineId(ChainEntity.class, DataSerializers.INT);
    protected Entity postEntity;
    protected Entity victimEntity;
    public ChainEntity(EntityType<?> entityTypeIn, World worldIn) {
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
    public void writeSpawnData(PacketBuffer buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.postEntity == null ? -1 : this.postEntity.getId());
        buffer.writeInt(this.victimEntity == null ? -1 : this.victimEntity.getId());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        super.readSpawnData(additionalData);
        int post = additionalData.readInt();
        int victim = additionalData.readInt();
        if(post != -1)
            postEntity = level.getEntity(post);
        if(victim != -1)
            victimEntity = level.getEntity(victim);
    }

    @Override
    public void remove() {
        super.remove();
        postEntity = null;
        victimEntity = null;
    }

    @Override
    public void tick() {
        super.tick();

        if(victimEntity == null && !level.isClientSide) {
            List<Entity> entities = level.getEntities(this, getBoundingBox(), null);
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
        if(level.isClientSide && !(victimEntity instanceof PlayerEntity)) return;
        float range = spellContext().range;
        float force = spellContext().force;
        if(victimEntity != null && postEntity != null) {
            if(victimEntity.distanceToSqr(postEntity) >= range * range) {
                Entity slower = victimEntity;
                Entity faster = postEntity;
                Vector3d vicPos = victimEntity.position().add(0,  victimEntity.getBbHeight() * 0.5, 0);
                Vector3d postPos = postEntity.position().add(0,  postEntity.getBbHeight() * 0.5, 0);
                Vector3d fasterPos = postPos;
                Vector3d direction = postPos.subtract(vicPos).normalize();
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
                    ((LivingEntity) slower).addEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 1, false, false, false));
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
                Vector3d direction = this.position().add(0,  this.getBbHeight() * 0.5, 0).subtract(victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0));
                if(victimEntity instanceof LivingEntity)
                    direction = direction.scale(0.03);
                else
                    direction = direction.scale(0.2);
                victimEntity.push(direction.x, direction.y, direction.z);
                if(victimEntity.getDeltaMovement().y <= 0.0D && victimEntity instanceof LivingEntity) {
                    ((LivingEntity) victimEntity).addEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 1, false, false, false));
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
        Vector3d self = position().add(0, getBbHeight() * 0.5, 0);
        Vector3d target = self;
        if(victimEntity != null && postEntity != null) {
            self = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0);
            target = postEntity.position().add(0, postEntity.getBbHeight() * 0.5, 0);
        } else if(victimEntity != null)
            target = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0);
        Vector3d end = self;
        Vector3d start = target;
        double dis = Math.max(start.subtract(end).length() * 10, 1);
        float scale = 0.10f;
        for (int i = 0; i < dis; i++) {
            double trailFactor = i / Math.max((dis - 1.0D), 1);
            Vector3d pos = ParticleUtil.drawLine(start, end, trailFactor);
            LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(pos.x, pos.y, pos.z), scale, scale, 1.0f, 1, spellContext().element.getRenderer());
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
        MagickContext magickContext = MagickContext.create(level, spellContext().postContext)
                .caster(getOwner()).projectile(this)
                .victim(victimEntity).noCost();
        if(spellContext().containChild(LibContext.DIRECTION))
            magickContext.<MagickContext>replenishChild(spellContext().getChild(LibContext.DIRECTION));
        else if(victimEntity != null) {
            Vector3d dir = victimEntity.position().add(0, victimEntity.getBbHeight() * 0.5, 0).subtract((this).position());
            magickContext.<MagickContext>replenishChild(DirectionContext.create(dir));
        }
        MagickReleaseHelper.releaseMagick(beforeCast(magickContext));
    }
}
