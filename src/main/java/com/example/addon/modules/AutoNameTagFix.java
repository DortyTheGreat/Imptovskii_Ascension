
package com.example.addon.modules;

import com.example.addon.Addon;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.Set;

/*

Это лишь фикс, который добавили в 0.5.7 метеоре, а т.к. я делал мод для 0.5.4, то мне проще добавить этот фикс в свой
аддон, чем переустанавливать метеор.

UPD: на самом деле в данный модуль СЛЕДУЕТ запихнуть delay, иначе начинается мы можем тыкать по 2-3 бирки, пока клиент не успеет синхронизировать с сервером и мы будем тратить ~2x бирок,
а если пинг ещё и огромный, то может и в 5 раз больше

*/

public class AutoNameTagFix extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Which entities to nametag.")
        .build()
    );

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The maximum range an entity can be to be nametagged.")
        .defaultValue(5)
        .min(0)
        .sliderMax(6)
        .build()
    );

	private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("delay in ticks")
        .defaultValue(5)
        .range(0, 1000)
        .sliderRange(0, 20)
        .build()
    );
	
    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
        .name("priority")
        .description("Priority sort")
        .defaultValue(SortPriority.LowestDistance)
        .build()
    );

    private final Setting<Boolean> renametag = sgGeneral.add(new BoolSetting.Builder()
        .name("renametag")
        .description("Allows already nametagged entities to be renamed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically faces towards the mob being nametagged.")
        .defaultValue(true)
        .build()
    );

    private Entity target;
    private boolean offHand;

    public AutoNameTagFix() {
        super(Addon.CATEGORY, "auto-nametag-fix", "Automatically uses nametags on entities without a nametag. WILL nametag ALL entities in the specified distance.");
    }
	
	private int timer = 0;
	
    @EventHandler
    private void onTick(TickEvent.Pre event) {
        // Nametag in hobar
        
		timer += 1;
		if (timer <= delay.get()) return;
		timer = 0;
		
		FindItemResult findNametag = InvUtils.findInHotbar(Items.NAME_TAG);

        if (!findNametag.found()) {
            error("No Nametag in Hotbar");
            toggle();
            return;
        }


        // Target
        target = TargetUtils.get(entity -> {
            if (!PlayerUtils.isWithin(entity, range.get())) return false;
            if (!entities.get().contains(entity.getType())) return false;
            if (entity.hasCustomName()) {
                return renametag.get() && !entity.getCustomName().equals(mc.player.getInventory().getStack(findNametag.slot()).getName());
            }
            return true;
        }, priority.get());

        if (target == null) return;


        // Swapping slots
        InvUtils.swap(findNametag.slot(), true);

        offHand = findNametag.isOffhand();


        // Interaction
        if (rotate.get()) Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target), -100, this::interact);
        else interact();
    }

    private void interact() {
        mc.interactionManager.interactEntity(mc.player, target, offHand ? Hand.OFF_HAND : Hand.MAIN_HAND);
        InvUtils.swapBack();
    }
}