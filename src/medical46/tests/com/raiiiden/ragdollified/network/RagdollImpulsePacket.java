package com.raiiiden.ragdollified.network;
public class RagdollImpulsePacket {public static int sent;public static Object impulse;public static void broadcastOrdered(Object level,Object sender,int id,int part,Object v,Object hit,float damage){if(id!=7||part!=0||damage!=8)throw new AssertionError("unexpected corpse hit");sent++;impulse=v;}}
