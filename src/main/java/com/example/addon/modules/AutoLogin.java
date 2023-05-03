package com.example.addon.modules;

import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import net.minecraft.text.Text;

import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoLogin extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Boolean> fromFile = sgGeneral.add(new BoolSetting.Builder()
        .name("from-file")
        .description("Use Auto Login only on server.")
        .defaultValue(true)
        .build()
    );
	
    private final Setting<String> loginCommand = sgGeneral.add(new StringSetting.Builder()
        .name("login-command")
        .description("Command to login.")
		.visible(() -> !fromFile.get())
        .defaultValue("login 1234")
        .build()
    );

    private final Setting<Boolean> serverOnly = sgGeneral.add(new BoolSetting.Builder()
        .name("server-only")
        .description("Use Auto Login only on server.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay before send command in ticks (20 ticks = 1 sec).")
        .defaultValue(20)
        .range(1, 120)
        .sliderRange(1, 40)
        .build()
    );

    boolean work;
    private int timer;

    public AutoLogin() {
        super(Addon.CATEGORY, "auto-login-228", "Automatically logs in your account.");
    }

    @Override
    public void onActivate() {
		
		try {
			File myObj = new File("passwords.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
        timer = 0;
        work = true;
    }

    //Shitty code anyway work
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (serverOnly.get() && mc.getServer() != null && mc.getServer().isSingleplayer()) return;
		
		
		
        if (timer >= delay.get() && !loginCommand.get().isEmpty() && work) {
            work = false;
			if (fromFile.get()){
				info("reading file...");
				boolean logged = false;
				try {
					FileReader reader = new FileReader("passwords.txt");
					BufferedReader bufferedReader = new BufferedReader(reader);
		 
					String line;
		 
					while ((line = bufferedReader.readLine()) != null) {

						String[] dataa = line.split(" ");
						
						
						
						if (dataa[0].equals(Utils.getWorldName()) && dataa[1].equals( (mc.player.getName().getString()) )){
							ChatUtils.sendPlayerMsg("/login " + dataa[2]);
							logged = true;
							break;
							
						}
						
					}
					reader.close();
		 
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (!logged){
					info("Oops, password seems missing");
				}
			}else{
				ChatUtils.sendPlayerMsg("/" + loginCommand.get());
				//mc.player.sendChatMessage(loginCommand.get().replace("/", ""));
			}
            timer = 0;
        } else timer ++;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        work = true;
    }
}