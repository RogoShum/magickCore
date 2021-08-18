package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
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
                EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
                MinecraftForge.EVENT_BUS.post(event);
                ReleaseAttribute attribute = new ReleaseAttribute(this.getOwner(), this, p_213868_1_.getEntity(), this.getTickTime(), this.getForce() / 5);
                MagickReleaseHelper.applyElementFunction(this.getElement(), this.getManaType(), attribute);
            }

            if (!this.world.isRemote) {
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

    @Override
    public int getSourceLight() {
        return 3;
    }
}
