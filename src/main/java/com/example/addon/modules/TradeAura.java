

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
        MinecraftClient.getInstance().executeSync(() -> triggerTradeCheck(p.getOffers()));
		///TradeOfferList l = p.getOffers();
		///for (TradeOffer offer : l) {
		///	p.trade(offer);
		///}
		
    }

    public void triggerTradeCheck(TradeOfferList l) {
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
			
			ItemStack emeraldIS = mc.player.getInventory().getStack(resultEm.slot());
			
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
}


