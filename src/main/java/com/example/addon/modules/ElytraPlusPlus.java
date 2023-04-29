package com.example.addon.modules;

import meteordevelopment.meteorclient.settings.DoubleSetting;

import com.example.addon.Addon; // import anticope.rejects.MeteorRejectsAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
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

/**

What's new in Dorty Edition?

- Speed Control

*/

public class ElytraPlusPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> instantFly = sgGeneral.add(new BoolSetting.Builder()
            .name("instant-fly")
            .description("Jump to fly, no weird double-jump needed!")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> speedCtrl = sgGeneral.add(new BoolSetting.Builder()
            .name("speed-ctrl")
            .description("""
                    Control your speed with the Forward and Back keys.
                    (default: W and S)
                    No fireworks needed!""")
            .defaultValue(true)
            .build()
    );
	
	private final Setting<Double> acceleration = sgGeneral.add(new DoubleSetting.Builder()
        .name("acceleration")
        .description("Acceleration value for speedCtrl. 1 - same as default rejects")
        .defaultValue(1.0)
        .min(0.1)
        .sliderMax(10.0)
        .build()
    );
	
	private final Setting<Double> maxSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-speed")
        .description("Maximum Speed for SpeedCTRL")
        .defaultValue(35.0)
        .min(0.0)
        .sliderMax(100)
        .build()
    );
	
	private final Setting<Double> minPitch = sgGeneral.add(new DoubleSetting.Builder()
        .name("min-pitch")
        .description("Minimum pitch for SpeedCTRL")
        .defaultValue(-5.0)
        .min(-181)
        .sliderMax(181)
        .build()
    );
	
	

    private final Setting<Boolean> heightCtrl = sgGeneral.add(new BoolSetting.Builder()
            .name("height-ctrl")
            .description("""
                    Control your height with the Jump and Sneak keys.
                    (default: Spacebar and Shift)
                    No fireworks needed!""")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> stopInWater = sgGeneral.add(new BoolSetting.Builder()
            .name("stop-in-water")
            .description("Stop flying in water")
            .defaultValue(true)
            .build()
    );

    private int jumpTimer;

    @Override
    public void onActivate() {
        jumpTimer = 0;
    }

    public ElytraPlusPlus() {
        super(Addon.CATEGORY, "Elytra-Plus-Plus", "SuperCoolFlight");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (jumpTimer > 0)
            jumpTimer--;

        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem() != Items.ELYTRA)
            return;

        if (mc.player.isFallFlying()) {
            if (stopInWater.get() && mc.player.isTouchingWater()) {
                sendStartStopPacket();
                return;
            }

            controlSpeed();
            controlHeight();
            return;
        }

        if (ElytraItem.isUsable(chest) && mc.options.jumpKey.isPressed())
            doInstantFly();
    }

    private void sendStartStopPacket() {
        ClientCommandC2SPacket packet = new ClientCommandC2SPacket(mc.player,
                ClientCommandC2SPacket.Mode.START_FALL_FLYING);
        mc.player.networkHandler.sendPacket(packet);
    }

    private void controlHeight() {
        if (!heightCtrl.get())
            return;

        Vec3d v = mc.player.getVelocity();

        if (mc.options.jumpKey.isPressed())
            mc.player.setVelocity(v.x, v.y + 0.08, v.z);
        else if (mc.options.sneakKey.isPressed())
            mc.player.setVelocity(v.x, v.y - 0.04, v.z);
    }

    private void controlSpeed() {
        if (!speedCtrl.get())
            return;

        float yaw = (float) Math.toRadians(mc.player.getYaw());
        Vec3d forward = new Vec3d(-MathHelper.sin(yaw) * 0.05 * acceleration.get(), 0,
                MathHelper.cos(yaw) * 0.05 * acceleration.get());

        Vec3d v = mc.player.getVelocity();
		
		if (mc.options.forwardKey.isPressed() && (mc.player.getPitch() > minPitch.get()) )
            mc.player.setVelocity(v.add(forward));
        else if (mc.options.backKey.isPressed())
            mc.player.setVelocity(v.subtract(forward));
		
        v = mc.player.getVelocity();
		
		Vec3d c = new Vec3d(0,0,0);
		
		double _maxSpeed = maxSpeed.get() / 20.0d;
		
		if (v.distanceTo(c) > _maxSpeed ){
			mc.player.setVelocity(v.multiply( _maxSpeed / v.distanceTo(c) ));
		}
		
		
        
    }

    private void doInstantFly() {
        if (!instantFly.get())
            return;

        if (jumpTimer <= 0) {
            jumpTimer = 20;
            mc.player.setJumping(false);
            mc.player.setSprinting(true);
            mc.player.jump();
        }

        sendStartStopPacket();
    }
}