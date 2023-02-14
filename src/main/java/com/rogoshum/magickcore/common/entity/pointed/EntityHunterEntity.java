package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class EntityHunterEntity extends ManaPointEntity implements IManaRefraction {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/entity_capture.png");
    public EntityHunterEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public Entity victim;

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        List<Entity> entityList = this.level.getEntities(this, this.getBoundingBox(), predicate);
        for(int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if(!suitableEntity(entity)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                if(!context.test(this.getOwner(), entity))
                    pass.set(false);
            }
            if(pass.get()) {
                return Util.make(new ArrayList<>(), list -> list.add(entity));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean releaseMagick() {
        if(victim != null) {
            Vec3 motion = this.position().add(0, getBbHeight() * 0.5, 0).subtract(victim.position().add(0, victim.getBbHeight() * 0.5, 0)).normalize();
            victim.setDeltaMovement(motion.x, motion.y, motion.z);
            if(!victim.isAlive())
                victim = null;
            return false;
        }
        float width = getBbWidth();
        float height = getBbHeight();
        Predicate<Entity> entityPredicate = entity -> ((entity instanceof LivingEntity && spellContext().force > 7) || entity instanceof IManaEntity || entity instanceof Projectile) && entity.getBbHeight() < height && entity.getBbWidth() < width;
        List<Entity> entities = findEntity(entityPredicate);
        for (Entity entity : entities) {
            if(victim == null)
                victim = entity;
        }
        return false;
    }

    @Override
    public void reSize() {
        float height = getType().getHeight() + spellContext().range * 0.5f;
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range * 0.5f;
        if(getBbWidth() != width)
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
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (!this.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if(player.getMainHandItem().getItem() instanceof WandItem) {
                if(this.victim != null) {
                    ItemStack type = new ItemStack(ModItems.ENTITY_TYPE.get());
                    ExtraDataUtil.itemManaData(type, (data) -> data.spellContext().applyType(ApplyType.SPAWN_ENTITY).addChild(SpawnContext.create(victim.getType())));
                    this.spawnAtLocation(type, this.getBbHeight() * 0.5f);
                }
                this.remove(RemovalReason.DISCARDED);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean fixedPosition() {
        return false;
    }

    @Override
    protected void applyParticle() {
        if(victim == null) return;
        Vec3 center = victim.position().add(0, victim.getBbHeight() * 0.5, 0);
        Vec3 center1 = position().add(0, getBbHeight() * 0.5, 0);
        for (int i = 0; i < RenderHelper.vertex_list.length; ++i) {
            float[] vertex = RenderHelper.vertex_list[i];
            double d0 = getBbWidth() * 0.7f;
            Vec3 vector3d = new Vec3(vertex[0], vertex[1], vertex[2]).scale(Math.sqrt(d0 * d0 * 2)).add(center1);
            double dis = vector3d.distanceTo(center);
            int distance = Math.max((int) (10 * dis), 1);
            float directionPoint = (float) (this.tickCount % distance) / distance;
            int b = (int) (directionPoint * distance);

            float scale;
            for (int c = 0; c < distance; c++) {
                if(c == b)
                    scale = 0.25f;
                else
                    scale = 0.10f;
                double trailFactor = c / (distance - 1.0D);
                Vec3 pos = ParticleUtil.drawLine(vector3d, center, trailFactor);
                LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                        , new Vec3(pos.x, pos.y, pos.z), scale, scale, 1.0f, 5, spellContext().element.getRenderer());
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
