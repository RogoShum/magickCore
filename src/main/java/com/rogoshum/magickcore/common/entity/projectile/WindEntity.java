package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.WindRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.child.ConditionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class WindEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.6f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/wind.png");
    public WindEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new WindRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
        //this.setNoGravity(true);
        this.noPhysics = true;
        List<Entity> entityList = this.level.getEntities(this, this.getBoundingBox().inflate(spellContext().range), (Entity::isAlive));
        for(int i = 0; i < entityList.size(); ++i) {
            Entity entity = entityList.get(i);
            if(!suitableEntity(entity)) continue;
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(spellContext().containChild(LibContext.CONDITION)) {
                ConditionContext context = spellContext().getChild(LibContext.CONDITION);
                if(!context.test(this, entity))
                    pass.set(false);
            }
            if(pass.get()) {
                Vec3 motion = this.position().add(0, getBbHeight() / 2, 0).subtract(entity.position().add(0, entity.getBbHeight() / 2, 0)).normalize();
                motion = motion.scale(0.08 * getBbWidth());
                entity.push(motion.x, motion.y, motion.z);
            }
        }
    }

    @Override
    protected float getGravity() {
        return 0.001f;
    }

    @Override
    public boolean hitBlockRemove(BlockHitResult blockRayTraceResult) {
        return false;
    }

    @Override
    public float getSourceLight() {
        return 6;
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.wind_fx.get(), 0.5f, 1.0F + this.random.nextFloat());
        }
        if(this.tickCount % 20 == 0)
            this.playSound(ModSounds.wind.get(), 0.5F, 1.0F + this.random.nextFloat());
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getFrameTime();
        double x = Mth.lerp(partial, this.xOld, this.getX());
        double y = Mth.lerp(partial, this.yOld, this.getY());
        double z = Mth.lerp(partial, this.zOld, this.getZ());
        float times = Math.max(getBbWidth(), 2) * 5;
        int age = Math.max((int) (2 * spellContext().range), 10);
        for (int i = 0; i < times; ++i) {
            LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(x , y + getBbHeight() / 2 , z)
                    , 0.1f, 0.1f, 1.0f, age, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setTraceTarget(this);
            MagickCore.addMagickParticle(par);
        }
    }
}
