package com.example.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
/// ^^^ MODULE BASIC IMPORTS ^^^
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;


import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.events.game.SectionVisibleEvent;
import meteordevelopment.meteorclient.events.render.TooltipDataEvent;
import meteordevelopment.meteorclient.mixin.EntityAccessor;
import meteordevelopment.meteorclient.mixin.EntityBucketItemAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ByteCountDataOutput;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.tooltip.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import java.lang.reflect.Constructor;

import java.io.PrintWriter;
import java.io.File;  // Import the File class

import java.util.HashMap;
import java.util.Map;

public class BookSaver extends Module {
    public BookSaver(Category cat) {
        super(cat, "Book Saver", "Saves data from books to files! You can find them in books_saved folder under .minecraft");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
	/*
    private final Setting<Integer> FOV = sgGeneral.add(new IntSetting.Builder()
        .name("FOV")
        .description("What the FOV should be.")
        .defaultValue(120)
        .range(0, 358)
        .sliderRange(0, 358)
        .build()
    );
	*/
	
	private final Setting<Integer> emptylines = sgGeneral.add(new IntSetting.Builder()
        .name("empty-lines")
        .description("Amount of empty lines added for next page")
        .defaultValue(3)
        .range(1, 1000)
        .sliderRange(1, 10)
        .build()
    );
	
	private final Map<String, Integer> BooksSeen = new HashMap<>();
	
	@Override
    public void onActivate() {
		BooksSeen.clear();
    }
	
	
	private String getPage(ItemStack stack, int page) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) return null;

        NbtList pages = tag.getList("pages", 8);
        if (pages.size() < page + 1) return null;
        if (stack.getItem() == Items.WRITABLE_BOOK) return pages.getString(page);
		
		try {
            return pages.getString(page);
        } catch (JsonParseException e) {
            return "Invalid book data";
        }
		
		/* будет актуально на 1.20.4
        try {
            return Text.Serialization.fromLenientJson(pages.getString(page));
        } catch (JsonParseException e) {
            return Text.literal("Invalid book data");
        }
		*/
    }
	
	public int counter = 0;
	
	
	
	@EventHandler
    private void getTooltipData(TooltipDataEvent event) {
        
        if (event.itemStack.getItem() != Items.WRITTEN_BOOK) return;
            
		NbtCompound tag = event.itemStack.getNbt();
		if (tag == null) return;
		
		
		
		String title = tag.getString("title");
		String author = tag.getString("author");
		int gen = event.itemStack.getNbt().getInt("generation");
		String g = "Амонгус";
		
		if (gen == 0) g = "Оригинал";
		if (gen == 1) g = "Копия";
		if (gen == 2) g = "Копия Копии";
		
		String fileSeparator = System.getProperty("file.separator");
		String name = "books_saved"+fileSeparator+author + "_" + title + "_" + Integer.toString(event.itemStack.getNbt().getList("pages", 8).hashCode()) + ".txt";
		
		if (BooksSeen.containsKey(name)) return;
		BooksSeen.put(name, 1);
		
		PrintWriter writer;
		
		try{
			/// this is so utterely stoopid. REALLY STOOOOOOOOUUUUUUPID
			File booksDir = new File("books_saved");
			booksDir.mkdir();
			
			writer = new PrintWriter(name, "UTF-8");
			writer.println("Автор : " + author);
			writer.println("Название : " + title);
			writer.println("Состояние : " + g);
		}
		catch(IOException e){
			info("some shit in BookSaver...");
			return;
		}
		
		boolean old = false;
		
		for(int i = 0; i < 100; ++i){
			String page = getPage(event.itemStack, i);
			if (page == null) continue;

			
			/// {"text":"езезезеез"}
			
			
			if (page.length() <= 10){
				info("How that even happened? " + page);
				continue;
			}
			
			/// Dorty 11.04.2024 - я вообще хз, что за лажа в старых книгах лежит -_-
			if (page.startsWith("{\"text\":\"")){
				page = page.substring(9);
				old = true;
			}else{
				page = page.substring(1);
			}
			
			if (page.endsWith("\"}")){
				page = page.substring(0, page.length() - 2);
				old = true;
			}else{
				page = page.substring(0, page.length() - 1);
			}

			
			
			
			page = page.replace("\\n", "\n");
			page = page.replace("§4", "");
			page = page.replace("§3", "");
			page = page.replace("§2", "");
			page = page.replace("§1", "");
			page = page.replace("§0", "");
			
			page = page.replace("§k", "");
			page = page.replace("§r", "");
			
			
			for(int j = 0; j < emptylines.get(); ++j)
				writer.println();
			writer.println(page);
			
			info("written page");
			
			
			
			
			
			
			
		}
		
		if (old){
			writer.println();
			writer.println("* Кажется, что это книга старого формата");
		}
		
		writer.close();
        
	}
	
}