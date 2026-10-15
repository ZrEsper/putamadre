package dev.tactical.tacz;
/** Use the active stage's pose for camera-relative world vertices. */
public final class TracePose {
 public static Object matrix(Object poseStack)throws Exception{return Reflect.call(Reflect.call(poseStack,"last"),"pose");}
}
