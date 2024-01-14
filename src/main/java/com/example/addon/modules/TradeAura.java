

package com.example.addon.modules;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

import com.example.addon.Addon;
import static com.example.addon.Utils.*; 

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;



import java.lang.reflect.Field;
import meteordevelopment.meteorclient.MeteorClient;

import meteordevelopment.meteorclient.settings.*;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;

import net.minecraft.client.MinecraftClient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;


import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;


import java.util.List;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import net.minecraft.item.Item;
import java.util.Arrays;

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

import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;

public class TradeAura extends Module {

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	
	final SettingGroup sgExtra = settings.createGroup("Visible");
	
	
	private final Setting<Boolean> Debug = sgExtra.add(new BoolSetting.Builder()
            .name("Debug")
            .description("notify with a message to debug module")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<Boolean> Close = sgExtra.add(new BoolSetting.Builder()
            .name("Close")
            .description("Close trading screen after trade")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<Integer> Index = sgGeneral.add(new IntSetting.Builder()
        .name("Index")
        .description("???")
        .defaultValue(0)
        .min(0)
        .sliderMax(10000)
        .build()
    );
	
	private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Items you want to buy.")
        .defaultValue(Arrays.asList())
        .build()
    );
	
    public TradeAura() {
        super(Addon.CATEGORY, "Trade-Aura", "Trades with villagers for you");
    }

	/*
    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof SetTradeOffersS2CPacket p)) return;
        ///MinecraftClient.getInstance().executeSync(() -> triggerTradeCheck(p.getOffers()));
		///TradeOfferList l = p.getOffers();
		///for (TradeOffer offer : l) {
		///	p.trade(offer);
		///}
		
    }
	*/
	
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

		
		MinecraftClient.getInstance().executeSync(() -> syncing_func(MSH));
		///Merchant tmp = MSH.merchant;
	}
	
	private void syncing_func(MerchantScreenHandler MSH){
		try{
			/// АХТУНГ! https://maven.fabricmc.net/docs/yarn-23w51b+build.4/net/minecraft/screen/MerchantScreenHandler.html#merchant
			if ( !(FieldUtils.readField(MSH, "field_7863", true) instanceof Merchant merc) ) return;
			FindItemResult resultEm = InvUtils.find(Items.EMERALD); 
			if (!resultEm.found()){
				if (Debug.get()){info("no emerald");}
				return;
			}
			
			
			
			info("H1");
			
			
			
			
			TradeOfferList Offers = MSH.getRecipes();
			int num = 0;
			for (TradeOffer offer : Offers) {
				
				if (Debug.get()){info(String.format("Offer: %s", offer.getSellItem().toString()));}
				
				ItemStack sellItem = offer.getSellItem();
				if (items.get().contains(sellItem.getItem())){
					if (Debug.get()){info("BUYING " + sellItem.getName());}
					mc.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(num));
					InvUtils.shiftClick().slotId(2);
				}
				/// https://maven.fabricmc.net/docs/yarn-20w51a+build.9/net/minecraft/village/TradeOffer.html#depleteBuyItems(net.minecraft.item.ItemStack,net.minecraft.item.ItemStack)
				
				num++;
			}
			
			
			///ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
			
			
			if (Close.get()){
			
				mc.player.closeHandledScreen();
				//((MerchantScreenHandler)mc.player.currentScreenHandler).closeHandledScreen();
			}
			
		}catch(IllegalAccessException e){
			info("IAE ex");
		}
		
	}
	
	
	
    /*public void triggerTradeCheck(TradeOfferList l, Merchant merc) {
		
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
			
			
			
			///use ???
			
			///info(Boolean.toString(offer.depleteBuyItems(emeraldIS, emptyIS)));
        }
        // ((MerchantScreenHandler)mc.player.currentScreenHandler).closeHandledScreen();
        if (Close.get()){
			
			mc.player.closeHandledScreen();
			//((MerchantScreenHandler)mc.player.currentScreenHandler).closeHandledScreen();
		}
        
    }
	*/
	
	
}


