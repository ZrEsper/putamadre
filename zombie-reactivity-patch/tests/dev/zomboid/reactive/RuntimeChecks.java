package dev.zomboid.reactive;
import static dev.zomboid.reactive.Checks.check;
import net.minecraft.world.entity.player.Player;
public class RuntimeChecks {
 public static void main(String[] args){
  Fakes.Level l=new Fakes.Level();Fakes.Zmob mob=new Fakes.Zmob(l);Player p=new Player(l);l.players.add(p);p.z=10;
  Reactive.profile(mob,mob.tag);mob.tag.putInt("zi_type",1);mob.tag.putInt("zi_delay",2);mob.tag.putDouble("zi_hearing",1);
  check(!Reactive.sees(mob,p),"visual reaction delay");l.now=104;check(Reactive.sees(mob,p),"normal visual acquisition");long lastPos=mob.tag.getLong("zh_pos");
  p.z=25;mob.los=false;mob.tag.putUUID("zi_angry",p.uuid);mob.tag.putLong("zi_anger_until",1000);check(!Reactive.sees(mob,p),"anger does not see through walls");check(mob.tag.getLong("zh_pos")==lastPos,"hidden movement does not update known location");
  mob.target=p;Reactive.tick(new Fakes.Event(mob));check(mob.target==null&&mob.nav.stops==1,"lose hidden target and stop tracking live coordinates");
  Fakes.Pos pos=new Fakes.Pos(0,0,10);l.now=108;check(Reactive.hear(mob,pos,p,18,200,false),"running heard behind nearby wall");check(mob.tag.getLong("zh_pos")==pos.asLong(),"noise remembers emitted position instead of hidden player");
  p.z=11;check(!Reactive.hear(mob,p.blockPosition(),p,0,200,false),"suppressed legacy footsteps cannot reveal player");
  p.z=0;Reactive.playerTick(p);l.now=120;p.z=1;p.sprint=true;p.character.traits.add("survivorcreator:elusive");Reactive.playerTick(p);
  Reactive.Step step=Reactive.steps.get(p);check(Math.abs(step.radius-9.9)<.001,"actual trait reduces emitted running radius");l.now=130;p.z=100;Reactive.playerTick(p);check(step.noiseTime==120&&Reactive.steps.get(p).noiseTime==120,"teleport is not a footstep");
  p.z=.5;mob.los=true;l.now=200;check(!Reactive.allowAttack(mob,p),"attack starts a windup rather than instant bite");long ready=mob.tag.getLong("zpr_bite_ready");l.now=ready-2;Reactive.jump(p);l.now=ready;check(!Reactive.allowAttack(mob,p)&&p.message.equals("Mordida esquivada"),"well timed server jump cancels bite");
  check(mob.tag.getLong("zpr_attack_next")>l.now&&p.tag.getLong("zpr_dodge_next")==l.now+40,"dodge and attack cooldown applied");
  l.now=mob.tag.getLong("zpr_attack_next");check(!Reactive.allowAttack(mob,p),"next bite needs a fresh windup");l.now=mob.tag.getLong("zpr_bite_ready");check(Reactive.allowAttack(mob,p),"no fresh jump means next bite succeeds");
  check(Math.abs(Reactive.speed(3,mob)-.5)<.001,"mutant runner preserves its native base speed");
  check(Math.abs(Reactive.speed(3,new net.minecraft.world.entity.monster.Zombie(l))-.34)<.001,"ordinary zombie runner speed");
  l.now=400;l.entities.add(mob);Reactive.sound(new Fakes.Sound("minecraft:block.chest.open"),l,0,0,4,1);
  check(mob.tag.getLong("zh_pos")==new Fakes.Pos(0,0,4).asLong(),"environment chest noise is investigated without a player target");
  Reactive.sound(new Fakes.Sound("minecraft:entity.generic.explode"),l,0,0,6,1);
  check(mob.tag.getLong("zh_pos")==new Fakes.Pos(0,0,6).asLong(),"louder explosion overrides same tick weak sound");
  Reactive.sound(new Fakes.Sound("minecraft:entity.zombie.ambient"),l,0,0,12,1);
  check(mob.tag.getLong("zh_pos")==new Fakes.Pos(0,0,6).asLong(),"zombie ambience does not create noise feedback");
  l.now=412;Reactive.block(p,l,new Fakes.Pos(0,0,5));check(mob.tag.getLong("zh_pos")==new Fakes.Pos(0,0,5).asLong(),"block interactions attract at their actual position");
  l.now=424;Reactive.levelEvent(2001,l,0,0,7);check(mob.tag.getLong("zh_pos")==new Fakes.Pos(0,0,7).asLong(),"server block break event attracts");
  check(!Reactive.warned,"no suppressed runtime exceptions in behavioral cases");
  System.out.println(Checks.count+" integration behavior cases passed with test entities; not a Minecraft runtime.");
 }
}
