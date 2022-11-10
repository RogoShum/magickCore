package com.rogoshum.magickcore.common.entity.radiated;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SectorEntity extends ManaRadiateEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/sector.png");
    public final Predicate<Entity> inSector = (entity -> {
        Vector3d pos = entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0);
        return this.getDistanceSq(pos) <= spellContext().range * spellContext().range && rightDirection(pos, entity);
    });
    public SectorEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void applyParticle() {
        applyParticle(2);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range),
                predicate != null ? predicate.and(inSector)
                        : inSector);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.RADIATE_DEFAULT;
    }

    protected void applyParticle(int particleAge) {
        float range = spellContext().range;
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookVec().normalize();
        }
        if(direction == null) return;

        List<Vector3d> vectors = ParticleUtil.drawSector(this.getPositionVec(), direction.normalize().scale(range), (double) (9 * range), (int) (range));
        for (Vector3d vector : vectors) {
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , vector
                    , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
            Vector3d dir = this.getPositionVec().subtract(vector);
            if(rand.nextFloat() > 0.8) {
                par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                        , vector
                        , 0.1f, 0.1f, 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(dir.x * 0.1, dir.y * 0.1, dir.z * 0.1);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    public boolean rightDirection(Vector3d vec, Entity entity) {
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookVec().normalize();
        }
        if(direction == null) return false;
        boolean inCone = (this.getPositionVec().subtract(vec).normalize().dotProduct(direction) + 1) <= 0.2 * spellContext().range;
        if(!inCone) return false;

        float halfHeight = entity.getHeight() * 0.5f;
        double top = vec.add(0, halfHeight, 0).subtract(this.getPositionVec()).normalize().y;
        double bottom = vec.subtract(0, halfHeight, 0).subtract(this.getPositionVec()).normalize().y;
        return top > direction.y && bottom < direction.y;
    }
}
