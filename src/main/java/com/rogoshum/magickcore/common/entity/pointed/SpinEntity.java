package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.SpinRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SpinEntity extends ManaPointEntity {
    protected Vec3 hitPoint = this.position();
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/spin.png");
    public SpinEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.soft_buildup_high.get(), 0.5F, 1.0F + this.random.nextFloat());
        }
        if (this.tickCount % 2 == 0) {
            this.playSound(SoundEvents.CHAIN_HIT, 0.5F, 1.0F + this.random.nextFloat());
        }
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return level.getEntities(this, new AABB(getHitPoint(), getHitPoint()).inflate(getRange()), predicate);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new SpinRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
        float range = getRange();
        Vec3 vec = new Vec3(1, 0, 0);
        if(spellContext().containChild(LibContext.DIRECTION)) {
            vec = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
        }
        Vec2 rotation = ParticleUtil.getRotationForVector(vec);
        vec = ParticleUtil.getVectorForRotation(rotation.x, rotation.y + Math.max(14 - spellContext().range * 0.5f, 1f));
        spellContext().addChild(DirectionContext.create(vec));
        hitPoint =  position().add(vec.scale(range * 2));
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.create(Math.max(1 / (spellContext().range+1), 0.25f), 1.0f, 1.0f);
    }

    @Override
    protected void applyParticle() {
        float scale = 0.5f;
        double width = getRange() * 2;
        Vec3 hit = getHitPoint();
        List<Vec3> list = ParticleUtil.drawRectangle(hit, scale, width, width, width);
        for(int i = 0; i < list.size(); ++i) {
            Vec3 pos = list.get(i);
            LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , pos.add(MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f)
                    , 0.05f, 0.05f, 1.0f, 1, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public void reSize() {
    }

    public Vec3 getHitPoint() {
        return hitPoint;
    }

    public float getRange() {
        return spellContext().range * 0.25f;
    }

    @Override
    protected boolean fixedPosition() {
        return false;
    }

    @Override
    public MagickContext beforeCast(MagickContext context) {
        context.replenishChild(PositionContext.create(getHitPoint()));
        return context;
    }
}
