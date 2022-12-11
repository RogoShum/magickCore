package com.rogoshum.magickcore.common.init;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.util.LootUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModLoots {
    public static final GlobalLootModifierSerializer<?> RandomLoots = new Serializer().setRegistryName("random_loots");

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                RandomLoots
        );
    }

    public static class Serializer extends GlobalLootModifierSerializer<RandomLootModifier>{

        @Override
        public RandomLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            return new RandomLootModifier(location, ailootcondition);
        }

        @Override
        public JsonObject write(RandomLootModifier instance) {
            return null;
        }
    }

    public static class RandomLootModifier implements IGlobalLootModifier {
        public RandomLootModifier(ResourceLocation location, ILootCondition[] ailootcondition) {
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
