package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.JewelryBagRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.LeafRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class LeafEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_1.png");
    private static final EntityDataAccessor<Integer> NUMBER = SynchedEntityData.defineId(LeafEntity.class, EntityDataSerializers.INT);
    public LeafEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
        this.entityData.define(NUMBER, random.nextInt(3));
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new LeafRenderer(this);
    }

    public void setNumber(int number) {
        this.getEntityData().set(NUMBER, number);
    }

    public int getNumber() {
        return this.getEntityData().get(NUMBER);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float getSourceLight() {
        return 5;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getFrameTime();
        double x = Mth.lerp(partial, this.xOld, this.getX());
        double y = Mth.lerp(partial, this.yOld, this.getY());
        double z = Mth.lerp(partial, this.zOld, this.getZ());
        float scale = this.getBbWidth() * 0.1f;
        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + x
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + y + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + z)
                , scale, scale, 1f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setParticleGravity(0f);
        par.setGlow();
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        int light = this.level.getMaxLocalRawBrightness(EntityLightSourceManager.entityPos(this));
        return ManaFactor.create(0.9f + (light / 15f), 1.0f, 1.0f + (light / 15f));
    }
}
