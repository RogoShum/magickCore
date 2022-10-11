package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class ManaLaserEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_laser.png");
    public ManaLaserEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.world.isRemote) return;
        TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
        Entity entity = traceContext.entity;
        if(entity == null && traceContext.uuid != MagickCore.emptyUUID) {
            entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
            traceContext.entity = entity;
            this.setMotion(this.getMotion().scale(1.1));
        } else if(entity != null) {
            Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
            Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

            Vector3d motion = goal.subtract(self).normalize().scale(this.getMotion().length() * 0.175);
            this.setMotion(motion.add(this.getMotion().scale(0.95)));
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    protected void applyParticle() {
        int count = 15;
        double scaleX = (this.getPosX() - this.lastTickPosX)/count;
        double scaleY = (this.getPosY() - this.lastTickPosY)/count;
        double scaleZ = (this.getPosZ() - this.lastTickPosZ)/count;
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getLaserTexture()
                    , new Vector3d(this.lastTickPosX + scaleX * i
                    , this.lastTickPosY + scaleY * i + this.getHeight() / 2
                    , this.lastTickPosZ + scaleZ * i)
                    , 0.05f, 0.05f, 1.0f, 4, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setNoScale();
            //par.addMotion(this.getMotion().x / 2, this.getMotion().y / 2, this.getMotion().z / 2);
            MagickCore.addMagickParticle(par);
        }

        LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                , 0.15f, 0.15f, 1.0f, 10, this.spellContext().element.getRenderer());
        par.setGlow();
        MagickCore.addMagickParticle(par);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
        MinecraftForge.EVENT_BUS.post(event);
        if(!suitableEntity(p_213868_1_.getEntity())) return;
        MagickContext context = MagickContext.create(world, spellContext().postContext).saveMana().caster(this.getOwner()).projectile(this).victim(p_213868_1_.getEntity()).force(this.spellContext().force / 3);
        MagickReleaseHelper.releaseMagick(context);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public void renderFrame(float partialTicks) {
    }
}
