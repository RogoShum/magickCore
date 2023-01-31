package com.rogoshum.magickcore.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.recipe.NBTRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class GenerationUtil {
    public static final File dir = new File(Minecraft.getInstance().gameDirectory, "generation");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void generateElementFuncFile(String element, String applyType) {
        String name = "element_func_" + element + "_" + applyType;
        String trigger = "trigger"+"_"+name;

        JsonObject json = new JsonObject();
        JsonArray pages = new JsonArray();
        JsonObject text = new JsonObject();
        text.addProperty("type", "text");
        text.addProperty("text", "");
        pages.add(text);
        json.addProperty("name", new TranslatableComponent("magickcore.description." + element).getString() + new TranslatableComponent("magickcore.context." + applyType).getString());
        json.addProperty("advancement", "magickcore:learn/" + trigger);
        json.addProperty("icon", "magickcore:" + element);
        json.addProperty("category", "element");
        json.add("pages", pages);
        save(name+".json", json);
        generateStringTrigger(name);
    }

    public static void generateEntityType(EntityType<?> entityType) {
        String name = "entity_type_" + entityType.getDefaultLootTable().getPath();
        String trigger = "trigger"+"_"+name;

        JsonObject json = new JsonObject();
        JsonArray pages = new JsonArray();
        JsonObject text = new JsonObject();
        text.addProperty("type", "text");
        text.addProperty("text", "");
        pages.add(text);
        json.addProperty("name", new TranslatableComponent(entityType.getDescriptionId()).getString());
        json.addProperty("advancement", "magickcore:learn/" + trigger);
        ItemStack type = new ItemStack(ModItems.ENTITY_TYPE.get());
        ExtraDataUtil.itemManaData(type, data -> data.spellContext().addChild(SpawnContext.create(entityType)));
        json.addProperty("icon", "magickcore:entity_type" + (type.getTag() == null ? "" : type.getTag()));
        json.addProperty("category", "spell_form");
        json.add("pages", pages);
        save(name+".json", json);
        generateStringTrigger(name);
    }

    public static void generateStringTrigger(String trigger) {
        JsonObject json = new JsonObject();
        JsonObject conditions = new JsonObject();
        JsonObject example = new JsonObject();
        JsonObject criteria = new JsonObject();
        example.addProperty("trigger", "magickcore:string");
        conditions.addProperty("string", trigger);
        example.add("conditions", conditions);
        criteria.add("example", example);
        json.add("criteria", criteria);
        save("trigger_"+trigger+".json", json);
    }

    public static void save(String fileName, JsonObject json) {
        Path path = dir.toPath().resolve(fileName);
        File file = path.toFile();
        if(!dir.exists())
            dir.mkdirs();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String s = GSON.toJson(json);
        try (BufferedWriter out = Files.newBufferedWriter(path)) {
            out.write(s);
        } catch (Exception ex) {
            System.out.println("Exception thrown while saving file!");
            ex.printStackTrace();
        }
    }

    public static JsonObject load(String fileName) {
        Path path = dir.toPath().resolve(fileName);
        try (BufferedReader scan = Files.newBufferedReader(path)) {
            String line;
            StringBuilder json = new StringBuilder();
            while ((line = scan.readLine()) != null) {
                json.append(line);
            }

            return GsonHelper.parse(json.toString());
        } catch (NoSuchFileException ignored) {
            System.out.println("File not found.");
        } catch (Exception ex) {
            System.out.println("Exception while reading file!");
            ex.printStackTrace();
        }
        return null;
    }
}
