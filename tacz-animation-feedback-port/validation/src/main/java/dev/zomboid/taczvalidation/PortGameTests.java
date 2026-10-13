package dev.zomboid.taczvalidation;
import dev.zomboid.taczfeedback.FeedbackMath;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.gametest.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import com.locknar.taczammocheck.port.SimpleChannel;
import com.locknar.taczammocheck.network.AmmoCheckStatePacket;
import com.locknar.taczammocheck.prone.ProneNetwork;
@GameTestHolder("locknar_doorkick") @PrefixGameTestTemplate(false)
public class PortGameTests {
 @GameTest(template="port_empty") public static void registeredItems(GameTestHelper h){h.assertTrue(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse("locknar_doorkick:mirror_gun")),"Door inspection item missing");h.assertTrue(BuiltInRegistries.SOUND_EVENT.containsKey(ResourceLocation.parse("locknar_doorkick:check")),"Ammo check sound missing");h.assertTrue(SimpleChannel.registeredMessages()==18,"Expected 18 migrated animation packets, got "+SimpleChannel.registeredMessages());h.succeed();}
 @GameTest(template="port_empty") public static void network(GameTestHelper h){
 var packet=new AmmoCheckStatePacket(java.util.UUID.randomUUID(),true,1500,"ak47");h.assertTrue(packet.equals(SimpleChannel.codecRoundTrip(packet,h.getLevel().registryAccess())),"Ammo animation packet changed");
 var move=new ProneNetwork.Move(5,true,30);h.assertTrue(move.equals(SimpleChannel.codecRoundTrip(move,h.getLevel().registryAccess())),"Prone input lost");
 var item=new net.minecraft.world.item.ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("tacz:modern_kinetic_gun")));com.tacz.guns.api.item.IGun.getIGunOrNull(item).setGunId(item,com.tacz.guns.api.TimelessAPI.getAllCommonGunIndex().iterator().next().getKey());
 var back=new com.locknar.taczammocheck.tactical.network.BackGunSyncPacket(7,item,2);var copy=(com.locknar.taczammocheck.tactical.network.BackGunSyncPacket)SimpleChannel.codecRoundTrip(back,h.getLevel().registryAccess());
 h.assertTrue(net.minecraft.world.item.ItemStack.isSameItemSameComponents(back.firstSlot(),copy.firstSlot()),"Back gun lost native item components");h.succeed();
 }
 @GameTest(template="port_empty") public static void cameraDirections(GameTestHelper h){
 h.assertTrue(Math.abs(FeedbackMath.bearing(0,1,0))<1e-6,"Forward indicator wrong");h.assertTrue(Math.abs(FeedbackMath.bearing(-1,0,0)-Math.PI/2)<1e-6,"Right indicator wrong");h.assertTrue(Math.abs(FeedbackMath.bearing(0,1,90)+Math.PI/2)<1e-6,"Indicator does not rotate with camera");h.assertTrue(Math.abs(Math.abs(FeedbackMath.bearing(0,-1,0))-Math.PI)<1e-6,"Rear indicator wrong");h.assertTrue(FeedbackMath.fade(1200)==0,"Damage indicator never fades");h.succeed();
 }
 @GameTest(template="port_empty") public static void scopedGlareRules(GameTestHelper h){
 h.assertTrue(FeedbackMath.glare(1,60,true,true,true,true),"Visible scoped sniper has no glare");h.assertTrue(!FeedbackMath.glare(1,60,true,true,true,false),"Glare leaks through walls");h.assertTrue(!FeedbackMath.glare(1,60,false,true,true,true),"Hip fire should not glare");h.assertTrue(!FeedbackMath.glare(.5,60,true,true,true,true),"Back/side scope should not glare");h.assertTrue(!FeedbackMath.glare(1,60,true,true,false,true),"No scope should not glare");h.succeed();
 }
}
