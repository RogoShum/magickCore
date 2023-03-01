package com.rogoshum.magickcore.common.init;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.MaterialJarItemRenderer;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.item.EntityRendererBlockItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.util.LootUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;

public class ModLoots {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOTS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MagickCore.MOD_ID);
    public static final RegistryObject<GlobalLootModifierSerializer<?>> RANDOM_LOOTS = LOOTS.register("random_loots", Serializer::new);
    public static class Serializer extends GlobalLootModifierSerializer<RandomLootModifier>{

        @Override
        public RandomLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            return new RandomLootModifier(location, ailootcondition);
        }

        @Override
        public JsonObject write(RandomLootModifier instance) {
            return null;
        }
    }

    public static class RandomLootModifier implements IGlobalLootModifier {
        public RandomLootModifier(ResourceLocation location, LootItemCondition[] ailootcondition) {
        }

        @Nonnull
        @Override
        public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context) {
           if(context.getQueriedLootTableId().toString().contains("chests")) {
               while (context.getRandom().nextBoolean()) {
                   int lucky = 3;
                   while (context.getRandom().nextBoolean())
                       lucky++;

                   generatedLoot.add(LootUtil.createRandomItemByLucky(lucky));
               }
           }
            return generatedLoot;
        }
    }
}
