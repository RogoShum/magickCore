package com.rogoshum.magickcore.common.entity.radiation;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.radiation.SectorRadiateRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
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

public class SectorEntity extends ManaRadiateEntity {
    public static final ManaFactor MANA_FACTOR = ManaFactor.create(0.3f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/sector.png");
    public final Predicate<Entity> inSector = (entity -> {
        Vec3 pos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        return this.distanceToSqr(pos) <= getRange() * getRange() && rightDirection(pos, (entity.getBbHeight() + entity.getBbWidth()) * 0.5f);
    });
    public SectorEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void applyParticle() {
        //applyParticle(2);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new SectorRadiateRenderer(this);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    public float getRange() {
        return spellContext().range * 1.5f;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(getRange()),
                predicate != null ? predicate.and(inSector)
                        : inSector);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    protected void applyParticle(int particleAge) {
        float range = getRange();
        Vec3 direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getCaster() != null) {
            direction = getCaster().getLookAngle().normalize();
        }
        if(direction == null) return;

        List<Vec3> vectors = ParticleUtil.drawSector(this.position(), direction.normalize().scale(range*2), 90, 15);
        for (Vec3 vector : vectors) {
            LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , vector
                    , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
            Vec3 dir = this.position().subtract(vector);
            par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , vector
                    , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            par.addMotion(dir.x * 0.1, dir.y * 0.1, dir.z * 0.1);
            MagickCore.addMagickParticle(par);
        }
    }

    public boolean rightDirection(Vec3 vec, float offset) {
        Vec3 direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getCaster() != null) {
            direction = getCaster().getLookAngle().normalize();
        }
        if(direction == null) return false;
        //boolean inCone = (this.getPositionVec().subtract(vec).normalize().dotProduct(direction) + 1) <= 0.2 * getRange();
        //if(!inCone) return false;

        float halfHeight = offset * 0.5f;
        Vec2 pitchYaw = ParticleUtil.getRotationForVector(direction);
        Vec3 normal = ParticleUtil.getVectorForRotation(pitchYaw.x + 90, pitchYaw.y);
        Vec3 top = vec.add(normal.scale(halfHeight));
        Vec3 bottom = vec.subtract(normal.scale(halfHeight));

        double a = normal.x, b = normal.y, c = normal.z;
        double d = - (a * position().x + b * position().y + c * position().z);

        double x0 = top.x, y0 = top.y, z0 = top.z;
        double t = - (a * x0 + b * y0 + c * z0 + d) / (a * a + b * b + c * c);

        double x = x0 + a * t;
        double y = y0 + b * t;
        double z = z0 + c * t;
        Vec3 foot = new Vec3(x, y, z);
        return top.subtract(foot).dot(bottom.subtract(foot)) <= 0;
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
                <= rangeCube && rightDirection(Vec3.atCenterOf(pos), 1));
    }
}
