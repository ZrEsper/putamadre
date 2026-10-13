from pathlib import Path
import subprocess
r=Path(__file__).resolve().parent;w=Path('/workspace/door-kick-work');src=w/'test-src';classes=w/'tests';src.mkdir(exist_ok=True)
files={
'net/minecraft/core/BlockPos.java':'package net.minecraft.core; public record BlockPos(int x,int y,int z){public BlockPos below(){return new BlockPos(x,y-1,z);}}',
'net/minecraft/world/level/block/state/properties/Property.java':'package net.minecraft.world.level.block.state.properties; public class Property {}',
'net/minecraft/world/level/block/state/properties/EnumProperty.java':'package net.minecraft.world.level.block.state.properties; public class EnumProperty extends Property {}',
'net/minecraft/world/level/block/state/properties/DoubleBlockHalf.java':'package net.minecraft.world.level.block.state.properties; public enum DoubleBlockHalf {LOWER,UPPER}',
'net/minecraft/world/level/block/Block.java':'package net.minecraft.world.level.block; public class Block {}',
'net/minecraft/world/level/block/DoorBlock.java':'package net.minecraft.world.level.block; public class DoorBlock extends Block {public static final net.minecraft.world.level.block.state.properties.EnumProperty HALF=new net.minecraft.world.level.block.state.properties.EnumProperty();}',
'net/minecraft/world/level/block/state/BlockState.java':'package net.minecraft.world.level.block.state; import net.minecraft.world.level.block.*;import net.minecraft.world.level.block.state.properties.*;public class BlockState {public Block block; public DoubleBlockHalf half; public Block getBlock(){return block;} public boolean hasProperty(Property p){return half!=null;} public Comparable getValue(Property p){return half;}}',
'net/minecraft/world/level/Level.java':'package net.minecraft.world.level; public class Level {public net.minecraft.world.level.block.state.BlockState state;public net.minecraft.world.level.block.state.BlockState getBlockState(net.minecraft.core.BlockPos p){return state;}}',
'Checks.java':'''import net.minecraft.core.*;import net.minecraft.world.level.*;import net.minecraft.world.level.block.*;import net.minecraft.world.level.block.state.*;import net.minecraft.world.level.block.state.properties.*;import com.locknar.taczammocheck.tactical.DoorCompat;import com.locknar.taczammocheck.tactical.server.ServerKickHandler;public class Checks {static int n;static void check(boolean b){if(!b)throw new AssertionError();n++;}public static void main(String[]a){Level l=new Level();l.state=new BlockState();BlockPos p=new BlockPos(2,10,3);l.state.block=new DoorBlock();l.state.half=DoubleBlockHalf.UPPER;check(ServerKickHandler.normalizeDoorPos(l,p).equals(p.below()));l.state.half=DoubleBlockHalf.LOWER;check(ServerKickHandler.normalizeDoorPos(l,p).equals(p));l.state.half=null;check(ServerKickHandler.normalizeDoorPos(l,p).equals(p));check(DoorCompat.isSupportedDoor(l.state));l.state.block=new Block();check(!DoorCompat.isSupportedDoor(l.state));check(!DoorCompat.isSupportedDoor(null));l.state.block=new ReinforcedDoor();check(DoorCompat.isSupportedDoor(l.state));System.out.println("Passed "+n+" checks on extracted patched methods");}static class ReinforcedDoor extends DoorBlock {}}'''}
for name,s in files.items():p=src/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
cp=':'.join(map(str,(r.parent/'weapon-interaction-patch/build/tools').glob('asm*.jar')))
subprocess.run(['java','-jar','/workspace/onboarding/tools/ecj.jar','-21','-proc:none','-warn:none','-cp',cp,'-d',str(w/'classes'),str(r/'Extract.java')],check=True)
jar=r.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.12-instant-door-kick.jar'
subprocess.run(['java','-cp',str(w/'classes')+':'+cp,'Extract',str(jar),str(classes)],check=True)
subprocess.run(['java','-jar','/workspace/onboarding/tools/ecj.jar','-21','-proc:none','-warn:none','-cp',str(classes),'-d',str(classes),*map(str,src.rglob('*.java'))],check=True)
subprocess.run(['java','-Xverify:all','-cp',str(classes),'Checks'],check=True)
