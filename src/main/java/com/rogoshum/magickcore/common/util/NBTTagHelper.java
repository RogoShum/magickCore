package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.common.init.CommonConfig;
import com.rogoshum.magickcore.common.item.AssemblyEssenceItem;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

public class NBTTagHelper {
    private static final String TAG_ITEM_DAMAGE = "Damege";
    private static final String TAG_ITEM_COUNT = "Count";
    private static final String TAG_ITEM_TAG = "tag";
    private static final String TAG_ITEM_ID = "id";
    private final CompoundTag tag;

    public NBTTagHelper(CompoundTag tag) {
        this.tag = tag;
    }

    public static CompoundTag getStackTag(ItemStack stack) {
        if(!stack.hasTag())
            stack.setTag(new CompoundTag());
        return stack.getTag();
    }

    public static Entity createEntityByItem(ItemStack stack, Level world) {
        if(!stack.hasTag() || !stack.getTag().contains("Magick_Store_Entity")) return null;
        CompoundTag tag = stack.getTag().getCompound("Magick_Store_Entity");
        Optional<EntityType<?>> type = EntityType.by(tag);
        if(type.isPresent()) {
            Entity entity = type.get().create(world);
            tag.remove("UUID");
            entity.load(tag);
            return entity;
        } else
            return null;
    }

    public static HashSet<String> getNBTKeySet(CompoundTag tag) {
        HashSet<String> keys = new HashSet<>();
        for (String key : tag.getAllKeys()) {
            keys.add(key);
            Tag inbt = tag.get(key);
            if(inbt instanceof CompoundTag)
                keys.addAll(getNBTKeySet((CompoundTag) inbt));
        }
        return keys;
    }

    public static HashMap<String, Tag> getNBTKeyMap(CompoundTag tag) {
        HashMap<String, Tag> keys = new HashMap<>();
        for (String key : tag.getAllKeys()) {
            Tag inbt = tag.get(key);
            if(inbt instanceof CompoundTag)
                keys.putAll(getNBTKeyMap((CompoundTag) inbt));
            else {
                if(inbt instanceof StringTag) {
                    StringTag string = (StringTag) inbt;
                    if(!string.getAsString().isEmpty())
                        keys.put(key, tag.get(key));
                } else
                    keys.put(key, tag.get(key));
            }
        }
        return keys;
    }

    public static ItemStack createItemWithEntity(Entity entity, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        storeEntityToItem(entity, stack);
        return stack;
    }

    public static void storeEntityToItem(Entity entity, ItemStack item) {
        CompoundTag tag = new CompoundTag();
        entity.saveAsPassenger(tag);
        item.getOrCreateTag().put("Magick_Store_Entity", tag);
    }

    public void ifContain(String s, Runnable runnable) {
        if(tag.contains(s))
            runnable.run();
    }

