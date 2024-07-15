package com.example.addon.modules;

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

import meteordevelopment.meteorclient.settings.IntSetting;

public class AutoBuild extends Module {
    public enum build {Portal, Penis, heart, Bunker, Platform, Wither, Swastika, Highway;}
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean place, toggle;
    private final SettingGroup sgMisc = settings.createGroup("Misc");
    private final Setting<AutoBuild.build> buildMod = sgMisc.add(new EnumSetting.Builder<AutoBuild.build>().name("build").description(" ").defaultValue(AutoBuild.build.Penis).build());
    private final Setting<Boolean> rotate = sgMisc.add(new BoolSetting.Builder().name("rotate").description(" ").defaultValue(true).build());
    private final Setting<Boolean> togg = sgMisc.add(new BoolSetting.Builder().name("auto-toggle").description(" ").defaultValue(true).build());
	
	private final Setting<Integer> delay = sgMisc.add(new IntSetting.Builder()
        .name("delay")
        .description("delay in ticks")
        .defaultValue(10)
        .range(0, 1000)
        .sliderRange(0, 100)
        .build()
    );
	
	private int dx = 1;
	private int dz = 1;
	
	private int mode = 0;
	
	int timer = 0;
	
    @EventHandler
    private void onTick(TickEvent.Pre event) {
        place = false;
		
		timer += 1;
		if (timer <= delay.get()) return;
		
        dx = (mode / 2) * 2 - 1;
		dz = (mode % 2) * 2 - 1;
		
            
		if (place) return;
		b(2, 0, 0);
		if (place) return;
		b(2, 1, 0);
		if (place) return;
		b(2, 1, 1);
		if (place) return;
		b(2, 1, -1);
		if (place) return;
		h(2, 2, 0);
		if (place) return;
		h(2, 2, 1);
		if (place) return;
		h(2, 2, -1);
		
		mode += 1;
		mode %= 4;
		
		timer = 0;
		
		if (togg.get()) {
			ChatUtils.info("Done");
			place = false;
			toggle();
		}
		
            

            


        
    }

    public AutoBuild() {super(Addon.CATEGORY, "auto-build", " ");}
    
	
	private boolean p(int x, int y, int z) {
        clearBlockPosition(x, y, z);BlockState blockState = mc.world.getBlockState(blockPos);
        if (!blockState.isReplaceable()) return true;
        if (BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.OBSIDIAN), rotate.get(), 100)) {
            place = true;
        }
        return false;
    }

    private boolean b(int x, int y, int z) {
		
		x *= dx;
		if (dz == -1){ // bruh swap. Java is good!
			x = x + z;  
			z = x - z;  
			x = x - z;  
		}
			
		
        clearBlockPosition(x, y, z);BlockState blockState = mc.world.getBlockState(blockPos);
        if (!blockState.isReplaceable()) return true;
        if (BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.SOUL_SAND), rotate.get(), 100)) {
            place = true;
        }
        return false;
    }

    private boolean h(int x, int y, int z) {
		
		x *= dx;
		if (dz == -1){ // bruh swap. Java is good!
			x = x + z;  
			z = x - z;  
			x = x - z;  
		}
		
        clearBlockPosition(x, y, z);BlockState blockState = mc.world.getBlockState(blockPos);
        if (!blockState.isReplaceable()) return true;
        if (BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.WITHER_SKELETON_SKULL), rotate.get(), 100)) {
            place = true;
        }
        return false;
    }

    private void clearBlockPosition(int x, int y, int z) {blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);}
}