package com.example.addon.utils;

import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
public class Vec3Util {
    
	public static Vec3i Vec3dToVec3i(Vec3d vc){
		return new Vec3i( (int) Math.round(vc.x), (int) Math.round(vc.y), (int) Math.round(vc.z));
	}
	
	
	public static Vec3d Vec3iToVec3d(Vec3i vc){
		return new Vec3d( (vc.getX()), (vc.getY()), (vc.getZ()));
	}
}
