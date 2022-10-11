package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ManaStarEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_star.png");
    public ManaStarEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    protected void traceTarget() {
        if(this.ticksExisted <= 3 || !spellContext().containChild(LibContext.TRACE) || this.world.isRemote) return;
        TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
        Entity entity = traceContext.entity;

        if(entity == null && traceContext.uuid != MagickCore.emptyUUID) {
            entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
            traceContext.entity = entity;
        } else if(entity != null) {
            Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
            Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

            Vector3d motion = goal.subtract(self).normalize().scale(this.getMotion().length() * 0.1);
            this.setMotion(motion.add(this.getMotion()));
        }
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getTrailTexture()
                , new Vector3d(this.lastTickPosX + (this.getPosX() - this.lastTickPosX) * partialTicks
                , this.lastTickPosY + (this.getPosY() - this.lastTickPosY) * partialTicks + this.getHeight() / 2
                , this.lastTickPosZ + (this.getPosZ() - this.lastTickPosZ) * partialTicks)
                , 0.05f, 0.05f, 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }
}
