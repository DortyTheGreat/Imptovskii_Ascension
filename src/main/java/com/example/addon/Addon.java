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

public class Addon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Example");
    public static final HudGroup HUD_GROUP = new HudGroup("Example");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Dorty-Pants");

        // Modules
        Modules.get().add(new ModuleExample());
		Modules.get().add(new sphere());
		Modules.get().add(new ElytraPlusPlus());
		Modules.get().add(new WaitingAutoSign());
		Modules.get().add(new AutoLogin());
		Modules.get().add(new Printer());
		Modules.get().add(new AutoBuild());
		Modules.get().add(new ChatBot());
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
