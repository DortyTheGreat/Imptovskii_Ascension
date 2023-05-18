package com.example.addon.modules;

import com.example.addon.Addon;


import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

/**
 * @author OLEPOSSU
 */


import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;



public class Suicide extends Module {
	//private boolean toggle;
    public Suicide() {
        super(Addon.CATEGORY, "Suicide", "Kills yourself. Recommended.");
    }
	
	// хз, что это. Это взял из BlackOut'a
	public void sendMessage(Text text, int id) {
        ((IChatHud) mc.inGameHud.getChatHud()).add(text, id);
    }
	
	private final String prefix = Formatting.DARK_RED + "[Dorty]";
	
	// хз, что это. Это взял из BlackOut'a
	public void sendDisableMsg(String text) {
        if (mc.world != null) {
            ChatUtils.forceNextPrefixClass(getClass());
            String msg = prefix + " " + Formatting.WHITE + name + Formatting.RED + " OFF " + Formatting.GRAY + text;
            sendMessage(Text.of(msg), hashCode());
        }
    }
	
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> disableDeath = sgGeneral.add(new BoolSetting.Builder()
        .name("Disable On Death")
        .description("Disables the module on death.")
        .defaultValue(true)
        .build()
    );

    @EventHandler(priority = 6969)
    private void onDeath(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen && disableDeath.get()) {
            toggle();
            sendDisableMsg("died");
        }
    }
}