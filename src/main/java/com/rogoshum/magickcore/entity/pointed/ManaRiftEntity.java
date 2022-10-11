package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ManaRiftEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_rift.png");
    public ManaRiftEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void releaseMagick() {
        List<Entity> livings = this.findEntity();
        for (Entity living : livings) {
            if (MagickReleaseHelper.sameLikeOwner(this.getOwner(), living)) {
                MagickContext context = new MagickContext(world).saveMana().caster(this.getOwner()).projectile(this).victim(living).tick(100).force(spellContext().force).applyType(EnumApplyType.BUFF);
                MagickReleaseHelper.releaseMagick(context);
            } else {
                MagickContext context = new MagickContext(world).saveMana().caster(this.getOwner()).projectile(this).victim(living).tick(100).force(spellContext().force).applyType(EnumApplyType.DE_BUFF);
                MagickReleaseHelper.releaseMagick(context);
            }
        }
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void doClientTask() {
        Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.rand.nextInt(200) - this.rand.nextInt(2000), new VectorHitReaction(rand, 0.1F, 0.005F));
        super.doClientTask();
    }

    @Override
    protected void makeSound() {
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.BLOCK_BAMBOO_SAPLING_PLACE, 2.0F, 1.0F + this.rand.nextFloat());
        }
    }

    @Override
    public float getSourceLight() {
        return 8;
    }

    protected void applyParticle()
    {
        if(this.world.isRemote())
        {
            for(int i = 0; i < 1; ++i) {
                LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 5
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.15f, 0.15f, this.rand.nextFloat(), 60, this.spellContext().element.getRenderer());
                par.setGlow();
                par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
                MagickCore.addMagickParticle(par);
            }

            for(int i = 0; i < 1; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , this.getPosY() + this.getHeight() / 6
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , this.rand.nextFloat() * this.getWidth() / 4, this.rand.nextFloat() * this.getWidth() / 4, 0.5f * this.rand.nextFloat(), this.spellContext().element.getRenderer().getParticleRenderTick(), this.spellContext().element.getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.05);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(1), predicate);
    }
}
