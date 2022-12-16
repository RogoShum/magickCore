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
    private static final DataParameter<Integer> POST = EntityDataManager.createKey(ChainEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> VICTIM = EntityDataManager.createKey(ChainEntity.class, DataSerializers.VARINT);
    protected Entity postEntity;
    protected Entity victimEntity;
    public ChainEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.dataManager.register(POST, -1);
        this.dataManager.register(VICTIM, -1);
    }

    public void setPostEntity(int entityId) {
        this.dataManager.set(POST, entityId);
    }

    public void setVictimEntity(int entityId) {
        this.dataManager.set(VICTIM, entityId);
    }

    public int getPostEntity() {
        return this.dataManager.get(POST);
    }

    public int getVictimEntity() {
        return this.dataManager.get(VICTIM);
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
        buffer.writeInt(this.postEntity == null ? -1 : this.postEntity.getEntityId());
        buffer.writeInt(this.victimEntity == null ? -1 : this.victimEntity.getEntityId());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        super.readSpawnData(additionalData);
        int post = additionalData.readInt();
        int victim = additionalData.readInt();
        if(post != -1)
            postEntity = world.getEntityByID(post);
        if(victim != -1)
            victimEntity = world.getEntityByID(victim);
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

        if(victimEntity == null && !world.isRemote) {
            List<Entity> entities = world.getEntitiesInAABBexcluding(this, getBoundingBox(), null);
            for (Entity entity : entities) {
                if(suitableEntity(entity)) {
                    victimEntity = entity;
                    break;
                }
            }
        } else if(victimEntity != null && !victimEntity.isAlive())
            victimEntity = null;

        if(!world.isRemote) {
            if(postEntity != null)
                setPostEntity(postEntity.getEntityId());
            else
                setPostEntity(-1);
            if(victimEntity != null)
                setVictimEntity(victimEntity.getEntityId());
            else
                setVictimEntity(-1);
        } else {
            postEntity = world.getEntityByID(getPostEntity());
            victimEntity = world.getEntityByID(getVictimEntity());
        }

        if(postEntity != null && !postEntity.isAlive()) {
            if(victimEntity != null)
                setPosition(postEntity.getPosX(), postEntity.getPosY() + postEntity.getHeight() * 0.5 - 0.25, postEntity.getPosZ());
            postEntity = null;
        }

        if(!fixedPosition() && victimEntity != null) {
            setPosition(victimEntity.getPosX(), victimEntity.getPosY() + victimEntity.getHeight() * 0.5 - 0.25, victimEntity.getPosZ());
        }
        if(world.isRemote && !(victimEntity instanceof PlayerEntity)) return;
        float range = spellContext().range;
        float force = spellContext().force;
        if(victimEntity != null && postEntity != null) {
            if(victimEntity.getDistanceSq(postEntity) >= range * range) {
                Entity slower = victimEntity;
                Entity faster = postEntity;
                Vector3d vicPos = victimEntity.getPositionVec().add(0,  victimEntity.getHeight() * 0.5, 0);
                Vector3d postPos = postEntity.getPositionVec().add(0,  postEntity.getHeight() * 0.5, 0);
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
                slower.addVelocity(direction.x, direction.y, direction.z);
                if(slower.getMotion().y <= 0.0D && slower instanceof LivingEntity) {
                    ((LivingEntity) slower).addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 1, false, false, false));
                }
                if(victimEntity.getDistanceSq(postEntity) >= (range + force) * (range + force) && slower.getMotion().normalize().dotProduct(direction.normalize()) < 0) {
                    if(slower == postEntity)
                        postEntity = null;
                    else
                        victimEntity = null;
                }
            }
        } else if(victimEntity != null) {
            if(victimEntity.getDistanceSq(this) >= range * range) {
                Vector3d direction = this.getPositionVec().add(0,  this.getHeight() * 0.5, 0).subtract(victimEntity.getPositionVec().add(0, victimEntity.getHeight() * 0.5, 0));
                if(victimEntity instanceof LivingEntity)
                    direction = direction.scale(0.03);
                else
                    direction = direction.scale(0.2);
                victimEntity.addVelocity(direction.x, direction.y, direction.z);
                if(victimEntity.getMotion().y <= 0.0D && victimEntity instanceof LivingEntity) {
                    ((LivingEntity) victimEntity).addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 1, false, false, false));
                }
                if(victimEntity.getDistanceSq(this) >= (range + force) * (range + force) && victimEntity.getMotion().normalize().dotProduct(direction.normalize()) < 0) {
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
        Vector3d self = getPositionVec().add(0, getHeight() * 0.5, 0);
        Vector3d target = self;
        if(victimEntity != null && postEntity != null) {
            self = victimEntity.getPositionVec().add(0, victimEntity.getHeight() * 0.5, 0);
            target = postEntity.getPositionVec().add(0, postEntity.getHeight() * 0.5, 0);
        } else if(victimEntity != null)
            target = victimEntity.getPositionVec().add(0, victimEntity.getHeight() * 0.5, 0);
        Vector3d end = self;
        Vector3d start = target;
        double dis = Math.max(start.subtract(end).length() * 10, 1);
        float scale = 0.10f;
        for (int i = 0; i < dis; i++) {
            double trailFactor = i / Math.max((dis - 1.0D), 1);
            Vector3d pos = ParticleUtil.drawLine(start, end, trailFactor);
            LitParticle par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
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
        if(context.projectile instanceof IManaEntity && ((IManaEntity) context.projectile).spellContext().containChild(LibContext.SEPARATOR))
            victimEntity = context.projectile;
        MagickContext magickContext = MagickContext.create(world, spellContext().postContext)
                .caster(getOwner()).projectile(this)
                .victim(victimEntity).noCost();
        if(spellContext().containChild(LibContext.DIRECTION))
            magickContext.<MagickContext>replenishChild(spellContext().getChild(LibContext.DIRECTION));
        else if(victimEntity != null) {
            Vector3d dir = victimEntity.getPositionVec().add(0, victimEntity.getHeight() * 0.5, 0).subtract((this).getPositionVec());
            magickContext.<MagickContext>replenishChild(DirectionContext.create(dir));
        }
        MagickReleaseHelper.releaseMagick(beforeCast(magickContext));
    }
}
