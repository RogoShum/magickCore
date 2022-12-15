package com.rogoshum.magickcore.common.magick.condition;

import com.rogoshum.magickcore.common.lib.LibConditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector2f;

import java.util.Objects;
import java.util.function.Function;

public class HealthCondition extends EntityCondition{
    private float value;
    private Compare compare = Compare.LESS;
    private boolean percentage;

    public HealthCondition() {}

    private HealthCondition(float value) {
        this.value = value;
    }

    public static HealthCondition create(float value) {
        return new HealthCondition(value);
    }

    public HealthCondition value(float value) {
        this.value = value;
        return this;
    }

    public HealthCondition compare(Compare compare) {
        this.compare = compare;
        return this;
    }

    public HealthCondition percentage(boolean percentage) {
        this.percentage = percentage;
        return this;
    }

    @Override
    public String getName() {
        return LibConditions.HEALTH;
    }

    @Override
    public boolean test(Entity entity) {
        if(!(entity instanceof LivingEntity)) return false;
        float health = ((LivingEntity) entity).getHealth();
        if(percentage)
            health = health / ((LivingEntity) entity).getMaxHealth();
        return compare.compare(health, value);
    }

    @Override
    protected void serialize(CompoundNBT tag) {
        tag.putFloat("value", value);
        tag.putBoolean("percentage", percentage);
        tag.putString("compare", compare.name());
    }

    @Override
    protected void deserialize(CompoundNBT tag) {
        value = tag.getFloat("value");
        percentage = tag.getBoolean("percentage");
        try {
            compare = Compare.valueOf(tag.getString("compare"));
        }catch (Exception ignored) {}
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HealthCondition)) return false;
        HealthCondition that = (HealthCondition) o;
        return Float.compare(that.value, value) == 0 && percentage == that.percentage && compare == that.compare;
    }

    @Override
    public int hashCode() {
        return Objects.hash("health", value, compare, percentage);
    }

    public enum Compare {
        LESS((vector2f -> vector2f.x < vector2f.y)),
        EQUAL((vector2f -> vector2f.x == vector2f.y)),
        MORE((vector2f -> vector2f.x > vector2f.y)),
        LESS_EQUAL((vector2f -> vector2f.x <= vector2f.y)),
        MORE_EQUAL((vector2f -> vector2f.x >= vector2f.y));

        private final Function<Vector2f, Boolean> compare;

        Compare(Function<Vector2f, Boolean> compare) {
            this.compare = compare;
        }

        public boolean compare(float self, float other) {
            return compare.apply(new Vector2f(self, other));
        }
    }
}
