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

public class ElytraStop extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();



  
	



    public ElytraStop() {
        super(Addon.CATEGORY, "Elytra-Stop", "Stop Mid-Air");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        ItemStack chest = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.getItem() != Items.ELYTRA)
            return;

        if (mc.player.isFallFlying()) {
			mc.player.setVelocity(0,0,0);
            //controlSpeed();
            //controlHeight();
            return;
        }


    }

    private void sendStartStopPacket() {
        ClientCommandC2SPacket packet = new ClientCommandC2SPacket(mc.player,
                ClientCommandC2SPacket.Mode.START_FALL_FLYING);
        mc.player.networkHandler.sendPacket(packet);
    }

}