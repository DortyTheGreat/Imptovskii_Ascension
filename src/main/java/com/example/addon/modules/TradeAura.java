

package com.example.addon.modules;

import com.example.addon.SHAccessor; 

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

import com.example.addon.Addon;
import static com.example.addon.Utils.*; 

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixin.AbstractSignEditScreenAccessor;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;


import java.lang.reflect.Field;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WIntEdit;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import java.io.*; 


import net.minecraft.client.gui.widget.TextFieldWidget;

import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.entity.passive.MerchantEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.village.*;
import net.minecraft.village.MerchantInventory;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.screen.ScreenHandler;

public class TradeAura extends Module {

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	
	final SettingGroup sgExtra = settings.createGroup("Visible");
	
	
	private final Setting<String> textOnAnvil = sgExtra.add(new StringSetting.Builder()
            .name("text")
            .description("What to put on the first line of the sign.")
            .defaultValue("Steve")
            .build()
    );
	
	private final Setting<Boolean> Debug = sgExtra.add(new BoolSetting.Builder()
            .name("Debug")
            .description("notify with a message to debug module")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<Boolean> Close = sgExtra.add(new BoolSetting.Builder()
            .name("Close")
            .description("notify with a message to debug module")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<Boolean> DPI = sgExtra.add(new BoolSetting.Builder()
            .name("DPI")
            .description("notify with a message to debug module")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<Boolean> USE = sgExtra.add(new BoolSetting.Builder()
            .name("USE")
            .description("notify with a message to debug module")
            .defaultValue(false)
            .build()
    );
	
    public TradeAura() {
        super(Addon.CATEGORY, "Trade-Aura", "Trades with villagers for you");
    }


    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof SetTradeOffersS2CPacket p)) return;
        ///MinecraftClient.getInstance().executeSync(() -> triggerTradeCheck(p.getOffers()));
		///TradeOfferList l = p.getOffers();
		///for (TradeOffer offer : l) {
		///	p.trade(offer);
		///}
		
    }
	
	private MerchantScreenHandler MSH_g;
	
	public Object genericInvokeMethod(Object obj, String methodName, Object... params) {
        int paramCount = params.length;
        Method method;
        Object requiredObj = null;
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            classArray[i] = params[i].getClass();
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException e) {
            info("NoSuchMethodException, check the code to update obfuscated name of method");
        } catch (IllegalArgumentException e) {
            info("IllegalArgumentException, check the code to update obfuscated name of method");
        } catch (IllegalAccessException e) {
            info("IllegalAccessException");
        } catch (InvocationTargetException e) {
            info("InvocationTargetException");
        }

