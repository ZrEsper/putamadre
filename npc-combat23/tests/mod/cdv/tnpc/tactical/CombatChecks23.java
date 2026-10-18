package mod.cdv.tnpc.tactical;
import fixture.World;import net.minecraft.world.entity.player.Player;import net.minecraft.nbt.CompoundTag;
public class CombatChecks23 {
 public static class Soldier extends mod.cdv.tnpc.entity.AutoGunOperator {public Soldier(World w){super(w);}public boolean hasEffect(Object e){return vision;}}
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static void main(String[]args)throws Exception{
 World w=new World();Soldier n=new Soldier(w);CompoundTag tag=new CompoundTag();tag.putInt("npc22_role",3);Roles22.read(n,tag);Player p=new Player(w);Social.init(n);CalmBrain.state(n).target=p;
 n.vision=true;ok(Combat23.vision(true,n,p),"active NVG extends dark combat vision");ok(TeamBrain.vision(true,n,p),"patched real TeamBrain honors NVG before dark distance gate");ok(!TeamBrain.vision(false,n,p),"NVG cannot see through wall");n.vision=false;ok(!Combat23.vision(true,n,p),"unpowered NVG cannot bypass darkness");
 var m=TeamBrain.mem(n);m.tidying=true;m.sheltering=true;m.fleeing=true;Combat23.prepare(n);ok(!m.tidying&&!m.sheltering&&!m.fleeing,"engaged hostile clears civilian blocking flags");
 CalmBrain.state(n).target=null;m.sheltering=true;Combat23.prepare(n);ok(m.sheltering,"idle schedules preserved without enemy");
 CalmBrain.state(n).target=p;Social.mem(n).occupied=true;Combat23.prepare(n);ok(m.sheltering,"active interaction not interrupted");Social.mem(n).occupied=false;
 tag.putInt("npc22_role",1);Roles22.read(n,tag);n.vision=true;ok(!Combat23.vision(true,n,p),"neutral rules preserved");ok(!Combat23.engaged(n),"neutral never promoted to hostile");
 System.out.println("REAL_TEAM_BRAIN_NVG_WALL_PRIORITY_INTERACTION_CHECKS="+checks);
 }
}
