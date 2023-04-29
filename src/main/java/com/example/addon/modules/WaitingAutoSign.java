

package com.example.addon.modules;


import meteordevelopment.meteorclient.settings.IntSetting;
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
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

public class WaitingAutoSign extends Module {
    private String[] _text;
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final Setting<Integer> timeMS = sgGeneral.add(new IntSetting.Builder()
        .name("waiting-time-in-ms")
        .description("???")
        .defaultValue(0)
        .min(0)
        .sliderMax(10000)
        .build()
    );
	
    public WaitingAutoSign() {
        super(Addon.CATEGORY, "waiting-auto-sign", "Automatically writes signs. The first sign's text will be used. Has a built-in delay");
    }

    @Override
    public void onDeactivate() {
        _text = null;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof UpdateSignC2SPacket)) return;

        _text = ((UpdateSignC2SPacket) event.packet).getText();
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(event.screen instanceof SignEditScreen) || _text == null) return;

        
		setTimeout(() -> {
			SignBlockEntity sign = ((AbstractSignEditScreenAccessor) event.screen).getSign();
			mc.player.networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), _text[0], _text[1], _text[2], _text[3]));

        event.cancel();
				
				}, timeMS.get());
        
    }
	
	
}


