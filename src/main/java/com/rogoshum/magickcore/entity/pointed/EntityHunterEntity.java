package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.WandItem;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.tool.ParticleHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
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

public class EntityHunterEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/entity_capture.png");
    public EntityHunterEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public Entity victim;

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        List<Entity> entityList = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range), predicate);
        for(int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if(!suitableEntity(entity)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                context.conditions.forEach((condition -> {
                    if(condition.getType() == EnumTargetType.TARGET) {
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
            Vector3d motion = this.getPositionVec().add(0, getHeight() / 2, 0).subtract(victim.getPositionVec().add(0, victim.getHeight() / 2, 0)).normalize();
            victim.setMotion(motion.x, motion.y, motion.z);
            return;
        }
        List<Entity> entities = findEntity(entity -> entity.getHeight() < this.getHeight() && entity.getWidth() < this.getWidth());
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
                    this.victim.remove();
                    ItemStack type = new ItemStack(ModItems.ENTITY_TYPE.get());
                    ExtraDataHelper.itemManaData(type, (data) -> data.spellContext().applyType(EnumApplyType.SPAWN_ENTITY).addChild(SpawnContext.create(victim.getType())));
                    this.entityDropItem(type, this.getHeight() / 2);
                }
                this.remove();
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void applyParticle() {
        List<Vector3d> list = ParticleHelper.drawRectangle(this.positionVec().add(0, this.getHeight() / 2, 0), 0.3f, getWidth(), getWidth(), getHeight());
        for(int i = 0; i < list.size(); ++i) {
            Vector3d pos = list.get(i);
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , pos
                    , 0.1f, 0.1f, 1.0f, 2, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            par.addMotion(MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f);
            MagickCore.addMagickParticle(par);
        }

        if(victim == null) return;

    }
}
