package dev.zomboidmelee.boxing;
import java.util.*;import net.minecraft.client.model.HumanoidModel;
/** Staggered, loose recovery: shoulders rise before the pelvis, one knee follows the other. */
public final class Rise21{
 static final Map<Object,Fall> FALLS=new WeakHashMap<>();static final long RISE=1_400_000_000L;
 static final class Fall{boolean down;double from,to;long changed;}
 static double blend(Fall f,long now){return f.from+(f.to-f.from)*Motion20.smooth((now-f.changed)/(double)(f.down?360_000_000L:RISE));}
 static Fall update(Object v,long now){boolean down=Stomp18.temporary(v);Fall f=FALLS.get(v);if(f==null){if(!down)return null;f=new Fall();f.down=true;f.to=1;f.changed=now;FALLS.put(v,f);}if(f.down!=down){f.from=blend(f,now);f.to=down?1:0;f.down=down;f.changed=now;}if(!down&&blend(f,now)<.001){FALLS.remove(v);return null;}return f;}
 public static void prone(Object v,Object pose){long now=BoxingClient16.CLOCK.getAsLong();Fall f=update(v,now);if(f==null)return;double b=blend(f,now),scale=Stomp18.temporary(v)?Stomp18.fallenScale(v):Math.max(.1,R.num(R.call(v,"getBbWidth"))/.6);double sign=Fall24.tilt(v);BoxingClient16.rotate(pose,"YP",Fall24.turn(v)*b);R.call(pose,"translate",0d,.22*scale*b,-.80*scale*b*sign);BoxingClient16.rotate(pose,"XP",90*b*sign);double seconds=(now-f.changed)/1e9;if(f.down){if(seconds>.28&&seconds<.8)BoxingClient16.rotate(pose,"ZP",Math.sin((seconds-.28)*18)*Math.exp(-(seconds-.28)*10)*1.4);}else{double q=Math.min(1,seconds/1.4);BoxingClient16.rotate(pose,"ZP",Math.sin(q*Math.PI*2)*3*Math.sin(q*Math.PI));}}
 public static void pose(Object model,Object v){if(!(model instanceof HumanoidModel<?>m))return;long now=BoxingClient16.CLOCK.getAsLong();Fall f=update(v,now);if(f==null)return;double b=blend(f,now),q=Math.max(0,Math.min(1,(now-f.changed)/(double)RISE)),fold=f.down?0:Math.sin(q*Math.PI),lag=f.down?0:Math.sin(Math.max(0,(q-.13)/.87)*Math.PI);
 double sign=Fall24.tilt(v);m.rightArm.xRot=(float)(-.8*b*sign-.40*fold);m.rightArm.yRot=(float)(-.25*b);m.rightArm.zRot=(float)(.45*b);m.leftArm.xRot=(float)(-.55*b*sign-.60*lag);m.leftArm.yRot=(float)(.20*b);m.leftArm.zRot=(float)(-.34*b);
 m.rightLeg.xRot=(float)(.25*b-.75*fold);m.rightLeg.zRot=(float)(.1*b);m.leftLeg.xRot=(float)(-.12*b-.46*lag);m.leftLeg.zRot=(float)(-.08*b);if(m.body!=null){m.body.xRot+=(float)(.45*fold);m.body.yRot+=(float)(.09*lag);}if(m.head!=null)m.head.xRot-=(float)(.22*lag);
 }
}
