package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.init.ModEntites;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ManaOrbEntity extends ManaProjectileEntity {
    public ManaOrbEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        if(this.func_234616_v_() == null || p_213868_1_.getEntity().getEntityId() != this.func_234616_v_().getEntityId())
        {
            if(this.getManaData() != null) {
                if(this.getManaType().getLabel().equals(EnumManaType.ATTACK.getLabel()))
                {
                    EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
                    MinecraftForge.EVENT_BUS.post(event);
                    this.getElement().getAbility().damageEntity(this.func_234616_v_(), this, p_213868_1_.getEntity(), this.getTickTime(), this.getForce() / 5);
                }

                if(this.getManaType().getLabel().equals(EnumManaType.DEBUFF.getLabel()))
                    this.getElement().getAbility().applyDebuff(p_213868_1_.getEntity(), this.getTickTime(), this.getForce() / 5);
                if(this.getManaType().getLabel().equals(EnumManaType.BUFF.getLabel()))
                    this.getElement().getAbility().applyBuff(p_213868_1_.getEntity(), this.getTickTime(), this.getForce() / 5);
            }

            if (!this.world.isRemote) {
                this.world.setEntityState(this, (byte)3);
                this.remove();
            }
        }
    }

    @Override
    public void remove() {
        ManaSphereEntity sphere = new ManaSphereEntity(ModEntites.mana_sphere, this.world);
        sphere.setPosition(this.getPosX(), this.getPosY() - sphere.getHeight() / 2.3f, this.getPosZ());
        sphere.setElement(this.getElement());
        sphere.setManaType(this.getManaType());
        sphere.setForce(this.getForce());
        sphere.setRange(this.getRange());
        sphere.setTickTime(this.getTickTime());
        sphere.setOwner(this.func_234616_v_());
        this.world.addEntity(sphere);
        super.remove();
    }
}
