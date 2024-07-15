package com.example.addon.modules;

import meteordevelopment.meteorclient.settings.DoubleSetting;

import com.example.addon.Addon; // import anticope.rejects.MeteorRejectsAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.map.MapState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.client.render.MapRenderer;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.File; 
import java.io.IOException; 
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO; 
import java.awt.Color;
import net.minecraft.world.biome.Biome;

import net.minecraft.world.biome.source.BiomeAccess;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkManager;

import net.minecraft.server.world.ServerWorld;

import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.registry.entry.RegistryEntry;

import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;
import meteordevelopment.starscript.value.Value;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import java.util.stream.Collectors;
/**



*/

public class GrassColorPeek extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final SettingGroup sgMisc = settings.createGroup("Misc");
	
	private final Setting<Boolean> AtCoords = sgMisc.add(new BoolSetting.Builder().name("AtCoords").description("LocateBlocks at given coords, otherwise only at player").defaultValue(true).build());
	
	private final Setting<Integer> X_coord = sgMisc.add(new IntSetting.Builder()
        .name("X coord")
        .description("Are you stupid?")
        .defaultValue(0)
        .range(-1000 * 1000 * 1000, 1000 * 1000 * 1000)
        .sliderRange(-1000 * 1000, 1000 * 1000)
		.visible((AtCoords::get))
        .build()
    );
	
	private final Setting<Integer> Z_coord = sgMisc.add(new IntSetting.Builder()
        .name("Z coord")
        .description("Are you stupid?")
        .defaultValue(0)
        .range(-1000 * 1000 * 1000, 1000 * 1000 * 1000)
        .sliderRange(-1000 * 1000, 1000 * 1000)
		.visible((AtCoords::get))
        .build()
    );
	
	
    @Override
    public void onActivate() {
		
    }
	

	
	public int get_Biome_int(int x, int y, int z){
		
		try {
			/// world == null ??
			ServerChunkManager SCM = mc.getServer().getOverworld().getChunkManager();
			
			info("seed " + mc.getServer().getOverworld().getSeed());
			
			info (mc.getServer().getOverworld().getChunk(X_coord.get() / 16, Z_coord.get() / 16);
			
			if (!(SCM.getChunkGenerator().getBiomeSource() instanceof MultiNoiseBiomeSource MNBS)){ 
				info("Well..");
				return -2281;
			
			}
			//BiomeSource BS = SCM.getChunkGenerator().getBiomeSource();
			
			///info(MNBS.getBiome(x,y,z, SCM.getNoiseConfig().getMultiNoiseSampler()).getIdAsString());
			
			Biome Bio = MNBS.getBiome(x,y,z, SCM.getNoiseConfig().getMultiNoiseSampler()).value();
			
			Identifier id = mc.world.getRegistryManager().get(RegistryKeys.BIOME).getId(Bio);
			
			//MultiNoiseBiomeSource Sampler = SCM.getNoiseConfig().getMultiNoiseSampler();
			
			//Biome Bio = Sampler.getBiome(x,y,z);
			info(Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" ")));
			info(Bio.toString());
			
			return Bio.getFoliageColor();
		} catch (Exception e) {
			info("Oops...");
			///info(mc.getServer());
			info(e.toString());
			return -228;
		}
		
		
	}
	
	@EventHandler
    private void onTick(TickEvent.Post event) {
		
		///if (!(mc.serverWorld.getChunkManager() instanceof ServerChunkManager SCM)){ 
		
		/// NoiseConfig -> MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler()
		
		
		
		int biome_int = -1337;
		
		
		int x,y,z;
		y = (int) mc.player.getY();
		if (AtCoords.get()){
			z = Z_coord.get();
			x = X_coord.get();
			///biome_int = mc.world.getBiomeAccess().getBiomeForNoiseGen(X_coord.get(), mc.player.getY(), Z_coord.get()).value().getFoliageColor();
		}else{
			z = (int) mc.player.getZ();
			x = (int) mc.player.getX();
			///biome_int = mc.world.getBiomeAccess().getBiomeForNoiseGen(mc.player.getX(), mc.player.getY(), mc.player.getZ()).value().getFoliageColor();
		}
		
		biome_int = get_Biome_int(x,y,z);
		
		info( " " +   biome_int );
		info(" " + (biome_int == -13911281));
	}
	
    public GrassColorPeek() {
        super(Addon.CATEGORY, "GrassColorPeek", "Peeks Color of grass :-)");
    }



    
}