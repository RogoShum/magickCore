package com.rogoshum.magickcore.common.init;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.entity.living.QuadrantCrystalEntity;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.LootUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

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

            if(context.hasParam(LootContextParams.THIS_ENTITY)) {
                Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
                if(entity instanceof QuadrantCrystalEntity quadrant) {
                    int count = Math.max(1, (int)(quadrant.spellContext().force()));
                    ItemStack quadrantFrag = new ItemStack(ModItems.QUADRANT_FRAGMENTS.get());
                    quadrantFrag.setCount(count);
                    NBTTagHelper.setElement(quadrantFrag, quadrant.spellContext().element().type());
                    generatedLoot.add(quadrantFrag);
                }
            }


            if(context.hasParam(LootContextParams.KILLER_ENTITY)) {
                Entity entity = context.getParam(LootContextParams.KILLER_ENTITY);
                ElementToolData tool = ExtraDataUtil.elementToolData(entity);
                for(ItemStack slot : entity.getAllSlots()) {
                    if(NBTTagHelper.hasElementOnTool(slot, LibElements.SOLAR) && tool != null) {
                        for(int i = 0; i < generatedLoot.size(); ++i) {
                            ItemStack stack = generatedLoot.get(i);
                            Optional<SmeltingRecipe> optional = entity.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), entity.level);

                            if(optional.isPresent()) {
                                ItemStack result = optional.get().getResultItem().copy();
                                result.setCount(stack.getCount());
                                generatedLoot.set(i, result);
                            }
                        }
                        NBTTagHelper.consumeElementOnTool(slot, LibElements.SOLAR);
                        break;
                    }
                }
            }

            return generatedLoot;
        }
    }
}