        return requiredObj;
    }
	
	@EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
		if (!(event.screen instanceof MerchantScreen)) return;
		
		if (!(mc.player.currentScreenHandler instanceof MerchantScreenHandler MSH)) return;
		Merchant tmp1;
		MSH_g = MSH;
		/// АХТУНГ! https://maven.fabricmc.net/docs/yarn-23w51b+build.4/net/minecraft/screen/MerchantScreenHandler.html#merchant
		Object babaj;
		try{
			if ( !(FieldUtils.readField(MSH, "field_7863", true) instanceof Merchant merc) ) return;
			///MinecraftClient.getInstance().executeSync(() -> syncing_func(MSH));
			
			FindItemResult resultEm = InvUtils.find(Items.EMERALD); 
			if (!resultEm.found()){
				if (Debug.get()){info("no emerald");}
				return;
			}
			
			info( MSH.quickMove(mc.player, resultEm.slot()).getName());
			for(int i = 0; i < 500; ++i){
				if (MSH.isValid(i)){
					ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
					
					//info(emeraldIS.getName());
					
					info(MSH.getSlot(i).getStack().getName() + " " +  i + " " + MSH.getSlot(i).getIndex() + " " + MSH.getRevision() );
					
					///MSH.getSlot(i).setStack(emeraldIS);
				}
			}
			ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
			
			SHAccessor SAVE_ME_PLS = new SHAccessor()
			
			//if (!(MSH instanceof SHAccessor SAVE_ME_PLS)){info("OH NO!!!!"); return;}
			SAVE_ME_PLS.PublicinsertItem(emeraldIS, 0,1, false); // I really wanna use that
			
			
			
			
			/// insertItem https://maven.fabricmc.net/docs/yarn-23w51b+build.4/net/minecraft/screen/ScreenHandler.html#insertItem(net.minecraft.item.ItemStack,int,int,boolean)
			
			genericInvokeMethod(MSH, "method_20214", 0, emeraldIS);
			
			MSH.updateToClient(); // ????
			info("here2");
			
			
		}catch(IllegalAccessException e){
			info("IAE ex");
		}
		///Merchant tmp = MSH.merchant;
	}
	
	private void syncing_func(MerchantScreenHandler MSH){
		FindItemResult resultEm = InvUtils.find(Items.EMERALD); 
		if (!resultEm.found()){
			if (Debug.get()){info("no emerald");}
			return;
		}
		
		info( MSH.quickMove(mc.player, resultEm.slot()).getName());
		for(int i = 0; i < 500; ++i){
			if (MSH.isValid(i)){
				ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
				
				//info(emeraldIS.getName());
				
				info(MSH.getSlot(i).getStack().getName() + " " +  i + " " + MSH.getSlot(i).getIndex() + " " + MSH.getRevision() );
				
				///MSH.getSlot(i).setStack(emeraldIS);
			}
		}
		ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
		
		MSH.setStackInSlot(0, MSH.getRevision(), emeraldIS);
		
		MSH.updateToClient(); // ????
		
		///info(MSH.quickMove(mc.player, resultEm.slot()).getName()); ///THIS ACTUALLY CREATED GHOST ITEM!
		
		///MSH.getSlot(1).setStack(emeraldIS);
		
		info("here");
		///InvUtils.swap(MSH.getSlot(0).getIndex(), resultEm.slot());
		
		///InvUtils.move().from(resultEm.slot()).to(MSH.getSlot(0).getIndex());
		
		///MSH.getSlot(0).setStack(emeraldIS);
	}
	
	private int slotID = 0;
	private ItemStack forem;
	private MerchantInventory MI;
	
    public void triggerTradeCheck(TradeOfferList l, Merchant merc) {
		
		MI = new MerchantInventory(merc);
		
		
        for (TradeOffer offer : l) {
            if (Debug.get()){info(String.format("Offer: %s", offer.getSellItem().toString()));}
            ItemStack sellItem = offer.getSellItem();
            if (!sellItem.isOf(Items.GOLDEN_CARROT)){
				if (Debug.get()){info("This is not a carrot");}
				continue;
			}
			/// https://maven.fabricmc.net/docs/yarn-20w51a+build.9/net/minecraft/village/TradeOffer.html#depleteBuyItems(net.minecraft.item.ItemStack,net.minecraft.item.ItemStack)
            
			FindItemResult resultEm = InvUtils.find(Items.EMERALD); 
			FindItemResult resultEmp = InvUtils.find(Items.AIR); 
			if (!resultEm.found()){
				if (Debug.get()){info("no emerald");}
				continue;
			}
			
			if (!resultEmp.found()){
				if (Debug.get()){info("no empty");}
				continue;
			}
			
			merc.trade(offer);
			
			ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
			forem = emeraldIS;
			
			info(emeraldIS.getName());
			
			slotID = -1;
			///MSH_g = MSH;
			for(int i = 0; i < 310; ++i){
				///setTimeout(this::DoTheThing,1000 + 100 * i);
				///MI.setStack(i, emeraldIS);
			}
			
			info(MI.getTradeOffer().getSellItem().getName());
			
			ItemStack emptyIS = mc.player.getInventory().getStack(resultEmp.slot());
			
			if (DPI.get()){
				info(Boolean.toString(offer.depleteBuyItems(emeraldIS, emptyIS)));
			}
			
			if (USE.get()){
				offer.use();
			}
			
			///use ???
			
			///info(Boolean.toString(offer.depleteBuyItems(emeraldIS, emptyIS)));
        }
        // ((MerchantScreenHandler)mc.player.currentScreenHandler).closeHandledScreen();
        if (Close.get()){
			
			mc.player.closeHandledScreen();
			//((MerchantScreenHandler)mc.player.currentScreenHandler).closeHandledScreen();
		}
        
    }
	
	private void DoTheThing(){
		slotID++;
		info("slot " + slotID);
		///info(slotID);
		info(forem.getName());
		
		/// autofill https://maven.fabricmc.net/docs/yarn-23w51b+build.4/net/minecraft/screen/MerchantScreenHandler.html#autofill(int,net.minecraft.item.ItemStack)
		try{
			Method method = MSH_g.getClass().getDeclaredMethod("method_20214");
			method.setAccessible(true);
			method.invoke(slotID,forem);
			
			///MSH_g.autofill(slotID,forem);
			///MI.setStack(slotID, forem);
			info(MI.getTradeOffer().getSellItem().getName());
		}catch(NoSuchMethodException e){
			info("NoSuchMethodException, check the code to update obfuscated name of method");
			
		}catch(IllegalAccessException e){
			info("IllegalAccessException, I have no idea how this could happen");
		}catch(InvocationTargetException e){
			info("InvocationTargetException, I have no idea how this could happen");
		}
		
		
	}
}


