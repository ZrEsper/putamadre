from pathlib import Path
r=Path(__file__).resolve().parent.parent/'build/fixture-src'
sources={
'net/minecraft/client/Minecraft.java':'''package net.minecraft.client;public class Minecraft{static final Minecraft INSTANCE=new Minecraft();public Object level,player,particleEngine;public static Minecraft getInstance(){return INSTANCE;}}''',
'net/minecraft/core/BlockPos.java':'''package net.minecraft.core;public record BlockPos(int x,int y,int z){public static BlockPos containing(double x,double y,double z){return new BlockPos((int)Math.floor(x),(int)Math.floor(y),(int)Math.floor(z));}}''',
'net/minecraft/core/particles/ParticleTypes.java':'''package net.minecraft.core.particles;public class ParticleTypes{public static final Object ELECTRIC_SPARK=new Object(),FLAME=new Object();}''',
'net/minecraft/world/level/block/LightBlock.java':'''package net.minecraft.world.level.block;public class LightBlock{public static final Object LEVEL=new Object();}''',
'net/minecraft/world/level/block/Blocks.java':'''package net.minecraft.world.level.block;public class Blocks{public static final Light LIGHT=new Light();public static class Light{public dev.tactical.tacz.EffectsChecks.State defaultBlockState(){return new dev.tactical.tacz.EffectsChecks.State("light",0);}}}''',
'dev/tactical/tacz/Settings.java':'''package dev.tactical.tacz;public class Settings{public static boolean LIGHTS=true;public static int IMPACT=11,IMPACT_TICKS=5,MAX_LIGHTS=16,LIGHT_RADIUS=48,MUZZLE=14,MUZZLE_TICKS=4;}''',
'dev/tactical/tacz/ShotTrails.java':'''package dev.tactical.tacz;public class ShotTrails{public static void cleanup(){}}''',
'com/tacz/guns/client/particle/BulletHoleParticle.java':'''package com.tacz.guns.client.particle;public class BulletHoleParticle{public int age,lifetime;public float alpha,quadSize=.09f;public boolean alive=true;public BulletHoleParticle(Object world,double x,double y,double z,Object face,Object pos,String ammo,String gun,String display){dev.tactical.tacz.ClientEffects.impact(world,x,y,z,face);dev.tactical.tacz.ImpactMarks.remember(this,world,x,y,z,face,pos,ammo,gun,display);}public boolean isAlive(){return alive;}public void remove(){alive=false;}}'''
}
for n,s in sources.items():p=r/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
