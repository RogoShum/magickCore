package com.rogoshum.magickcore.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class StringTrigger extends AbstractCriterionTrigger<StringTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("magickcore:string");

    public ResourceLocation getId() {
        return ID;
    }

    public StringTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new StringTrigger.Instance(entityPredicate, json.get("string").getAsString());
    }

    public void trigger(ServerPlayerEntity player, String string) {
        this.triggerListeners(player, (instance) -> {
            return instance.test(string);
        });
    }

    public static class Instance extends CriterionInstance {
        private final String string;

        public Instance(EntityPredicate.AndPredicate player, String string) {
            super(StringTrigger.ID, player);
            this.string = string;
        }

        public static StringTrigger.Instance forString(String string) {
            return new StringTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, string);
        }

        public boolean test(String string) {
            return this.string.equals(string);
        }

        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.addProperty("string", string);
            return jsonobject;
        }
    }
}
