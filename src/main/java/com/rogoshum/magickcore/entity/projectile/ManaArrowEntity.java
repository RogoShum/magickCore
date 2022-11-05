package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.tool.ParticleHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ManaArrowEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/arrow.png");
    public ManaArrowEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
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
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected float getGravityVelocity() {
        float force = spellContext().force;
        if(force > 10)
            force = 10;

        return 0.01f - force * 0.001f;
    }

    @Override
    protected void applyParticle() {

    }

    public void addParticle(Vector3d pos, Vector3d direction, int count, float baseScale, float scale) {
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getLaserTexture()
                    , new Vector3d(pos.x - i * direction.x
                    , pos.y - i * direction.y + this.getHeight() / 2
                    , pos.z - i * direction.z)
                    , (float) (baseScale * Math.pow(scale, i)), (float) (baseScale * Math.pow(scale, i)), 1.0f, 1, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public void renderFrame(float partialTicks) {
        //float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        int count = (int)Math.max(getWidth() * 7, 10);
        int tailCount = Math.max(count / 2, 5);
        double space = Math.max(getWidth() * 0.07f, 0.03f);
        float baseScale = Math.max(getWidth() * 0.09f, 0.05f);
        float scale = 0.9f;
        Vector3d direction = this.getMotion().normalize();
        double x = MathHelper.lerp(partialTicks, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partialTicks, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partialTicks, this.lastTickPosZ, this.getPosZ());
        x = x + direction.x * this.getWidth() * 0.5;
        y = y + direction.y * this.getWidth() * 0.5;
        z = z + direction.z * this.getWidth() * 0.5;
        direction = direction.scale(space);
        Vector3d origin = new Vector3d(x, y, z);
        Vector3d tail = origin.subtract(direction.scale(count));

        addParticle(origin, direction, count, baseScale, scale);

        Vector2f dirPitchYaw = ParticleHelper.getRotationForVector(direction);
        Vector3d upPitch = ParticleHelper.getVectorForRotation(dirPitchYaw.x-30f, dirPitchYaw.y);
        Vector3d downPitch = ParticleHelper.getVectorForRotation(dirPitchYaw.x+30f, dirPitchYaw.y);
        Vector3d yaw = ParticleHelper.getVectorForRotation(0, dirPitchYaw.y+90f);

        Vector3d rotate = upPitch.add(yaw).normalize().scale(space);
        addParticle(origin, rotate, tailCount, baseScale * 0.8f, scale);
        addParticle(tail, rotate, (int) (tailCount * 0.5), baseScale * 0.6f, scale);

        rotate = upPitch.add(yaw.scale(-1)).normalize().scale(space);
        addParticle(origin, rotate, tailCount, baseScale * 0.8f, scale);
        addParticle(tail, rotate, (int) (tailCount * 0.5), baseScale * 0.6f, scale);

        rotate = downPitch.add(yaw).normalize().scale(space);
        addParticle(origin, rotate, tailCount, baseScale * 0.8f, scale);
        addParticle(tail, rotate, (int) (tailCount * 0.5), baseScale * 0.6f, scale);

        rotate = downPitch.add(yaw.scale(-1)).normalize().scale(space);
        addParticle(origin, rotate, tailCount, baseScale * 0.8f, scale);
        addParticle(tail, rotate, (int) (tailCount * 0.5), baseScale * 0.6f, scale);
    }
}
