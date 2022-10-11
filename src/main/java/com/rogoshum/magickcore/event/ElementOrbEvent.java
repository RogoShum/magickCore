package com.rogoshum.magickcore.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.entity.pointed.ManaPowerEntity;
import com.rogoshum.magickcore.init.ModEnchantments;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Dimension;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ElementOrbEvent {
    private static final HashMap<Class, LivingElementTable> spawnElementMap = new HashMap<Class, LivingElementTable>();
    private static final HashMap<String, LivingElementTable> spawnElementMap_dimension = new HashMap<String, LivingElementTable>();
    private static final HashMap<String, LivingElementTable> spawnElementMap_biome = new HashMap<String, LivingElementTable>();
    private static final List<EntityType<?>> element_animal = new ArrayList<>();

    public static void initElementMap()
    {
        spawnElementMap.put(EndermanEntity.class, new LivingElementTable(4, LibElements.VOID));
        spawnElementMap.put(BlazeEntity.class, new LivingElementTable(3, LibElements.SOLAR));
        spawnElementMap.put(CaveSpiderEntity.class, new LivingElementTable(1, LibElements.WITHER));
        spawnElementMap.put(SpiderEntity.class, new LivingElementTable(7, LibElements.WITHER));
        spawnElementMap.put(ShulkerEntity.class, new LivingElementTable(6, LibElements.VOID));
        spawnElementMap.put(MagmaCubeEntity.class, new LivingElementTable(2, LibElements.SOLAR));
        spawnElementMap.put(EvokerEntity.class, new LivingElementTable(1, LibElements.TAKEN));
        spawnElementMap.put(VexEntity.class, new LivingElementTable(2, LibElements.TAKEN));

        spawnElementMap_dimension.put(Dimension.THE_END.getLocation().toString(), new LivingElementTable(5, LibElements.VOID));
        spawnElementMap_dimension.put(Dimension.THE_NETHER.getLocation().toString(), new LivingElementTable(7, LibElements.SOLAR));

        spawnElementMap_biome.put(Biome.Category.SWAMP.getString(), new LivingElementTable(4, LibElements.WITHER));
        spawnElementMap_biome.put(Biome.Category.ICY.getString(), new LivingElementTable(4, LibElements.STASIS));
        spawnElementMap_biome.put(Biome.Category.MESA.getString(), new LivingElementTable(6, LibElements.STASIS));
        spawnElementMap_biome.put(Biome.Category.EXTREME_HILLS.getString(), new LivingElementTable(5, LibElements.STASIS));

        element_animal.add(EntityType.COW);
        element_animal.add(EntityType.SHEEP);
        element_animal.add(EntityType.CHICKEN);
        element_animal.add(EntityType.PIG);
    }

    public static boolean containAnimalType(EntityType<?> type)
    {
        return element_animal.contains(type);
    }

    public static boolean putAnimalTypeElementTable(EntityType<?> type)
    {
        if(element_animal.contains(type))
            return false;

        element_animal.add(type);
        return true;
    }

    public static boolean putBiomeElementTable(String s, LivingElementTable table)
    {
        if(spawnElementMap_biome.containsKey(s))
            return false;

        spawnElementMap_biome.put(s, table);
        return true;
    }

    public static boolean putDimensionElementTable(String s, LivingElementTable table)
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
        ExtraDataHelper.entityStateData(event.getEntityLiving(), state -> {
            if(event.getEntityLiving() instanceof IMob && !event.getEntityLiving().world.isRemote)
            {
                if(!state.getElement().type().equals(LibElements.ORIGIN) && state.getIsDeprived())
                {
                    ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.element_orb.get(), event.getEntityLiving().world);
                    Vector3d vec = event.getEntityLiving().getPositionVec();
                    orb.setPosition(vec.x, vec.y + event.getEntityLiving().getHeight() / 2, vec.z);
                    orb.spellContext().element(state.getElement());
                    orb.spellContext().tick(200);
                    orb.setShooter(event.getEntityLiving());
                    event.getEntityLiving().world.addEntity(orb);
                }
                else
                {
                    ManaPowerEntity orb = new ManaPowerEntity(ModEntities.mana_power.get(), event.getEntityLiving().world);
                    Vector3d vec = event.getEntityLiving().getPositionVec();
                    orb.setPosition(vec.x, vec.y + event.getEntityLiving().getHeight() / 2, vec.z);
                    orb.spellContext().tick(100);
                    orb.setMana(event.getEntityLiving().getMaxHealth() / 2);
                    event.getEntityLiving().world.addEntity(orb);
                }
            }
        });


        ExtraDataHelper.entityStateData(event.getEntityLiving(), state -> {
            if (!state.getElement().type().equals(LibElements.ORIGIN) && !event.getEntityLiving().world.isRemote) {
                Collection<ItemEntity> loots = event.getDrops();
                loots.forEach((e) -> {
                    if (e.getItem().isFood()) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.element_meat.get());
                        CompoundNBT tag = new CompoundNBT();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }

                    if (e.getItem().getItem().getRegistryName().toString().contains("wool")) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.element_wool.get());
                        CompoundNBT tag = new CompoundNBT();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }
                });
            }
        });
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if(event.getSource().getTrueSource() == event.getSource().getImmediateSource() && event.getSource().getTrueSource() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) event.getSource().getTrueSource()).getHeldItemMainhand();
            if(EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ELEMENT_DEPRIVATION.get(), stack) > 0) {
                ExtraDataHelper.entityStateData(event.getEntityLiving(), EntityStateData::setDeprived);
            }
        }
    }

    public static float getShieldCapacity(LivingEntity livingEntity)
    {
        return MagickCore.rand.nextInt((int) livingEntity.getHealth()) + livingEntity.getHealth();
    }


    public boolean testIfGenerateShield(LivingEntity livingEntity) {
        for(PlayerEntity playerEntity : livingEntity.world.getPlayers()) {
            if(playerEntity.getHeldItemOffhand().getItem() instanceof EnderPearlItem &&
                    playerEntity.getEntityWorld() == livingEntity.getEntityWorld() &&
                    playerEntity.getDistanceSq(livingEntity) <= 4096)
                return true;
        }

        return false;
    }

    @SubscribeEvent
    public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
    {
        ExtraDataHelper.entityStateData(event.getEntityLiving(), state -> {
            if(!state.allowElement())
                return;
            if(!event.getEntityLiving().isNonBoss())
                state.setElemented();
            if(testIfGenerateShield(event.getEntityLiving()))
                state.setElemented();

            if(ElementOrbEvent.spawnElementMap.containsKey(event.getEntityLiving().getClass()))
            {
                LivingElementTable table = ElementOrbEvent.spawnElementMap.get(event.getEntityLiving().getClass());

                if(state.allowElement() && MagickCore.rand.nextInt(table.getChance()) == 0)
                {
                    state.setFinalMaxElementShield(getShieldCapacity(event.getEntityLiving()));
                    state.setElement(MagickRegistry.getElement(table.element));
                    state.setMaxManaValue(state.getFinalMaxElementShield());
                }

                state.setElemented();
            }

            String dimension_name = event.getEntityLiving().world.getDimensionKey().getLocation().toString();
            if(event.getEntityLiving() instanceof IMob && ElementOrbEvent.spawnElementMap_dimension.containsKey(dimension_name))
            {
                LivingElementTable table = ElementOrbEvent.spawnElementMap_dimension.get(dimension_name);

                if(state.allowElement() && MagickCore.rand.nextInt(table.getChance()) == 0)
                {
                    state.setFinalMaxElementShield(getShieldCapacity(event.getEntityLiving()));
                    state.setElement(MagickRegistry.getElement(table.element));
                    state.setMaxManaValue(state.getFinalMaxElementShield());
                }

                state.setElemented();
            }

            String biome_type = event.getEntityLiving().world.getBiome(event.getEntityLiving().getPosition()).getCategory().getString();
            if(event.getEntityLiving() instanceof IMob && ElementOrbEvent.spawnElementMap_biome.containsKey(biome_type))
            {
                LivingElementTable table = ElementOrbEvent.spawnElementMap_biome.get(biome_type);

                if(state.allowElement() && MagickCore.rand.nextInt(table.getChance()) == 0)
                {
                    state.setFinalMaxElementShield(getShieldCapacity(event.getEntityLiving()));
                    state.setElement(MagickRegistry.getElement(table.element));
                    state.setMaxManaValue(state.getFinalMaxElementShield());
                }

                state.setElemented();
            }
        });

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
