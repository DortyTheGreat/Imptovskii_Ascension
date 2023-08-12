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

import meteordevelopment.meteorclient.utils.player.Rotations;
/**

What's new in Dorty Edition?

- Speed Control

*/

public class Rocket extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Boolean> legacyVersion = sgGeneral.add(new BoolSetting.Builder()
            .name("legacy-Version")
            .description("""
                    Works as before 12.08.2023 patch""")
            .defaultValue(false)
            .build()
    );
	
	private final Setting<Double> acceleration = sgGeneral.add(new DoubleSetting.Builder()
        .name("acceleration")
        .description("Acceleration value for speedCtrl. 1 - same as default rejects")
        .defaultValue(2.0) // 2 is limit for np
        .min(0.1)
        .sliderMax(10.0)
        .build()
    );
	
	private final Setting<Double> maxSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-speed")
        .description("Maximum Speed for SpeedCTRL")
        .defaultValue(39.0)
        .min(0.0)
        .sliderMax(100)
        .build()
    );
	
	private final Setting<Double> pitchFocus = sgGeneral.add(new DoubleSetting.Builder()
        .name("Pitch-Focus")
        .description("Pitch to focus on")
        .defaultValue(-4.2)
        .min(-181)
        .sliderMax(181)
        .build()
    );
	
	private final Setting<Integer> Priority = sgGeneral.add(new IntSetting.Builder()
        .name("Roll-Focus")
        .description("I have no idea what that is")
        .defaultValue(50)
        .min(-100000)
        .sliderMax(100000)
        .build()
    );
	
	

   

    private final Setting<Boolean> stopInWater = sgGeneral.add(new BoolSetting.Builder()
            .name("stop-in-water")
            .description("Stop flying in water")
            .defaultValue(false)
            .build()
    );

    

    public Rocket() {
        super(Addon.CATEGORY, "Rocket", "Fly like a freaking rocket!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem() != Items.ELYTRA)
            return;

        if (mc.player.isFallFlying()) {
            if (stopInWater.get() && mc.player.isTouchingWater()) {
                sendStartStopPacket();
                return;
            }

            controlSpeed();
            return;
        }
    }

    private void sendStartStopPacket() {
        ClientCommandC2SPacket packet = new ClientCommandC2SPacket(mc.player,
                ClientCommandC2SPacket.Mode.START_FALL_FLYING);
        mc.player.networkHandler.sendPacket(packet);
    }

   

    private void controlSpeed() {
     

        float yaw = (float) Math.toRadians(mc.player.getYaw());
        

		Rotations.rotate(mc.player.getYaw(), pitchFocus.get(), Priority.get());
		
		Vec3d forward = new Vec3d(-MathHelper.sin(yaw) * 0.05 * acceleration.get(), 0, /* for the legacy mode*/
            MathHelper.cos(yaw) * 0.05 * acceleration.get());
		
		Vec3d forward_10_percent = new Vec3d(-MathHelper.sin(yaw) * 0.05 * 0.1 * acceleration.get(), 0,
            MathHelper.cos(yaw) * 0.05 * 0.1 * acceleration.get());
		
        Vec3d v = mc.player.getVelocity();
		
		Vec3d c = new Vec3d(0,0,0);
		
		
		
		double _maxSpeed = maxSpeed.get() / 20.0d;
		
		if ( v.distanceTo(c) <= _maxSpeed)
            mc.player.setVelocity(v.add(forward));
		
		if (legacyVersion.get()){
			v = mc.player.getVelocity();
			if (mc.options.backKey.isPressed() || v.distanceTo(c) > _maxSpeed)
				mc.player.setVelocity( (v.subtract(forward)) );
		}else{
			/// bruh moment, I hate geometry
			
			for(int i = 0; i < 10; ++i){
				v = mc.player.getVelocity();
				if (mc.options.backKey.isPressed() || v.distanceTo(c) > _maxSpeed)
					mc.player.setVelocity( (v.subtract(forward_10_percent)) );
			}
			
			
		}
		
		
        
    }

    
}