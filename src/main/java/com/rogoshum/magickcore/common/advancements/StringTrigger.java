package com.rogoshum.magickcore.common.advancements;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;

public class StringTrigger extends SimpleCriterionTrigger<StringTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation("magickcore:string");

    public ResourceLocation getId() {
        return ID;
    }

    public StringTrigger.Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new StringTrigger.Instance(entityPredicate, json.get("string").getAsString());
    }

    public void trigger(ServerPlayer player, String string) {
        this.trigger(player, (instance) -> {
            return instance.test(string);
        });
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final String string;

        public Instance(EntityPredicate.Composite player, String string) {
            super(StringTrigger.ID, player);
            this.string = string;
        }

        public static StringTrigger.Instance forString(String string) {
            return new StringTrigger.Instance(EntityPredicate.Composite.ANY, string);
        }

        public boolean test(String string) {
            return this.string.equals(string);
        }

        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.addProperty("string", string);
            return jsonobject;
        }
    }
}
