package com.rogoshum.magickcore.common.entity.radiation;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.radiation.SphereRadiateRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SphereEntity extends ManaRadiateEntity {
    public static final ManaFactor MANA_FACTOR = ManaFactor.create(0.2f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/sphere.png");
    public final Predicate<Entity> inSphere = (entity ->
            this.distanceToSqr(entity.position().add(0, entity.getBbHeight() * 0.5, 0))
                    <= getRange() * getRange());
    public SphereEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public float getRange() {
        return spellContext().range() * 1.5f;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new SphereRadiateRenderer(this);
    }

    @Override
    protected void applyParticle() {
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(getRange()),
                predicate != null ? predicate.and(inSphere)
                        : inSphere);
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
    public void tick() {
        super.tick();
    }

    protected void applyParticle(int particleAge) {
        Vec3[] vec3s = ParticleUtil.drawSphere((int) (3*spellContext().range()), (int) (3*spellContext().range()));
        for (Vec3 pos : vec3s) {
            LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element().type()).getParticleTexture()
                    , pos.scale(getRange()).add(this.position())
                    , 0.2f, 0.2f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element().type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            par.addMotion(pos.x * 0.2, pos.y * 0.2, pos.z * 0.2);
            MagickCore.addMagickParticle(par);
        }
        /*
        float radius = getRange();
        float rho, drho, theta, dtheta;
        float x, y, z;
        int stacks = Math.max((int) (2 * getRange()), 8);
        drho = (float) (2.0f * Math.PI / stacks);
        dtheta = (float) (2.0f * Math.PI / stacks);
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            for (int j = 0; j < stacks; j++) {
                theta = j * dtheta;
                x = (float) (-Math.sin(theta) * Math.sin(rho));
                y = (float) (Math.cos(theta) * Math.sin(rho));
                z = (float) Math.cos(rho);

                Vec3 pos = new Vec3(x * radius, y * radius, z * radius);
                LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                        , pos.add(this.position())
                        , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(x * 0.2, y * 0.2, z * 0.2);
                MagickCore.addMagickParticle(par);
            }
        }
         */
    }

    @Override
    public Iterable<BlockPos> findBlocks() {
        int range = (int) (getRange());
        return BlockPos.betweenClosed(new BlockPos(this.position()).above(range).east(range).south(range), new BlockPos(this.position()).below(range).west(range).north(range));
    }

    @Override
    public Predicate<BlockPos> blockPosPredicate() {
        float rangeCube = getRange() * getRange();
        return (pos -> this.distanceToSqr( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                <= rangeCube);
    }
}
