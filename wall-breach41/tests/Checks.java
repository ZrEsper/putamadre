import dev.zomboid.hordes.*;import net.minecraft.core.BlockPos;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.entity.Mob;import net.minecraft.world.level.block.state.BlockState;
public class Checks{
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 static Mob mob(double x,double z){Mob m=new Mob();m.x=x;m.z=z;m.world=new ServerLevel();return m;}
 static ServerLevel world(Mob m){return (ServerLevel)m.world;}
 static BlockPos wall(Mob m,int x,int y,int z){BlockPos p=new BlockPos(x,y,z);world(m).blocks.put(p,new BlockState(false));return p;}
 static BlockPos find(Mob m,double x,double z){return BreachAI.find(m,world(m),m.tag,x,z);}
 static void lock(Mob m,BlockPos p){m.tag.putInt("zh_siege_bx",p.x());m.tag.putInt("zh_siege_by",p.y());m.tag.putInt("zh_siege_bz",p.z());}
 public static void main(String[]a){
  Mob m=mob(.5,.5);BlockPos feet=wall(m,1,0,0),head=wall(m,1,1,0);ok(find(m,1,0).equals(feet),"feet first");lock(m,head);ok(find(m,1,0).equals(head),"maintain valid progress");world(m).blocks.remove(head);ok(find(m,1,0).equals(feet),"reselect removed block");world(m).blocks.remove(feet);ok(find(m,1,0)==null,"clear opening");
  m=mob(.5,.5);head=wall(m,1,1,0);ok(find(m,1,0).equals(head),"headroom obstruction");
  m=mob(.5,.5);wall(m,1,-1,0);ok(find(m,1,0)==null,"never excavate ground");
  m=mob(.5,.72);feet=wall(m,1,0,0);ok(find(m,1,1).equals(feet),"diagonal shoulder blocked despite clear center ray");
  m=mob(.5,.5);feet=wall(m,1,0,0);BlockPos behind=wall(m,2,0,0);lock(m,behind);ok(find(m,1,0).equals(feet),"cannot keep target through nearer wall");
  m=mob(.5,.5);feet=wall(m,1,0,0);lock(m,feet);ok(find(m,-1,0)==null,"turning away abandons old wall");
  m=mob(.5,.5);feet=wall(m,1,0,0);world(m).unloaded.add(feet);ok(find(m,1,0)==null,"unloaded obstruction");ok(find(m,Double.NaN,0)==null,"invalid direction");ok(find(m,0,0)==null,"no direction");
  m=mob(.5,.5);feet=wall(m,1,0,0);head=wall(m,1,1,0);world(m).blocks.get(feet).container=true;ok(find(m,1,0).equals(head),"prefer breakable route without container destruction");
  m=mob(.5,.5);ok(!BreachAI.blocked(m),"initial navigation");world(m).time=39;ok(!BreachAI.blocked(m),"wait before stuck");world(m).time=40;ok(BreachAI.blocked(m),"stuck with active navigation");m.x+=1;ok(!BreachAI.blocked(m),"movement clears stuck");m.horizontalCollision=true;ok(BreachAI.blocked(m),"immediate collision");m.horizontalCollision=false;m.nav.done=true;ok(BreachAI.blocked(m),"finished unreachable route");ok(BreachAI.permitted(m,world(m)),"grief allowed");world(m).grief=false;ok(!BreachAI.permitted(m,world(m)),"NeoForge grief veto");
  // Two-block-high walls in every cardinal direction and approach offset.
  for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)if(Math.abs(dx)+Math.abs(dz)==1)for(double offset:new double[]{.2,.5,.8}){
   m=mob(dx==0?offset:.5,dz==0?offset:.5);int x=dx>0?1:dx<0?-1:0,z=dz>0?1:dz<0?-1:0;feet=wall(m,x,0,z);head=wall(m,x,1,z);
   ok(find(m,dx,dz).equals(feet),"wall base in all directions");world(m).blocks.remove(feet);ok(find(m,dx,dz).equals(head),"remaining head block");world(m).blocks.remove(head);ok(find(m,dx,dz)==null,"walkable gap");
  }
  System.out.println(checks+" geometry, navigation and permission checks passed");
 }
}
