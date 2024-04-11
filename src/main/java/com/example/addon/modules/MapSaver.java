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

/**



*/

public class MapSaver extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    
	

	
	private final Setting<Integer> maxMaps = sgGeneral.add(new IntSetting.Builder()
        .name("max-maps")
        .description("Maximum amount of brute-force maps")
        .defaultValue(1000)
        .range(1, 1000*1000*1000)
        .sliderRange(1, 100*1000)
        .build()
    );
	
	
	private final Setting<Integer> mapsPerTick = sgGeneral.add(new IntSetting.Builder()
        .name("maps-per-tick")
        .description("Maximum amount of brute-force maps done in one tick")
        .defaultValue(10)
        .range(1, 1000)
        .sliderRange(1, 100)
        .build()
    );
	
	private final Setting<Boolean> debugPrint = sgGeneral.add(new BoolSetting.Builder()
        .name("debug-print")
        .description("prints messages for debugin this bit")
        .defaultValue(true)
        .build()
    );
	
	
	
	private int start_map = 0;
	
	final Color[] BaseMapColors = new Color[]
	{
		new Color(  0,   0,   0,   0),
		new Color(127, 178,  56, 255),
		new Color(247, 233, 163, 255),
		new Color(199, 199, 199, 255),
		new Color(255,   0,   0, 255),
		new Color(160, 160, 255, 255),
		new Color(167, 167, 167, 255),
		new Color(  0, 124,   0, 255),
		new Color(255, 255, 255, 255),
		new Color(164, 168, 184, 255),
		new Color(151, 109,  77, 255),
		new Color(112, 112, 112, 255),
		new Color( 64,  64, 255, 255),
		new Color(143, 119,  72, 255),
		new Color(255, 252, 245, 255),
		new Color(216, 127,  51, 255),
		new Color(178,  76, 216, 255),
		new Color(102, 153, 216, 255),
		new Color(229, 229,  51, 255),
		new Color(127, 204,  25, 255),
		new Color(242, 127, 165, 255),
		new Color( 76,  76,  76, 255),
		new Color(153, 153, 153, 255),
		new Color( 76, 127, 153, 255),
		new Color(127,  63, 178, 255),
		new Color( 51,  76, 178, 255),
		new Color(102,  76,  51, 255),
		new Color(102, 127,  51, 255),
		new Color(153,  51,  51, 255),
		new Color( 25,  25,  25, 255),
		new Color(250, 238,  77, 255),
		new Color( 92, 219, 213, 255),
		new Color( 74, 128, 255, 255),
		new Color(  0, 217,  58, 255),
		new Color(129,  86,  49, 255),
		new Color(112,   2,   0, 255),
		new Color(209, 177, 161, 255),
		new Color(159,  82,  36, 255),
		new Color(149,  87, 108, 255),
		new Color(112, 108, 138, 255),
		new Color(186, 133,  36, 255),
		new Color(103, 117,  53, 255),
		new Color(160,  77,  78, 255),
		new Color( 57,  41,  35, 255),
		new Color(135, 107,  98, 255),
		new Color( 87,  92,  92, 255),
		new Color(122,  73,  88, 255),
		new Color( 76,  62,  92, 255),
		new Color( 76,  50,  35, 255),
		new Color( 76,  82,  42, 255),
		new Color(142,  60,  46, 255),
		new Color( 37,  22,  16, 255),
		new Color(189,  48,  49, 255),
		new Color(148,  63,  97, 255),
		new Color( 92,  25,  29, 255),
		new Color( 22, 126, 134, 255),
		new Color( 58, 142, 140, 255),
		new Color( 86,  44,  62, 255),
		new Color( 20, 180, 133, 255),
		new Color(100, 100, 100, 255),
		new Color(216, 175, 147, 255),
		new Color(127, 167, 150, 255),
		
		/// NEW COLORS ????
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0),
		new Color(0, 0, 0, 0)
		
	};
		
	private byte[] r = new byte[BaseMapColors.length*4],
	   g = new byte[BaseMapColors.length*4],
	   b = new byte[BaseMapColors.length*4],
	   a = new byte[BaseMapColors.length*4];
	

    private int jumpTimer;

    @Override
    public void onActivate() {
		
		/// https://github.com/LB--/MCModify/blob/java/src/main/java/com/lb_stuff/mcmodify/minecraft/Map.java
		
		Color[] MapColors = new Color[BaseMapColors.length*4];
		for(int i = 0; i < BaseMapColors.length; ++i)
		{
			Color bc = BaseMapColors[i];
			MapColors[i*4 +0] = new Color((int)(bc.getRed()*180.0/255.0+0.5), (int)(bc.getGreen()*180.0/255.0+0.5), (int)(bc.getBlue()*180.0/255.0+0.5), bc.getAlpha());
			MapColors[i*4 +1] = new Color((int)(bc.getRed()*220.0/255.0+0.5), (int)(bc.getGreen()*220.0/255.0+0.5), (int)(bc.getBlue()*220.0/255.0+0.5), bc.getAlpha());
			MapColors[i*4 +2] = bc;
			MapColors[i*4 +3] = new Color((int)(bc.getRed()*135.0/255.0+0.5), (int)(bc.getGreen()*135.0/255.0+0.5), (int)(bc.getBlue()*135.0/255.0+0.5), bc.getAlpha());
		}
		for(int i = 0; i < MapColors.length; ++i)
		{
			Color mc = MapColors[i];
			r[i] = (byte)mc.getRed();
			g[i] = (byte)mc.getGreen();
			b[i] = (byte)mc.getBlue();
			a[i] = (byte)mc.getAlpha();
		}
		
		start_map = 0;
		
    }
	
	@EventHandler
    private void onTick(TickEvent.Post event) {
		int maxi = Math.min(start_map + mapsPerTick.get(), maxMaps.get());
		for(int i = start_map; i < maxi; ++i){
			MapState mapState = FilledMapItem.getMapState(i, mc.player.getWorld());
			if ( !(mapState != null) ){
				///info("map " + i + " has no data");
				continue; /// I LOVE JAVA TYPES!
			}
		
			if (debugPrint.get()) info("map " + i + " has some data, saving ...");

			File screensDir = new File("map_screenshots");
			screensDir.mkdir();
			
			
			String fileSeparator = System.getProperty("file.separator");
        
			
			
			//pls Mojang make functions like these public so I don't have to use invokers
			File screenshot = new File("map_screenshots"+fileSeparator+i+".png");
			
			int test = mapState.colors[0];
			info("" + test);
			
			
			// Image file dimensions 
			int width = 128, height = 128; 
	  
			// Create buffered image object 
			BufferedImage img = null; 
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); 
			
			
			
			for (int y = 0; y < height; y++) 
			{ 
				for (int x = 0; x < width; x++) 
				{ 
					// generating values less than 256 
					int aa = Byte.toUnsignedInt(a[Byte.toUnsignedInt( mapState.colors[y*width + x])]); 
					int rr = Byte.toUnsignedInt(r[Byte.toUnsignedInt( mapState.colors[y*width + x])]); 
					int gg = Byte.toUnsignedInt(g[Byte.toUnsignedInt( mapState.colors[y*width + x])]);  
					int bb = Byte.toUnsignedInt(b[Byte.toUnsignedInt( mapState.colors[y*width + x])]);   
	  
					  //pixel 
					int p = (aa<<24) | (rr<<16) | (gg<<8) | bb;  
	  
					img.setRGB(x, y, p); 
				}
				
			}
			
			try
			{ 
				ImageIO.write(img, "png", screenshot);  
			} 
			catch(IOException e) 
			{ 
				System.out.println("Error: " + e); 
				info("Oops, something went wrong...");
			}
			// file object 
			
			
			
		}
		
		start_map = maxi;
		if (start_map == maxMaps.get()) toggle();
		
	}
	
    public MapSaver() {
        super(Addon.CATEGORY, "Map Saver", "Saves map data into files");
    }



    
}