package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.ManaCapacityRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.ManaSphereRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ManaSphereEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_sphere.png");

    public ManaSphereEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ManaSphereRenderer(this);
    }

    @Override
    protected boolean fixedPosition() {
        return false;
    }

    @Override
    protected void doClientTask() {
        super.doClientTask();
        Vector3d rand = new Vector3d(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.random.nextInt(200) - this.random.nextInt(2000), new VectorHitReaction(rand, 0.3F, 0.07F));
    }

    @Override
    protected void makeSound() {
        if(!this.level.isClientSide && this.tickCount == 1)
        {
            this.playSound(ModSounds.sphere_spawn.get(), 1.0F, 1.0F - this.random.nextFloat() / 5);
        }

        if(!this.level.isClientSide && this.tickCount == 10)
            this.playSound(ModSounds.sphere_ambience.get(), 1.0F, (0.85F - this.random.nextFloat() / 5));

        if(!this.level.isClientSide && this.tickCount % 15 == 0 && this.tickCount < this.spellContext().tick - 10 && this.tickCount > 10)
        {
            this.playSound(ModSounds.sphere_ambience.get(), 1.0F, (0.85F - this.random.nextFloat() / 5));
        }

        if(!this.level.isClientSide && this.tickCount == this.spellContext().tick - 20)
        {
            this.playSound(ModSounds.shpere_dissipate.get(), 0.5F, (1.0F - this.random.nextFloat()));
        }
    }

    @Override
    public float getSourceLight() {
        return 8;
    }

    @Override
    protected void collideWithNearbyEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(1.2));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);

                if(!MagickReleaseHelper.sameLikeOwner(this.getOwner(), entity) && !ModBuffs.hasBuff(entity, LibBuff.FREEZE))
                    this.push(entity);
            }
        }
    }

    @Override
    public void push(Entity entityIn) {
                double d0 = entityIn.getX() - this.getX();
                double d1 = entityIn.getZ() - this.getZ();
                double d2 = MathHelper.absMax(d0, d1);
                if (d2 >= (double)0.01F) {
                    d2 = (double)MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * (double)0.05F;
                    d1 = d1 * (double)0.05F;
                    d0 = d0 * (double)(1.0F - 1.5);
                    d1 = d1 * (double)(1.0F - 1.5);
                    if (!entityIn.isVehicle()) {
                        entityIn.push(d0, 0.0D, d1);
                    }
                }
    }

    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.level, this.spellContext().element.getRenderer().getMistTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getY() + this.getBbHeight() / 2
                , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getZ())
                , MagickCore.getRandFloat() * this.getBbWidth()
                , MagickCore.getRandFloat() * this.getBbWidth()
                , 0.5f * MagickCore.getRandFloat(), this.spellContext().element.getRenderer().getParticleRenderTick() / 2, this.spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15.0f);
        litPar.setCanCollide(false);
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2);
        MagickCore.addMagickParticle(litPar);

        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 2; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getZ())
                    , scale, scale, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox(), predicate);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.POINT_DEFAULT;
    }
}
