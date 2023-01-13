package com.rogoshum.magickcore.common.entity.radiation;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.radiation.ConeRadiateRenderer;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConeEntity extends ManaRadiateEntity {
    public static final ManaFactor MANA_FACTOR = ManaFactor.create(0.2f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/cone.png");
    public final Predicate<Entity> inCone = (entity -> {
        Vector3d pos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        double range = getRange();
        return this.distanceToSqr(pos) <= range * range && rightDirection(pos);
    });

    public ConeEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ConeRadiateRenderer(this);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(getRange()),
                predicate != null ? predicate.and(inCone)
                        : inCone);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    public float getRange() {
        return spellContext().range * 1.5f;
    }

    protected void applyParticle(int particleAge) {
        float range = getRange();
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookAngle().normalize();
        }
        if(direction == null) return;
        ParticleUtil.spawnImpactParticle(level, this.position(), range, direction, spellContext().element, ParticleType.PARTICLE);
        /*
        for (int i = 1; i <= range; ++i) {
            Vector3d[] vectors = ParticleUtil.drawCone(this.getPositionVec(), direction.normalize().scale(getRange()), 4.5 * i, i * 2);
            for (Vector3d vector : vectors) {
                Vector3d dir = this.getPositionVec().subtract(vector);
                LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                        , vector
                        , 0.2f, 0.2f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(dir.x * 0.1, dir.y * 0.1, dir.z * 0.1);
                MagickCore.addMagickParticle(par);

                par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                        , vector
                        , 0.2f, 0.2f, 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(dir.x * 0.1, dir.y * 0.1, dir.z * 0.1);
                MagickCore.addMagickParticle(par);
            }
        }

         */
    }

    @Override
    protected void applyParticle() {
        //applyParticle(2);
    }

    public boolean rightDirection(Vector3d vec) {
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookAngle().normalize();
        }

        return direction != null && (this.position().subtract(vec).normalize().dot(direction) + 1) <= Math.toRadians(getRange() * 2.25);
    }

    @Override
    public Iterable<BlockPos> findBlocks() {
        int range = (int) getRange();
        return BlockPos.betweenClosed(new BlockPos(this.position()).above(range).east(range).south(range), new BlockPos(this.position()).below(range).west(range).north(range));
    }

    @Override
    public Predicate<BlockPos> blockPosPredicate() {
        float rangeCube = getRange() * getRange();
        return (pos -> this.distanceToSqr( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                <= rangeCube && rightDirection(Vector3d.atLowerCornerOf(pos)));
    }
}
