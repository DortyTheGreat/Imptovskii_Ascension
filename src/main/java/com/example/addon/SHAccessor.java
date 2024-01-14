package com.example.addon;

import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.village.*;
import net.minecraft.entity.player.PlayerInventory;

public class SHAccessor extends MerchantScreenHandler{
	public boolean PublicinsertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast){
		return insertItem(stack, startIndex, endIndex, fromLast);
	}
	
	public SHAccessor(int syncId, PlayerInventory playerInventory, Merchant merchant){
		super(syncId, playerInventory, merchant);
	}
	
};