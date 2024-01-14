

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


import net.minecraft.client.gui.widget.TextFieldWidget;

public class AnvilName extends Module {

	private OpenScreenEvent event_;
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	
	final SettingGroup sgExtra = settings.createGroup("Visible");
	
	
	private final Setting<String> textOnAnvil = sgExtra.add(new StringSetting.Builder()
            .name("text")
            .description("What to put on the first line of the sign.")
            .defaultValue("Steve")
            .build()
    );
	
	private final Setting<Integer> EColor = sgGeneral.add(new IntSetting.Builder()
        .name("EColor")
        .description("???")
        .defaultValue(0)
        .min(0)
        .sliderMax(10000)
        .build()
    );
	
	private final Setting<Integer> UEColor = sgGeneral.add(new IntSetting.Builder()
        .name("UEColor")
        .description("???")
        .defaultValue(0)
        .min(0)
        .sliderMax(10000)
        .build()
    );
	
	
	private final Setting<Boolean> notifyUponPlace = sgExtra.add(new BoolSetting.Builder()
            .name("notify-upon-place")
            .description("notify with a message upon sign placed")
            .defaultValue(false)
            .build()
    );
	
    public AnvilName() {
        super(Addon.CATEGORY, "Anvil-Name", "Automatically writes signs. The first sign's text will be used. Has a built-in delay");
    }


    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(event.screen instanceof AnvilScreen)) return;
		/*
		Field f = obj.getClass().getDeclaredField("stuffIWant"); //NoSuchFieldException
		f.setAccessible(true);
		Hashtable iWantThis = (Hashtable) f.get(obj); //IllegalAccessException
		*/
		
		event_ = event;
		for(int i = 0; i < 10; ++i){
			setTimeout(this::DoTheThing,1000 + 500 * i);
		}
		
		
        
		
		///((AnvilScreen)(event.screen)).nameField.setText(textOnAnvil.get());
		///f.setText(textOnAnvil.get());
        
    }
	
	private void DoTheThing(){
		if (notifyUponPlace.get()){info("Screen Opened");}
		/// Да, это ужас и уродство, но типо норм...
		try{
			Field f = ((AnvilScreen)(event_.screen)).getClass().getDeclaredField("field_2821"); //NoSuchFieldException
			/// https://maven.fabricmc.net/docs/yarn-23w51b+build.4/net/minecraft/client/gui/screen/ingame/AnvilScreen.html#nameField
			
			
			///((AnvilScreen)(event_.screen)).nameField.setText(textOnAnvil.get());
			
			
			f.setAccessible(true);
			/**
			
			TO-DO:
			rewrite to https://stackoverflow.com/questions/1196192/how-to-read-the-value-of-a-private-field-from-a-different-class-in-java
			
			*/
			try{
				TextFieldWidget iWantThis = (TextFieldWidget) f.get(((AnvilScreen)(event_.screen))); //IllegalAccessException
				iWantThis.setText(textOnAnvil.get());
				iWantThis.setEditableColor(EColor.get());
				iWantThis.setUneditableColor(UEColor.get());
				if (notifyUponPlace.get()){info("text changed");}
			}catch(IllegalAccessException e){
				/// ...
				if (notifyUponPlace.get()){info("IllegalAccessException");}
			}
			
		}catch(NoSuchFieldException e){
			if (notifyUponPlace.get()){info("NoSuchFieldException");}
		}
	}
}


