package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.LeadItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
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
    protected Entity postEntity;
    protected Entity victimEntity;
    public ChainEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
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
    public void tick() {
        super.tick();

        if(victimEntity == null) {
            List<Entity> entities = world.getEntitiesInAABBexcluding(this, getBoundingBox(), null);
            for (Entity entity : entities) {
                if(suitableEntity(entity)) {
                    victimEntity = entity;
                    break;
                }
            }
        } else if(!victimEntity.isAlive())
            victimEntity = null;
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
        if(victimEntity != null && postEntity != null) {
            if(victimEntity.getDistanceSq(postEntity) >= range * range) {
                Entity slower = victimEntity;
                Vector3d vicPos = victimEntity.getPositionVec().add(0,  victimEntity.getHeight() * 0.5, 0);
                Vector3d postPos = postEntity.getPositionVec().add(0,  postEntity.getHeight() * 0.5, 0);
                Vector3d fasterPos = postPos;
                Vector3d direction = vicPos.subtract(postPos).normalize();
                if(postEntity.getMotion().lengthSquared() <= 0.3) {
                    slower = postEntity;
                    fasterPos = vicPos;
                    direction = postPos.subtract(vicPos).normalize();
                }
                direction = fasterPos.add(direction.scale(range));
                slower.setMotion(Vector3d.ZERO);
                slower.setPosition(direction.x, direction.y - slower.getHeight() * 0.5, direction.z);
            }
        } else if(victimEntity != null) {
            if(victimEntity.getDistanceSq(this) >= range * range) {
                Vector3d direction = victimEntity.getPositionVec().add(0,  victimEntity.getHeight() * 0.5, 0).subtract(this.getPositionVec().add(0, getHeight() * 0.5, 0)).normalize();
                direction = this.getPositionVec().add(direction.scale(range));
                victimEntity.setPosition(direction.x, direction.y, direction.z);
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
        return null;
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
            double trailFactor = i / (dis - 1.0D);
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
