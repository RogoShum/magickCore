package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.entity.IManaEntity;
import com.rogoshum.magickcore.common.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.api.enums.TargetType;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.WandItem;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class EntityHunterEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/entity_capture.png");
    public EntityHunterEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public Entity victim;

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        List<Entity> entityList = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), predicate);
        for(int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if(!suitableEntity(entity)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                context.conditions.forEach((condition -> {
                    if(condition.getType() == TargetType.TARGET) {
                        if(!condition.test(entity))
                            pass.set(false);
                    } else if(!condition.test(this.getOwner()))
                        pass.set(false);
                }));
            }
            if(pass.get()) {
                return Util.make(new ArrayList<>(), list -> list.add(entity));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void releaseMagick() {
        if(victim != null) {
            Vector3d motion = this.getPositionVec().add(0, getHeight() * 0.5, 0).subtract(victim.getPositionVec().add(0, victim.getHeight() * 0.5, 0)).normalize();
            victim.setMotion(motion.x, motion.y, motion.z);
            if(!victim.isAlive())
                victim = null;
            return;
        }
        float width = getWidth();
        float height = getHeight();
        Predicate<Entity> entityPredicate = entity -> ((entity instanceof LivingEntity && spellContext().force > 7) || entity instanceof IManaEntity) && entity.getHeight() < height && entity.getWidth() < width;
        List<Entity> entities = findEntity(entityPredicate);
        for (Entity entity : entities) {
            if(victim == null)
                victim = entity;
        }
    }

    @Override
    public void reSize() {
        float height = getType().getHeight() + spellContext().range * 0.5f;
        if(getHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range * 0.5f;
        if(getWidth() != width)
            this.setWidth(width);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.NON_MANA;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (!this.world.isRemote && hand == Hand.MAIN_HAND) {
            if(player.getHeldItemMainhand().getItem() instanceof WandItem) {
                if(this.victim != null) {
                    ItemStack type = new ItemStack(ModItems.ENTITY_TYPE.get());
                    ExtraDataUtil.itemManaData(type, (data) -> data.spellContext().applyType(ApplyType.SPAWN_ENTITY).addChild(SpawnContext.create(victim.getType())));
                    this.entityDropItem(type, this.getHeight() * 0.5f);
                }
                this.remove();
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void applyParticle() {
        if(victim == null) return;
        Vector3d center = victim.getPositionVec().add(0, victim.getHeight() * 0.5, 0);
        Vector3d center1 = getPositionVec().add(0, getHeight() * 0.5, 0);
        for (int i = 0; i < RenderHelper.vertex_list.length; ++i) {
            float[] vertex = RenderHelper.vertex_list[i];
            double d0 = getWidth() * 0.7f;
            Vector3d vector3d = new Vector3d(vertex[0], vertex[1], vertex[2]).scale(Math.sqrt(d0 * d0 * 2)).add(center1);
            double dis = vector3d.distanceTo(center);
            int distance = Math.max((int) (10 * dis), 1);
            float directionPoint = (float) (this.ticksExisted % distance) / distance;
            int b = (int) (directionPoint * distance);

            float scale;
            for (int c = 0; c < distance; c++) {
                if(c == b)
                    scale = 0.25f;
                else
                    scale = 0.10f;
                double trailFactor = c / (distance - 1.0D);
                Vector3d pos = ParticleUtil.drawLine(vector3d, center, trailFactor);
                LitParticle par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(pos.x, pos.y, pos.z), scale, scale, 1.0f, 5, spellContext().element.getRenderer());
                par.setParticleGravity(0);
                par.setLimitScale();
                par.setGlow();
                MagickCore.addMagickParticle(par);
            }
        }
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }
}
