package com.rogoshum.magickcore.common.integration.botania;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.integration.jei.RecipeCollector;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.item.material.ElementItem;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.ability.SolarAbility;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.registry.elementmap.ElementFunctions;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ManaDiscountEvent;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.entity.EntityPixie;

import java.util.HashMap;
import java.util.List;

public class BotaniaLoader extends AdditionLoader {
    public static final RegistryObject<Item> BOTANIA = ModItems.ITEMS.register(LibItem.BOTANIA, () -> new ElementItem(LibElements.BOTANIA));
    @Override
    public void onLoad(IEventBus eventBus) {
        HashMap<String, ElementFunctions> registry = MagickRegistry.<ElementFunctions>getRegistry(LibRegistry.ELEMENT_FUNCTION).registry();
        ElementFunctions functions = ElementFunctions.create();
        functions.add(ApplyType.ATTACK, BotaniaAbility::damageEntity);
        functions.add(ApplyType.BUFF, BotaniaAbility::applyBuff);
        functions.add(ApplyType.HIT_ENTITY, BotaniaAbility::applyDebuff);
        functions.add(ApplyType.DE_BUFF, BotaniaAbility::applyDebuff);
        functions.add(ApplyType.HIT_BLOCK, BotaniaAbility::hitBlock);
        functions.add(ApplyType.DIFFUSION, BotaniaAbility::diffusion);
        functions.add(ApplyType.AGGLOMERATE, BotaniaAbility::agglomerate);
        registry.put(LibElements.BOTANIA, functions);
        HashMap<String, MagickElement> elements = MagickRegistry.<MagickElement>getRegistry(LibRegistry.ELEMENT).registry();
        elements.put(LibElements.BOTANIA, ModElements.BOTANIA);
        ModElements.elements.add(LibElements.BOTANIA);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RecipeCollector.addElementCoreRecipe(LibElements.BOTANIA, new ItemStack(BOTANIA.get())));
    }

    @SubscribeEvent
    public void onManaDiscountEvent(ManaDiscountEvent event) {
        if(event.getEntityPlayer().level.isClientSide()) return;
        float discount = 0.0f;
        for(ItemStack stack : event.getEntityPlayer().getArmorSlots()) {
            if(NBTTagHelper.hasElementOnTool(stack, LibElements.BOTANIA)) {
                discount += 0.1f;
                NBTTagHelper.consumeElementOnTool(stack, LibElements.BOTANIA);
            }
        }

        HashMap<String, ManaBuff> buffHashMap = ExtraDataUtil.entityStateData(event.getEntityPlayer()).getBuffList();
        if(buffHashMap.containsKey(LibBuff.THORNS)) {
            ManaBuff buff = buffHashMap.get(LibBuff.THORNS);
            discount -= buff.getForce() * 0.5f;
        }
        event.setDiscount(event.getDiscount() + discount);
    }

    @SubscribeEvent
    public void onMobSpawnEvent(EntityJoinWorldEvent event) {
        if(event.getEntity().level.isClientSide()) return;
        if(!(event.getEntity() instanceof Mob) || event.getEntity() instanceof EntityDoppleganger || event.getEntity() instanceof EntityPixie) return;
        if(event.getEntity().level.getEntitiesOfClass(EntityDoppleganger.class, event.getEntity().getBoundingBox().inflate(3)).isEmpty()) return;
        if(event.getEntity().level.random.nextFloat() > 0.4) return;
        EntityStateData data = ExtraDataUtil.entityStateData(event.getEntity());
        if(event.getEntity().level.random.nextFloat() > 0.3)
            data.setFinalMaxElementShield(((Mob) event.getEntity()).getMaxHealth());
        data.setElement(ModElements.BOTANIA);
        data.setElemented();
    }

    @SubscribeEvent
    public void onMobSpawnEvent(EntityEvents.EntityUpdateEvent event) {
        if(event.getEntity().level.isClientSide()) return;
        if(event.getEntity() instanceof EntityManaBurst entity) {
            AABB axis = new AABB(entity.getX(), entity.getY(), entity.getZ(), entity.xOld, entity.yOld, entity.zOld).inflate(1);
            List<Mob> entities = entity.level.getEntitiesOfClass(Mob.class, axis);

            for (Mob living : entities) {
                EntityStateData data = ExtraDataUtil.entityStateData(living);
                if(data.getElement() == ModElements.BOTANIA)
                    data.setFinalMaxElementShield(data.getFinalMaxElementShield()+5);

                if(data.getElement() == ModElements.ORIGIN)
                    data.setElement(ModElements.BOTANIA);
            }
        }
    }
}
