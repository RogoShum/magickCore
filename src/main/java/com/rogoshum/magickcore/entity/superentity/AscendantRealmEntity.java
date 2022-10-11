package com.rogoshum.magickcore.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class AscendantRealmEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/ascendant_realm.png");
    public AscendantRealmEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void makeSound() {
        if(this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.ENTITY_BLAZE_DEATH, 2.0F, 1.0F - this.rand.nextFloat());
        }

        if(this.rand.nextInt(200) == 0)
        {
            this.playSound(SoundEvents.ENTITY_BLAZE_AMBIENT, 2.0F, 1.0F - this.rand.nextFloat());
        }
    }

    protected void applyParticle()
    {
        if(this.world.isRemote() && this.spellContext().element != null)
        {
            for(int i = 0; i < 5; ++i) {
                LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.15f, 0.15f, this.rand.nextFloat(), 60, this.spellContext().element.getRenderer());
                par.setGlow();
                par.setParticleGravity(0);
                par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
                MagickCore.addMagickParticle(par);
            }
            for(int i = 0; i < 1; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , this.rand.nextFloat(), this.rand.nextFloat(), 0.3f + 0.3f * this.rand.nextFloat(), this.spellContext().element.getRenderer().getParticleRenderTick(), this.spellContext().element.getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(35.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.1);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }

    @Override
    public void releaseMagick() {
        List<Entity> list = findEntity(entity -> entity instanceof LivingEntity);

        for (Entity living : list)
        {
            if(!(living instanceof MobEntity))
                continue;
            TakenEntityData state = ExtraDataHelper.takenEntityData(living);
            if(living.isAlive() && !state.getOwnerUUID().equals(this.getOwnerUUID()) && !MagickReleaseHelper.sameLikeOwner(this.getOwner(), living))
            {
                MagickContext context = new MagickContext(this.world).saveMana().caster(this.getOwner()).projectile(this).victim(living).tick(this.spellContext().tick / 4).force(EnumManaLimit.FORCE.getValue()).applyType(EnumApplyType.HIT_ENTITY);
                MagickReleaseHelper.releaseMagick(context);
                context = new MagickContext(this.world).saveMana().caster(this.getOwner()).projectile(this).victim(living).tick(this.spellContext().tick / 4).force(EnumManaLimit.FORCE.getValue()).applyType(EnumApplyType.ATTACK);
                MagickReleaseHelper.releaseMagick(context);
            }
        }
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(1), predicate);
    }
}
