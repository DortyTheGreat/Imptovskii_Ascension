

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
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

public class WaitingAutoSign extends Module {
    private String[] _text;
	
	private OpenScreenEvent event_;
	
	private int ticker = 0;
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	
	
	private final Setting<Integer> timeMS = sgGeneral.add(new IntSetting.Builder()
        .name("waiting-time-in-ms")
        .description("???")
        .defaultValue(0)
        .min(0)
        .sliderMax(10000)
        .build()
    );
	
	private final Setting<Integer> tries = sgGeneral.add(new IntSetting.Builder()
        .name("packet-tries")
        .description("How many times shall this module send the packet")
        .defaultValue(1)
        .min(1)
        .sliderMax(10)
        .build()
    );
	
	private final Setting<Integer> tries_interval = sgGeneral.add(new IntSetting.Builder()
        .name("packet-tries-interval")
        .description("Time beetween retries in MS")
        .defaultValue(500)
        .min(1)
        .sliderMax(2000)
        .build()
    );
	
	final SettingGroup sgExtra = settings.createGroup("Visible");
	
	 private final Setting<Boolean> typefrommenu = sgExtra.add(new BoolSetting.Builder()
            .name("menu-prefab")
            .description("True=input 4 lines from meteor menu, False= from first sign")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<String> lineOne = sgExtra.add(new StringSetting.Builder()
            .name("line-one")
            .description("What to put on the first line of the sign.")
            .defaultValue("Steve")
			.visible(() -> typefrommenu.get())
            .build()
    );

    private final Setting<String> lineTwo = sgExtra.add(new StringSetting.Builder()
            .name("line-two")
            .description("What to put on the second line of the sign.")
            .defaultValue("did")
			.visible(() -> typefrommenu.get())
            .build()
    );

    private final Setting<String> lineThree = sgExtra.add(new StringSetting.Builder()
            .name("line-three")
            .description("What to put on the third line of the sign.")
            .defaultValue("nothing")
			.visible(() -> typefrommenu.get())
            .build()
    );

    private final Setting<String> lineFour = sgExtra.add(new StringSetting.Builder()
            .name("line-four")
            .description("What to put on the Fourth line of the sign.")
            .defaultValue("wrong.")
			.visible(() -> typefrommenu.get())
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
		if (!(typefrommenu.get()))
			_text = ((UpdateSignC2SPacket) event.packet).getText();
		else
			_text = null;
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(event.screen instanceof SignEditScreen) || (_text == null && !(typefrommenu.get())) ) return;

        event_ = event;
		///try {Thread.sleep(timeMS.get());}catch(InterruptedException e){		}
		
		for(int i = 0; i < tries.get(); ++i){
			setTimeout(this::DoTheThing,timeMS.get() + tries_interval.get() * i);
		}
		/// sign.getTextOnRow
				
		event.cancel();
        
    }
	
	
	private void DoTheThing(){
		SignBlockEntity sign = ((AbstractSignEditScreenAccessor) event_.screen).getSign();
		if (typefrommenu.get())
			mc.player.networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), lineOne.get(),lineTwo.get(),lineThree.get(),lineFour.get()));
		else
			mc.player.networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), _text[0], _text[1], _text[2], _text[3]));
        event_.cancel();
	}
	
	
}


