package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

public class NBTTagHelper {
    private static final String TAG_ITEM_DAMAGE = "Damege";
    private static final String TAG_ITEM_COUNT = "Count";
    private static final String TAG_ITEM_TAG = "tag";
    private static final String TAG_ITEM_ID = "id";
    private final CompoundNBT tag;

    public NBTTagHelper(CompoundNBT tag) {
        this.tag = tag;
    }

    public static CompoundNBT getStackTag(ItemStack stack) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
        return stack.getTag();
    }

    public static Entity createEntityByItem(ItemStack stack, World world) {
        if(!stack.hasTag() || !stack.getTag().contains("Magick_Store_Entity")) return null;
        CompoundNBT tag = stack.getTag().getCompound("Magick_Store_Entity");
        Optional<EntityType<?>> type = EntityType.readEntityType(tag);
        if(type.isPresent()) {
            Entity entity = type.get().create(world);
            tag.remove("UUID");
            entity.read(tag);
            return entity;
        } else
            return null;
    }

    public static ItemStack createItemWithEntity(Entity entity, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        storeEntityToItem(entity, stack);
        return stack;
    }

    public static void storeEntityToItem(Entity entity, ItemStack item) {
        CompoundNBT tag = new CompoundNBT();
        entity.writeUnlessRemoved(tag);
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

    public void ifContainNBT(String s, Consumer<CompoundNBT> consumer) {
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
            consumer.accept(tag.getUniqueId(s));
    }

    public static CompoundNBT getBlockTag(CompoundNBT tag) {
        return tag.getCompound("BlockEntityTag");
    }

    public static CompoundNBT getEntityTag(Entity entity) {
        CompoundNBT nbt = new CompoundNBT();
        entity.writeUnlessRemoved(nbt);
        return nbt;
    }

    public static boolean hasElement(ItemStack stack) {
        return getStackTag(stack).contains("ELEMENT");
    }

    public static ItemStack setElement(ItemStack stack, String element) {
        if(!stack.isEmpty()) {
            CompoundNBT tag = getStackTag(stack);
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

    public static boolean consumeElementOnTool(ItemStack stack, String element) {
        if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT) && hasElementOnTool(stack, element)) {
            CompoundNBT tag = getToolElementTable(stack);
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
            CompoundNBT tag = getToolElementTable(stack);
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
        CompoundNBT tag = getToolElementTable(stack);
        tag.putInt(element, 300);
        CompoundNBT nbt = getStackTag(stack);
        nbt.put(LibElementTool.TOOL_ELEMENT, tag);
        stack.setTag(nbt);
    }

    public static void putElementOnTool(ItemStack stack, String element, int durability) {
        CompoundNBT tag = getToolElementTable(stack);
        tag.putInt(element, durability);
        CompoundNBT nbt = getStackTag(stack);
        nbt.put(LibElementTool.TOOL_ELEMENT, tag);
        stack.setTag(nbt);
    }

    public static CompoundNBT getToolElementTable(ItemStack stack) {
        CompoundNBT tag = getStackTag(stack);
        if(!tag.contains(LibElementTool.TOOL_ELEMENT))
            tag.put(LibElementTool.TOOL_ELEMENT, new CompoundNBT());

        return tag.getCompound(LibElementTool.TOOL_ELEMENT);
    }

    public static void setEntityTag(Entity entity, CompoundNBT tag)
    {
        entity.read(tag);
    }

    public static void putVectorDouble(CompoundNBT nbt, String name, Vector3d vec){
        nbt.putDouble(name + "X", vec.x);
        nbt.putDouble(name + "Y", vec.y);
        nbt.putDouble(name + "Z", vec.z);
    }

    public static void removeVectorDouble(CompoundNBT nbt, String name){
        nbt.remove(name + "X");
        nbt.remove(name + "Y");
        nbt.remove(name + "Z");
    }

    public static boolean hasVectorDouble(CompoundNBT nbt, String name){
        return nbt.contains(name + "X") && nbt.contains(name + "Y") && nbt.contains(name + "Z");
    }

    public static Vector3d getVectorFromNBT(CompoundNBT nbt, String name){
        return new Vector3d(nbt.getDouble(name + "X"), nbt.getDouble(name + "Y"), nbt.getDouble(name + "Z"));
    }
    //法杖改名：合成时法杖NBT记录原名，核心名字赋予法杖，复原时法杖名字跟随核心，NBT变回原名
    public static void contextItemWithCore(ItemStack contextTool, ItemStack coreItem) {
        if(coreItem.hasDisplayName()) {
            if(contextTool.hasDisplayName()) {
                contextTool.getOrCreateTag().putString("manaItemName", ITextComponent.Serializer.toJson(contextTool.getDisplayName()));
            }
            contextTool.setDisplayName(coreItem.getDisplayName());
        }
    }

    public static void coreItemFromContext(ItemStack contextTool, ItemStack coreItem) {
        if(contextTool.hasDisplayName()) {
            coreItem.setDisplayName(contextTool.getDisplayName());
            contextTool.setDisplayName(null);
        }
        if(contextTool.hasTag()) {
            if(contextTool.hasDisplayName())
                contextTool.setDisplayName(null);
            if(contextTool.getTag().contains("manaItemName")) {
                ITextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(contextTool.getTag().getString("manaItemName"));
                if (itextcomponent != null) {
                    contextTool.setDisplayName(itextcomponent);
                    contextTool.getTag().remove("manaItemName");
                }
            }
        }
    }

    public static class PlayerData {
        private final CompoundNBT tag;
        private final int limit;
        private final LinkedHashMap<String, ItemStack> spells = new LinkedHashMap<>();

        private PlayerData(CompoundNBT tag) {
            if(!tag.contains("MagickCore"))
                tag.put("MagickCore", new CompoundNBT());
            CompoundNBT magickcore = tag.getCompound("MagickCore");
            if(!magickcore.contains("PersistentSpells"))
                magickcore.put("PersistentSpells", new CompoundNBT());
            this.tag = magickcore;
            CompoundNBT temp = magickcore.getCompound("PersistentSpells");
            int l = magickcore.getInt("Limit");
            limit = Math.max(l, 3);
            magickcore.putInt("Limit", limit);
            temp.keySet().forEach(index -> {
                spells.put(index, ItemStack.read(temp.getCompound(index)));
            });
            for (Iterator<Map.Entry<String, ItemStack>> it = spells.entrySet().iterator(); it.hasNext();){
                Map.Entry<String, ItemStack> item = it.next();
                ItemStack val = item.getValue();
                if(val.isEmpty())
                    it.remove();
            }
        }
        public static PlayerData playerData(PlayerEntity player) {
            CompoundNBT persistentData = player.getPersistentData();
            if(!persistentData.contains(PlayerEntity.PERSISTED_NBT_TAG))
                persistentData.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
            return new PlayerData(persistentData.getCompound(PlayerEntity.PERSISTED_NBT_TAG));
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
            CompoundNBT newSpells = new CompoundNBT();
            for (Map.Entry<String, ItemStack> item : spells.entrySet()) {
                newSpells.put(item.getKey(), item.getValue().write(new CompoundNBT()));
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
