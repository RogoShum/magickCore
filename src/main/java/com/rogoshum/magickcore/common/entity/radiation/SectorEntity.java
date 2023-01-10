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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
        Vector3d pos = entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0);
        return this.getDistanceSq(pos) <= getRange() * getRange() && rightDirection(pos, (entity.getHeight() + entity.getWidth()) * 0.5f);
    });
    public SectorEntity(EntityType<?> entityTypeIn, World worldIn) {
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
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(getRange()),
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
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookVec().normalize();
        }
        if(direction == null) return;

        List<Vector3d> vectors = ParticleUtil.drawSector(this.getPositionVec(), direction.normalize().scale(range*2), 90, 15);
        for (Vector3d vector : vectors) {
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , vector
                    , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
            Vector3d dir = this.getPositionVec().subtract(vector);
            par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , vector
                    , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            par.addMotion(dir.x * 0.1, dir.y * 0.1, dir.z * 0.1);
            MagickCore.addMagickParticle(par);
        }
    }

    public boolean rightDirection(Vector3d vec, float offset) {
        Vector3d direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookVec().normalize();
        }
        if(direction == null) return false;
        //boolean inCone = (this.getPositionVec().subtract(vec).normalize().dotProduct(direction) + 1) <= 0.2 * getRange();
        //if(!inCone) return false;

        float halfHeight = offset * 0.5f;
        Vector2f pitchYaw = ParticleUtil.getRotationForVector(direction);
        Vector3d normal = ParticleUtil.getVectorForRotation(pitchYaw.x + 90, pitchYaw.y);
        Vector3d top = vec.add(normal.scale(halfHeight));
        Vector3d bottom = vec.subtract(normal.scale(halfHeight));

        double a = normal.x, b = normal.y, c = normal.z;
        double d = - (a * getPositionVec().x + b * getPositionVec().y + c * getPositionVec().z);

        double x0 = top.x, y0 = top.y, z0 = top.z;
        double t = - (a * x0 + b * y0 + c * z0 + d) / (a * a + b * b + c * c);

        double x = x0 + a * t;
        double y = y0 + b * t;
        double z = z0 + c * t;
        Vector3d foot = new Vector3d(x, y, z);
        return top.subtract(foot).dotProduct(bottom.subtract(foot)) <= 0;
    }

    @Override
    public Iterable<BlockPos> findBlocks() {
        int range = (int) getRange();
        return BlockPos.getAllInBoxMutable(new BlockPos(this.getPositionVec()).up(range).east(range).south(range), new BlockPos(this.getPositionVec()).down(range).west(range).north(range));
    }

    @Override
    public Predicate<BlockPos> blockPosPredicate() {
        float rangeCube = getRange() * getRange();
        return (pos -> this.getDistanceSq( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                <= rangeCube && rightDirection(Vector3d.copyCentered(pos), 1));
    }
}
