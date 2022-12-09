package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.SpinRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SpinEntity extends ManaPointEntity {
    protected Vector3d hitPoint = this.getPositionVec();
    public SpinEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(getHitPoint(), getHitPoint()).grow(getRange()), predicate);
    }

    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new SpinRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
        float range = getRange();
        Vector3d vec = new Vector3d(1, 0, 0);
        if(spellContext().containChild(LibContext.DIRECTION)) {
            vec = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
        }
        Vector2f rotation = ParticleUtil.getRotationForVector(vec);
        vec = ParticleUtil.getVectorForRotation(rotation.x, rotation.y + Math.max(7 - spellContext().range * 0.5f, 1f));
        spellContext().addChild(DirectionContext.create(vec));
        hitPoint =  getPositionVec().add(vec.scale(range * 2));
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.create(Math.max(1 / (spellContext().range+1), 0.25f), 1.0f, 1.0f);
    }

    @Override
    protected void applyParticle() {
        float scale = 0.5f;
        double width = getRange() * 2;
        Vector3d hit = getHitPoint();
        List<Vector3d> list = ParticleUtil.drawRectangle(hit, scale, width, width, width);
        for(int i = 0; i < list.size(); ++i) {
            Vector3d pos = list.get(i);
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
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

    public Vector3d getHitPoint() {
        return hitPoint;
    }

    public float getRange() {
        return spellContext().range * 0.25f;
    }

    @Override
    protected boolean fixedPosition() {
        return false;
    }
}
