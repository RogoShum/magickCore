package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.AscendantRealmRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AscendantRealmEntity extends ManaPointEntity implements ISuperEntity{
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/ascendant_realm.png");
    public AscendantRealmEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new AscendantRealmRenderer(this);
    }

    @Override
    protected void makeSound() {
        if(this.tickCount == 1) {
            this.playSound(SoundEvents.BLAZE_DEATH, 2.0F, 1.0F - this.random.nextFloat());
        }

        if(this.random.nextInt(200) == 0) {
            this.playSound(SoundEvents.BLAZE_AMBIENT, 2.0F, 1.0F - this.random.nextFloat());
        }
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }
    protected void applyParticle() {
        double width = this.getBbWidth() * 0.5;
        double height = this.getBbHeight() * 0.5;
        for(int i = 0; i < 1; ++i) {
            LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * width + this.getX()
                    , MagickCore.getNegativeToOne() * height + this.getY() + this.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * width + this.getZ())
                    , 0.15f, 0.15f, 1.0f, 60, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
            MagickCore.addMagickParticle(par);
        }
        for(int i = 0; i < 1; ++i) {
            LitParticle litPar = new LitParticle(this.level, this.spellContext().element.getRenderer().getMistTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * width + this.getX()
                    , MagickCore.getNegativeToOne() * height + this.getY() + this.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * width + this.getZ())
                    , this.random.nextFloat(), this.random.nextFloat(), 0.7f, this.spellContext().element.getRenderer().getParticleRenderTick(), this.spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(15.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.1);
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    public boolean releaseMagick() {
        List<Entity> list = findEntity(entity -> entity instanceof LivingEntity);

        for (Entity living : list) {
            if(!(living instanceof Mob))
                continue;
            TakenEntityData state = ExtraDataUtil.takenEntityData(living);
            if(living.isAlive() && !state.getOwnerUUID().equals(this.getOwnerUUID()) && !MagickReleaseHelper.sameLikeOwner(this.getOwner(), living)) {
                MagickContext context = new MagickContext(this.level).noCost().caster(this.getOwner()).projectile(this).victim(living).tick((int) (this.spellContext().tick * 0.5)).force(ManaLimit.FORCE.getValue()).applyType(ApplyType.HIT_ENTITY);
                MagickReleaseHelper.releaseMagick(context);
                context = new MagickContext(this.level).noCost().caster(this.getOwner()).projectile(this).victim(living).tick((int) (this.spellContext().tick * 0.5)).force(7.5f).applyType(ApplyType.ATTACK);
                MagickReleaseHelper.releaseMagick(context);
            }
        }

        return true;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(1), predicate);
    }

    @Override
    public float getSourceLight() {
        return -15f;
    }

    @Override
    public float eyeHeight() {
        return super.eyeHeight();
    }
}
