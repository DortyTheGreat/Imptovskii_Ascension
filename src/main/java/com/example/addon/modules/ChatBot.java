package com.example.addon.modules;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.LinkedList;
import java.util.Queue;

import meteordevelopment.meteorclient.events.world.TickEvent;

import meteordevelopment.meteorclient.settings.IntSetting;

import com.example.addon.Addon;
import com.example.addon.settings.StringMapSetting;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.StarscriptError;


import meteordevelopment.meteorclient.events.packets.PacketEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChatBot extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder()
            .name("prefix")
            .description("Command prefix for the bot.")
            .defaultValue("!")
            .build()
    );

    private final Setting<Boolean> help = sgGeneral.add(new BoolSetting.Builder()
            .name("help")
            .description("Add help command.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Map<String, String>> commands = sgGeneral.add(new StringMapSetting.Builder()
            .name("commands")
            .description("Commands.")
            .renderer(StarscriptTextBoxRenderer.class)
            .defaultValue(new LinkedHashMap<>() {{
                put("ping", "Pong!");
                put("tps", "Current TPS: {server.tps}");
                put("time", "It's currently {server.time}");
                //put("pos", "I am @ {player.pos}");
				
				put("web", "https://new-places.ru/");
				put("vk", "https://vk.com/newplacesanarchy"); 
				put("donate", "https://new-places.ru/donate");
				put("map", "https://spawn.new-places.ru/");
				put("wiki", "https://wiki.new-places.ru/");
				put("ds", "https://discord.new-places.ru/");
				put("discord", "https://discord.new-places.ru/");
				put("yt", "https://www.youtube.com/channel/UC8aH-rBeqYI92zeiDAHdGFg");
				put("YT", "https://www.youtube.com/channel/UC8aH-rBeqYI92zeiDAHdGFg");
				put("youtube", "https://www.youtube.com/channel/UC8aH-rBeqYI92zeiDAHdGFg");
				put("github", "https://github.com/imptovskii");
				put("faq", "https://www.new-places.ru/faq/");
				put("FAQ", "https://www.new-places.ru/faq/");
				
				put("end", "nether - 100 200");
				
				put("seed", "9172939576280861554 - over seed.");
				
				
            }})
            .build()
    );	
	
	private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay before send text in ticks (20 ticks = 1 sec).")
        .defaultValue(60)
        .range(1, 240)
        .sliderRange(1, 40)
        .build()
    );
	
	private Queue<String> queue = new LinkedList<>();
	private int timer = 0;
	
	@Override
    public void onActivate() {
		timer = 0;
	}
	
	@EventHandler
    private void onTick(TickEvent.Post event) {
        //if (serverOnly.get() && mc.getServer() != null && mc.getServer().isSingleplayer()) return;
		
		
		
        if ( timer < delay.get() ) {
			timer ++;
			return;
		}
		timer = 0;
		
		String msg;
		if ((msg = queue.poll()) != null)
			ChatUtils.sendPlayerMsg(msg);
	}
	
    public ChatBot() {
        super(Addon.CATEGORY, "chat-bot-cooler", "Bot which automatically responds to chat messages, but better");
    }

    @EventHandler
    private void onMessageRecieve(ReceiveMessageEvent event) {
        String msg = event.getMessage().getString();
        if (help.get() && msg.endsWith(prefix.get() + "help")) {
            queue.offer("Available commands: " + String.join(", ", commands.get().keySet()));
			
            return;
        }
        for (String cmd : commands.get().keySet()) {
            if (!msg.endsWith(prefix.get() + cmd)) continue;
			
			Script script = compile(commands.get().get(cmd));
			if (script == null) queue.offer("An error occurred");
			try {
				var section = MeteorStarscript.ss.run(script);
				queue.offer(section.text);
			} catch (StarscriptError e) {
				MeteorStarscript.printChatError(e);
				queue.offer("An error occurred");
			}
			return;
            
        }
    }

    private static Script compile(String script) {
        if (script == null) return null;
        Parser.Result result = Parser.parse(script);
        if (result.hasErrors()) {
            MeteorStarscript.printChatError(result.errors.get(0));
            return null;
        }
        return Compiler.compile(result);
    }
	
	
	@EventHandler
    private void onPacketSent(PacketEvent.Send event) {


        if (event.packet instanceof ChatMessageC2SPacket packet) {
            info("I sent a packet");
			timer = 0;
        }
    }
}