

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

	
	
	private final Setting<Boolean> notifyUponPlace = sgExtra.add(new BoolSetting.Builder()
            .name("notify-upon-place")
            .description("notify with a message upon sign placed")
            .defaultValue(false)
            .build()
    );
	
    public AnvilName() {
        super(Addon.CATEGORY, "waiting-auto-sign", "Automatically writes signs. The first sign's text will be used. Has a built-in delay");
    }


    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(event.screen instanceof AnvilScreen)) return;
		/*
		Field f = obj.getClass().getDeclaredField("stuffIWant"); //NoSuchFieldException
		f.setAccessible(true);
		Hashtable iWantThis = (Hashtable) f.get(obj); //IllegalAccessException
		*/
		
		
		/// Да, это ужас и уродство, но типо норм...
		try{
			Field f = event.screen.getClass().getDeclaredField("nameField"); //NoSuchFieldException
			
			f.setAccessible(true);
		
			try{
				TextFieldWidget iWantThis = (TextFieldWidget) f.get(event.screen); //IllegalAccessException
				iWantThis.setText(textOnAnvil.get());
			}catch(IllegalAccessException e){
				/// ...
			}
			
		}catch(NoSuchFieldException e){
			/// ...
		}
		
		
        
		
		///((AnvilScreen)(event.screen)).nameField.setText(textOnAnvil.get());
		///f.setText(textOnAnvil.get());
        
    }
	
	
}


