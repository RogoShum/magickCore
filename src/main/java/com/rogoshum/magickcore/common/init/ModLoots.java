package com.rogoshum.magickcore.common.init;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.util.LootUtil;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import com.rogoshum.magickcore.common.event.SubscribeEvent;


import java.util.List;
public class ModLoots {
    static {
        /*
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
            if (id.toString().contains("chests")) {
                // 我们的代码放这里
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .with(ItemEntry.builder(Items.EGG));

                table.pool(poolBuilder);
                while (context.getRandom().nextBoolean()) {
                    int lucky = 3;
                    while (context.getRandom().nextBoolean())
                        lucky++;

                    generatedLoot.add(LootUtil.createRandomItemByLucky(lucky));
                }
            }
        });

         */
    }
}
