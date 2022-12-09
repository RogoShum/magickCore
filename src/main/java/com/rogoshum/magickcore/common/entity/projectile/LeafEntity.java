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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class LeafEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_1.png");
    private static final DataParameter<Integer> NUMBER = EntityDataManager.createKey(LeafEntity.class, DataSerializers.VARINT);
    public LeafEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.dataManager.register(NUMBER, rand.nextInt(3));
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new LeafRenderer(this);
    }

    public void setNumber(int number) {
        this.getDataManager().set(NUMBER, number);
    }

    public int getNumber() {
        return this.getDataManager().get(NUMBER);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getRenderPartialTicks();
        double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
        float scale = this.getWidth() * 0.1f;
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + x
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + y + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + z)
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
        int light = this.world.getLight(EntityLightSourceManager.entityPos(this));
        return ManaFactor.create(0.9f + (light / 15f), 1.0f, 1.0f + (light / 15f));
    }
}