    public void ifContainString(String s, Consumer<String> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getString(s));
    }

    public void ifContainInt(String s, Consumer<Integer> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getInt(s));
    }

    public void ifContainFloat(String s, Consumer<Float> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getFloat(s));
    }

    public void ifContainNBT(String s, Consumer<CompoundTag> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getCompound(s));
    }

    public void ifContainDouble(String s, Consumer<Double> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getDouble(s));
    }

    public void ifContainBoolean(String s, Consumer<Boolean> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getBoolean(s));
    }

    public void ifContainUUID(String s, Consumer<UUID> consumer) {
        if(tag.contains(s))
            consumer.accept(tag.getUUID(s));
    }

    public static CompoundTag getBlockTag(CompoundTag tag) {
        return tag.getCompound("BlockEntityTag");
    }

    public static CompoundTag getEntityTag(Entity entity) {
        CompoundTag nbt = new CompoundTag();
        entity.saveAsPassenger(nbt);
        return nbt;
    }

    public static boolean hasElement(ItemStack stack) {
        return getStackTag(stack).contains("ELEMENT");
    }

    public static ItemStack setElement(ItemStack stack, String element) {
        if(!stack.isEmpty()) {
            CompoundTag tag = getStackTag(stack);
            tag.putString("ELEMENT", element);
            stack.setTag(tag);
        }

        return stack;
    }

    public static String getElement(ItemStack stack) {
        if(!stack.isEmpty() && stack.hasTag() && hasElement(stack))
            return getStackTag(stack).getString("ELEMENT");

        return LibElements.ORIGIN;
    }

    public static int getElementOnToolCount(Entity entity, String element) {
        int count = 0;
        for(ItemStack stack : entity.getAllSlots()) {
            if(hasElementOnTool(stack, element))
                count++;
        }
        return count;
    }

    public static boolean hasElementOnTool(ItemStack stack, String element) {
        try {
            if (!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT) && getToolElementTable(stack).contains(element))
                return true;
        }
        catch (Exception exception) {
            MagickCore.LOGGER.info(stack);
            MagickCore.LOGGER.info(element);
            exception.printStackTrace();
        }
        return false;
    }

    public static int getAssemblyCount(Entity entity, String element) {
        int count = 0;
        for(ItemStack stack : entity.getAllSlots()) {
            if(hasAssemblyOnItem(stack, element))
                count++;
        }
        return count;
    }

    public static boolean hasAssemblyOnItem(ItemStack stack, String element) {
        try {
            if(!stack.isEmpty() && stack.hasTag()) {
                ItemDimensionData data = ExtraDataUtil.itemDimensionData(stack);
                for (ItemStack slot : data.getSlots()) {
                    if(slot.getItem() instanceof AssemblyEssenceItem && getElement(slot).equals(element))
                        return true;
                }
            }
        } catch (Exception exception) {
            MagickCore.LOGGER.info(stack);
            MagickCore.LOGGER.info(element);
            exception.printStackTrace();
        }
        return false;
    }

    public static boolean consumeElementOnTool(ItemStack stack, String element) {
        if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT) && hasElementOnTool(stack, element)) {
            CompoundTag tag = getToolElementTable(stack);
            int count = tag.getInt(element);
            if(count > 1)
                tag.putInt(element, count - 1);
            else
                tag.remove(element);
            return true;
        }
        return false;
    }

    public static boolean consumeElementOnTool(ItemStack stack, String element, int count) {
        if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT) && hasElementOnTool(stack, element)) {
            CompoundTag tag = getToolElementTable(stack);
            int durability = tag.getInt(element);
            if(durability > count)
                tag.putInt(element, durability - count);
            else
                tag.remove(element);
            return true;
        }
        return false;
    }

    public static void putElementOnTool(ItemStack stack, String element) {
        CompoundTag tag = getToolElementTable(stack);
        tag.putInt(element, CommonConfig.ELEMENT_STRING_DURATION.get());
        CompoundTag nbt = getStackTag(stack);
        nbt.put(LibElementTool.TOOL_ELEMENT, tag);
        stack.setTag(nbt);
    }

    public static void putElementOnTool(ItemStack stack, String element, int durability) {
        CompoundTag tag = getToolElementTable(stack);
        tag.putInt(element, durability);
        CompoundTag nbt = getStackTag(stack);
        nbt.put(LibElementTool.TOOL_ELEMENT, tag);
        stack.setTag(nbt);
    }

    public static CompoundTag getToolElementTable(ItemStack stack) {
        CompoundTag tag = getStackTag(stack);
        if(!tag.contains(LibElementTool.TOOL_ELEMENT))
            tag.put(LibElementTool.TOOL_ELEMENT, new CompoundTag());

        return tag.getCompound(LibElementTool.TOOL_ELEMENT);
    }

    public static void saveVectorSet(CompoundTag tag, Set<Vec3> set) {
        for(Vec3 vec : set) {
            CompoundTag vectorTag = new CompoundTag();
            putVectorDouble(vectorTag, "", vec);
            tag.put(vec.x+"_"+ vec.y+"_"+ vec.z, vectorTag);
        }
    }

    public static void addOrDeleteVector(CompoundTag tag, Vec3 vec) {
        addOrDeleteVector(tag, vec, false);
    }

    public static void addOrDeleteVector(CompoundTag tag, Vec3 vec, boolean deleteOnly) {
        String key = vec.x+"_"+ vec.y+"_"+ vec.z;
        if(tag.contains(key))
            tag.remove(key);
        else if(!deleteOnly) {
            CompoundTag vectorTag = new CompoundTag();
            putVectorDouble(vectorTag, "", vec);
            tag.put(key, vectorTag);
        }
    }

    public static HashSet<Vec3> getVectorSet(CompoundTag tag) {
        HashSet<Vec3> set = new HashSet<>();
        for(String key : tag.getAllKeys()) {
            CompoundTag vectorTag = tag.getCompound(key);
            if(hasVectorDouble(vectorTag, "")) {
                Vec3 vec = getVectorFromNBT(vectorTag, "");
                set.add(vec);
            }
        }
        return set;
    }

    public static void setEntityTag(Entity entity, CompoundTag tag)
    {
        entity.load(tag);
    }

    public static void putVectorDouble(CompoundTag nbt, String name, Vec3 vec){
        nbt.putDouble(name + "X", vec.x);
        nbt.putDouble(name + "Y", vec.y);
        nbt.putDouble(name + "Z", vec.z);
    }

    public static void removeVectorDouble(CompoundTag nbt, String name){
        nbt.remove(name + "X");
        nbt.remove(name + "Y");
        nbt.remove(name + "Z");
    }

    public static boolean hasVectorDouble(CompoundTag nbt, String name){
        return nbt.contains(name + "X") && nbt.contains(name + "Y") && nbt.contains(name + "Z");
    }

    public static Vec3 getVectorFromNBT(CompoundTag nbt, String name){
        return new Vec3(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
    }
    //法杖改名：合成时法杖NBT记录原名，核心名字赋予法杖，复原时法杖名字跟随核心，NBT变回原名
    public static void contextItemWithCore(ItemStack contextTool, ItemStack coreItem) {
        if(coreItem.hasCustomHoverName()) {
            if(contextTool.hasCustomHoverName()) {
                contextTool.getOrCreateTag().putString("manaItemName", Component.Serializer.toJson(contextTool.getHoverName()));
            }
            contextTool.setHoverName(coreItem.getHoverName());
        }
    }

    public static void coreItemFromContext(ItemStack contextTool, ItemStack coreItem) {
        if(contextTool.hasCustomHoverName()) {
            coreItem.setHoverName(contextTool.getHoverName());
            contextTool.setHoverName(null);
        }
        if(contextTool.hasTag()) {
            if(contextTool.hasCustomHoverName())
                contextTool.setHoverName(null);
            if(contextTool.getTag().contains("manaItemName")) {
                Component itextcomponent = Component.Serializer.fromJson(contextTool.getTag().getString("manaItemName"));
                if (itextcomponent != null) {
                    contextTool.setHoverName(itextcomponent);
                    contextTool.getTag().remove("manaItemName");
                }
            }
        }
    }

    public static class PlayerData {
        private final CompoundTag tag;
        private final int limit;
        private final LinkedHashMap<String, ItemStack> spells = new LinkedHashMap<>();

        private PlayerData(CompoundTag tag) {
            if(!tag.contains("MagickCore"))
                tag.put("MagickCore", new CompoundTag());
            CompoundTag magickcore = tag.getCompound("MagickCore");
            if(!magickcore.contains("PersistentSpells"))
                magickcore.put("PersistentSpells", new CompoundTag());
            this.tag = magickcore;
            CompoundTag temp = magickcore.getCompound("PersistentSpells");
            int l = magickcore.getInt("Limit");
            limit = Math.max(l, 3);
            magickcore.putInt("Limit", limit);
            temp.getAllKeys().forEach(index -> {
                spells.put(index, ItemStack.of(temp.getCompound(index)));
            });
            for (Iterator<Map.Entry<String, ItemStack>> it = spells.entrySet().iterator(); it.hasNext();){
                Map.Entry<String, ItemStack> item = it.next();
                ItemStack val = item.getValue();
                if(val.isEmpty())
                    it.remove();
            }
        }
        public static PlayerData playerData(Player player) {
            CompoundTag persistentData = player.getPersistentData();
            if(!persistentData.contains(Player.PERSISTED_NBT_TAG))
                persistentData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
            return new PlayerData(persistentData.getCompound(Player.PERSISTED_NBT_TAG));
        }

        public void pushSpell(ItemStack stack) {
            for (int i = 0; i < limit; i++) {
                String key = String.valueOf(i);
                if(!spells.containsKey(key)) {
                    spells.put(key, stack);
                    return;
                }
            }
        }

        public ItemStack popSpell() {
            ItemStack stack = ItemStack.EMPTY;
            if(spells.size() < 1) return stack;
            Iterator<Map.Entry<String, ItemStack>> it = spells.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ItemStack> item = it.next();
                stack = item.getValue();
            }
            it.remove();
            return stack;
        }

        public Map<String, ItemStack> getSpells() {
            return spells;
        }

        public ItemStack swapSpell(ItemStack stack, int index) {
            int spellIndex = 0;
            String key = null;
            for (Map.Entry<String, ItemStack> item : spells.entrySet()) {
                if (index == spellIndex) {
                    key = item.getKey();
                    break;
                }
                spellIndex++;
            }
            if(key != null) {
                ItemStack spell = spells.get(key);
                spells.put(key, stack);
                return spell;
            }
            return ItemStack.EMPTY;
        }

        public ItemStack takeSpell(int index) {
            int spellIndex = 0;
            String key = null;
            for (Map.Entry<String, ItemStack> item : spells.entrySet()) {
                if (index == spellIndex) {
                    key = item.getKey();
                    break;
                }
                spellIndex++;
            }
            if(key != null) {
                ItemStack stack = spells.get(key);
                spells.remove(key);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        public void save() {
            CompoundTag newSpells = new CompoundTag();
            for (Map.Entry<String, ItemStack> item : spells.entrySet()) {
                newSpells.put(item.getKey(), item.getValue().save(new CompoundTag()));
            }
            tag.put("PersistentSpells", newSpells);
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            tag.putInt("Limit", limit);
        }
    }
}
