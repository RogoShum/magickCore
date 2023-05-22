package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.ManaSphereRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibBuff;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ManaSphereEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_sphere.png");

    public ManaSphereEntity(EntityType<?> entityTypeIn, Level worldIn) {
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
    }

    @Override
    public void reSize() {
        float height = getType().getHeight() + spellContext().range() * 0.3f;
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range() * 0.3f;
        if(getBbWidth() != width)
            this.setWidth(width);
    }

    @Override
    protected void makeSound() {
        if(!this.level.isClientSide && this.tickCount == 1)
        {
            this.playSound(ModSounds.sphere_spawn.get(), 1.0F, 1.0F - this.random.nextFloat() / 5);
        }

        if(!this.level.isClientSide && this.tickCount == 10)
            this.playSound(ModSounds.sphere_ambience.get(), 1.0F, (0.85F - this.random.nextFloat() / 5));

        if(!this.level.isClientSide && this.tickCount % 15 == 0 && this.tickCount < this.spellContext().tick() - 10 && this.tickCount > 10)
        {
            this.playSound(ModSounds.sphere_ambience.get(), 1.0F, (0.85F - this.random.nextFloat() / 5));
        }

        if(!this.level.isClientSide && this.tickCount == this.spellContext().tick() - 20)
        {
            this.playSound(ModSounds.shpere_dissipate.get(), 0.5F, (1.0F - this.random.nextFloat()));
        }
    }

    @Override
    public float getSourceLight() {
        return 10;
    }

    @Override
    protected void collideWithNearbyEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox().inflate(1.4));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);

                if(!MagickReleaseHelper.sameLikeOwner(this.getCaster(), entity) && !ModBuffs.hasBuff(entity, LibBuff.FREEZE))
                    this.push(entity);
            }
        }
    }

    @Override
    public void push(Entity entityIn) {
        Vec3 me = this.position().add(0, this.getBbHeight() * 0.5, 0);
        Vec3 it = entityIn.position().add(0, entityIn.getBbHeight() * 0.5, 0);
        if (!entityIn.isVehicle()) {
            Vec3 force = me.subtract(it).scale(0.01);
            entityIn.push(force.x, force.y, force.z);
        }
    }

    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.level, this.spellContext().element().getRenderer().getMistTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getY() + this.getBbHeight() / 2
                , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getZ())
                , MagickCore.getRandFloat() * this.getBbWidth()
                , MagickCore.getRandFloat() * this.getBbWidth()
                , 0.5f * MagickCore.getRandFloat(), this.spellContext().element().getRenderer().getParticleRenderTick() / 2, this.spellContext().element().getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(5.0f);
        litPar.setCanCollide(false);
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.2);
        MagickCore.addMagickParticle(litPar);

        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.3f;
        for (int i = 0; i < 2; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getZ())
                    , scale, scale, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element().type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setShakeLimit(5f);
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
