package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.ISuperEntity;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaPointEntity;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Iterator;

public class RadianceWellEntity extends ManaPointEntity implements ISuperEntity {
    public RadianceWellEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.1F, 0.005F));

        Iterator<Integer> iter = hitReactions.keySet().iterator();
        while (iter.hasNext()) {
            VectorHitReaction reaction = hitReactions.get(iter.next());
            //MagickCore.LOGGER.info("isInvalid " + reaction.isInvalid());
            if (reaction.isInvalid()) {
                iter.remove();
            }
            reaction.tick();
        }

        this.traceEntity(null, 2, new Vector3d(0, 0, 0), 0f, 0);

        Iterator<Integer> it = traceEntity.keySet().iterator();
        while (it.hasNext()) {
            Integer id = it.next();
            LivingEntity living = (LivingEntity) this.world.getEntityByID(id);
            if(living != null && !living.removed && living instanceof PlayerEntity)
            {
                ModBuff.applyBuff(living, LibBuff.RADIANCE_WELL, 20, 3, true);
            }
        }

        applyParticle();
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.getElement() != null)
        {
            if(this.ticksExisted % 5 ==0) {
                LitParticle cc = new LitParticle(this.world, this.getElement().getRenderer().getRingTexture()
                        , new Vector3d(this.getPosX()
                        , this.getPosY() + this.getHeight()
                        , this.getPosZ())
                        , 1.1f, 1.1f, 0.4f, 60, this.getElement().getRenderer());
                cc.setGlow();
                cc.setParticleGravity(0);
                MagickCore.addMagickParticle(cc);
            }
            for(int i = 0; i < 5; ++i) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 5
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.1f, 0.1f, this.rand.nextFloat(), 60, this.getElement().getRenderer());
                par.setGlow();
                par.setParticleGravity(-0.15f);
                par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.01);
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
