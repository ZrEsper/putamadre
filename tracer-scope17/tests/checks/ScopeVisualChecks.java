package checks;
import java.util.*;import dev.tactical.tacz.ScopeZoom;import dev.tactical.tacz.ScopeLimits;import dev.zomboid.tacz.Visual17;import net.minecraft.client.Minecraft;import net.minecraft.resources.ResourceLocation;import com.tacz.guns.api.TimelessAPI;import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
public class ScopeVisualChecks {
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static class Pose{public Object cameraMatrix=new Object();public Pose last(){return this;}public Object pose(){return cameraMatrix;}}
 public static class Index{public float[] zoom={1};public float[] getZoom(){return ScopeZoom.scale(this,zoom);}}
 public static class Gun implements com.tacz.guns.api.item.IGun{public int selected;public String scope="red_dot",builtin="empty";public float getAimingZoom(Object stack){float[] z=TimelessAPI.index.getZoom();return z==null||z.length==0?1:z[selected%z.length];}public Object getGunId(Object s){return "test:gun";}public Object getAttachmentId(Object s,Object t){return scope;}public Object getBuiltInAttachmentId(Object s,Object t){return builtin;}}
 public static class Stack{public Gun gun=new Gun();public Gun getItem(){return gun;}}
 public static class Player{public Stack stack=new Stack();public com.tacz.guns.api.entity.IGunOperator operator=new com.tacz.guns.api.entity.IGunOperator();public String message;public Stack getMainHandItem(){return stack;}public void displayClientMessage(Object text,boolean b){message=text.toString();}}
 public static class Textures{public ResourceLocation atlas;public Atlas getTexture(ResourceLocation id){atlas=id;return new Atlas(id);}}
 public record Sprite(ResourceLocation atlas,ResourceLocation sprite){}
 public record Atlas(ResourceLocation id){public Sprite getSprite(ResourceLocation sprite){return new Sprite(id,sprite);}}
 public static class Particle{private float quadSize=.1f,alpha;private int lifetime;private Sprite sprite;protected void setSprite(Sprite s){sprite=s;}}
 public static class BulletData{public int amount=1;public int getBulletAmount(){return amount;}}
 public static class GunInfo{public String type="pistol";public BulletData bullet=new BulletData();public String getType(){return type;}public BulletData getBulletData(){return bullet;}}
 static void wheel(double delta){ScopeZoom.scroll(new MouseScrollingEvent(delta));}
 public static void main(String[]a)throws Exception{
  Minecraft mc=Minecraft.getInstance();Gun gun=mc.player.stack.gun;
  for(int i=0;i<100;i++)wheel(12);ok(gun.getAimingZoom(mc.player.stack)==1,"red dot cannot become 27.7x");ok(mc.player.message.contains("1.0× / 1.0×"),"HUD reports physical maximum");
  gun.scope="variable25";TimelessAPI.index=new Index();TimelessAPI.index.zoom=new float[]{4,12,25};for(int i=0;i<50;i++)wheel(1);ok(gun.getAimingZoom(mc.player.stack)==25,"25x cap");wheel(-1);ok(gun.getAimingZoom(mc.player.stack)<25,"zoom out immediately from cap");for(int i=0;i<50;i++)wheel(-12);ok(gun.getAimingZoom(mc.player.stack)==4,"declared lower bound");
  gun.selected=2;wheel(-1);ok(gun.getAimingZoom(mc.player.stack)>=4&&gun.getAimingZoom(mc.player.stack)<=25,"native index switch remains bounded");
  gun.scope="fixed6";gun.selected=0;TimelessAPI.index=new Index();TimelessAPI.index.zoom=new float[]{6};wheel(-12);ok(gun.getAimingZoom(mc.player.stack)==6,"fixed optics remain fixed");
  gun.scope="empty";gun.builtin="builtin8";TimelessAPI.index=new Index();TimelessAPI.index.zoom=new float[]{2,8};for(int i=0;i<20;i++)wheel(12);ok(gun.getAimingZoom(mc.player.stack)==8,"built-in optic uses own maximum");
  mc.screen=new Object();MouseScrollingEvent blocked=new MouseScrollingEvent(1);ScopeZoom.scroll(blocked);ok(!blocked.canceled,"inventory scroll unaffected");mc.screen=null;
  mc.player.operator.progress=.1;blocked=new MouseScrollingEvent(1);ScopeZoom.scroll(blocked);ok(!blocked.canceled,"not aiming leaves hotbar wheel");mc.player.operator.progress=.9;
  for(float maximum:new float[]{1,2,4,6,8,12,16,25,32,64,100})for(double factor:new double[]{0,.01,.5,1,1.35,4,100,Double.NaN}){float[] raw={1,maximum};float[] bounded=ScopeLimits.scaled(raw,factor);for(float v:bounded)ok(v>=1&&v<=maximum,"every scaled mode within declared bounds");ok(raw[1]==maximum,"raw attachment data not mutated");}
  ok(dev.zomboid.tacz.ScopeZoom.magnification(25)==25,"third person removes hidden 1.35 multiplier");ok(dev.zomboid.tacz.ScopeZoom.magnification(1)==1,"red dot stays 1x");ok(dev.zomboid.tacz.ScopeZoom.magnification(100)==100,"legitimate scopes are not limited by old global 32x cap");
  for(String[] args:new String[][]{{"tacz:9mm","pistol","pistol"},{"tacz:556x45","rifle","rifle"},{"tacz:762x51","sniper","rifle"},{"tacz:50bmg","sniper","heavy"},{"tacz:12g","shotgun","shotgun"},{"custom:unknown","smg","pistol"}}){TimelessAPI.info.type=args[1];Particle p=new Particle();Visual17.apply(p,args[0],"tacz:test_gun");ok(p.sprite.atlas.value().equals("minecraft:textures/atlas/particles.png"),"UV source uses particle atlas");ok(p.sprite.sprite.value().equals("tacz:bullet_hole_"+args[2]),"ammo-aware mark selection");float expected=Visual17.markSize(args[2]);ok(p.quadSize==expected,"ammo-dependent size");Visual17.configure(p);ok(p.quadSize==expected&&p.lifetime==Integer.MAX_VALUE&&p.alpha==1,"persistent marks preserve ammo size");ok(Visual17.size(p)==expected,"renderer size override preserves ammo size");}
  TimelessAPI.info.type="rifle";TimelessAPI.info.bullet.amount=8;Particle p=new Particle();Visual17.apply(p,"pack:custom_shell","pack:shotgun");ok(p.sprite.sprite.value().endsWith("shotgun"),"multi-pellet metadata fallback");
  Pose pose=new Pose();ok(dev.tactical.tacz.TracePose.matrix(pose)==pose.cameraMatrix,"hitscan uses active stage pose");pose.cameraMatrix=new Object();ok(dev.tactical.tacz.TracePose.matrix(pose)==pose.cameraMatrix,"hitscan refreshes current camera transform");
  System.out.println("SCOPE_VISUAL_CHECKS="+checks);
 }
}
