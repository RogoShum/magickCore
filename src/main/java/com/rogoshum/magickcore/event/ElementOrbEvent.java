package com.rogoshum.magickcore.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.entity.ManaElementOrbEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEnchantments;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibEnchantment;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Dimension;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ElementOrbEvent {
    private static final HashMap<Class, LivingElementTable> spawnElementMap = new HashMap<Class, LivingElementTable>();
    private static final HashMap<String, LivingElementTable> spawnElementMap_dimension = new HashMap<String, LivingElementTable>();
    private static final List<EntityType<?>> element_animal = new ArrayList<>();

    public static void initElementMap()
    {
        spawnElementMap.put(EndermanEntity.class, new LivingElementTable(3, LibElements.VOID));
        spawnElementMap.put(BlazeEntity.class, new LivingElementTable(3, LibElements.SOLAR));
        spawnElementMap.put(CaveSpiderEntity.class, new LivingElementTable(2, LibElements.WITHER));
        spawnElementMap.put(ShulkerEntity.class, new LivingElementTable(3, LibElements.VOID));
        spawnElementMap.put(MagmaCubeEntity.class, new LivingElementTable(2, LibElements.SOLAR));

        spawnElementMap_dimension.put(Dimension.THE_END.getLocation().toString(), new LivingElementTable(7, LibElements.VOID));
        spawnElementMap_dimension.put(Dimension.THE_NETHER.getLocation().toString(), new LivingElementTable(10, LibElements.SOLAR));

        element_animal.add(EntityType.COW);
        element_animal.add(EntityType.SHEEP);
        element_animal.add(EntityType.CHICKEN);
        element_animal.add(EntityType.PIG);
    }

    public static boolean containAnimalType(EntityType<?> type)
    {
        return element_animal.contains(type);
    }

    public static boolean putLivingElementTable(String s, LivingElementTable table)
    {
        if(spawnElementMap_dimension.containsKey(s))
            return false;

        spawnElementMap_dimension.put(s, table);
        return true;
    }

    public static boolean putLivingElementTable(Class clazz, LivingElementTable table)
    {
        if(spawnElementMap.containsKey(clazz))
            return false;

        spawnElementMap.put(clazz, table);
        return true;
    }

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event)
    {
        IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
        if(state.getIsDeprived() && !state.getElement().getType().equals(LibElements.ORIGIN) && !event.getEntityLiving().world.isRemote)
        {
            ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntites.element_orb, event.getEntityLiving().world);
            Vector3d vec = event.getEntityLiving().getPositionVec();
            orb.setPosition(vec.x, vec.y + event.getEntityLiving().getHeight() / 2, vec.z);
            orb.setElement(state.getElement());
            orb.setTickTime(2000);
            orb.setShooter(event.getEntityLiving());
            event.getEntityLiving().world.addEntity(orb);
        }

        IElementAnimalState animalState = event.getEntityLiving().getCapability(MagickCore.elementAnimal).orElse(null);
        if(animalState != null && animalState.getElement().getType() != LibElements.ORIGIN)
        {
            Collection<ItemEntity> loots = event.getDrops();
            loots.stream().forEach((e) -> {
                if(e.getItem().isFood())
                {
                    int count = e.getItem().getCount();
                    ItemStack stack = new ItemStack(ModItems.element_meat.get());
                    CompoundNBT tag = new CompoundNBT();
                    tag.putString("ELEMENT", animalState.getElement().getType());
                    stack.setCount(count);
                    stack.setTag(tag);
                    e.setItem(stack);
                }
            });
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event)
    {
        if(event.getSource().getTrueSource() == event.getSource().getImmediateSource() && event.getSource().getTrueSource() instanceof LivingEntity)
        {
            ItemStack stack = ((LivingEntity) event.getSource().getTrueSource()).getHeldItemMainhand();
            if(EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ELEMENT_DEPRIVATION.get(), stack) > 0)
            {
                IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);
                state.setDeprived();
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        if(event.getTarget() instanceof ManaElementOrbEntity)
        {
            if (event.getPlayer().getHeldItemMainhand().getItem() == Items.GLASS_BOTTLE) {
                ItemStack stack = new ItemStack(ModItems.orb_bottle.get());
                ManaItem item = (ManaItem) stack.getItem();
                item.setElement(stack, ((ManaElementOrbEntity)event.getTarget()).getElement());
                event.getPlayer().setHeldItem(Hand.MAIN_HAND, stack);
                event.getTarget().remove();
            }
        }
    }

    @SubscribeEvent
    public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
    {
        IEntityState state = event.getEntityLiving().getCapability(MagickCore.entityState).orElse(null);

        if(!event.getEntityLiving().isNonBoss())
            state.setElemented();

        if(ElementOrbEvent.spawnElementMap.containsKey(event.getEntityLiving().getClass()))
        {

            LivingElementTable table = ElementOrbEvent.spawnElementMap.get(event.getEntityLiving().getClass());

            if(state.allowElement() && MagickCore.rand.nextInt(table.getChance()) == 0)
            {
                state.setFinalMaxElementShield(MagickCore.rand.nextInt(91) + 10);
                state.setElement(ModElements.getElement(table.element));
            }

            state.setElemented();
        }

        String dimension_name = event.getEntityLiving().world.getDimensionKey().getLocation().toString();
        if(ElementOrbEvent.spawnElementMap_dimension.containsKey(dimension_name))
        {
            LivingElementTable table = ElementOrbEvent.spawnElementMap_dimension.get(dimension_name);

            if(state.allowElement() && MagickCore.rand.nextInt(table.getChance()) == 0)
            {
                state.setFinalMaxElementShield(MagickCore.rand.nextInt(41) + 10);
                state.setElement(ModElements.getElement(table.element));
            }

            state.setElemented();
        }
    }

    public static class LivingElementTable{
        private final int chance;
        private final String element;
        protected LivingElementTable(int chance, String element)
        {
            this.chance = chance;
            this.element = element;
        }

        protected int getChance(){return this.chance;}
        protected String getType(){return this.element;}
    }
}
