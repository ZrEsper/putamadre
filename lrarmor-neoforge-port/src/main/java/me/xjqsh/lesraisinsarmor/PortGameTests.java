package me.xjqsh.lesraisinsarmor;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import me.xjqsh.lesraisinsarmor.network.s2c.ArmorDataMessage;
@GameTestHolder("lrarmor") @PrefixGameTestTemplate(false)
public final class PortGameTests {
 @GameTest(template="port_empty") public static void registeredArmor(GameTestHelper helper){
  long count=BuiltInRegistries.ITEM.stream().filter(i->i instanceof LrArmorItem).count();helper.assertTrue(count==62,"Expected original 62 armor pieces, got "+count);
  for(var item:BuiltInRegistries.ITEM)if(item instanceof LrArmorItem armor)helper.assertTrue(armor.getMaxDurability()>0,"Missing loaded armor data for "+armor.getDescriptionId());helper.succeed();
 }
 @GameTest(template="port_empty") public static void loadedAttributes(GameTestHelper helper){
  LrArmorItem armor=(LrArmorItem)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("lrarmor","defender_chestplate"));
  ItemStack stack=new ItemStack(armor);double defense=stack.getAttributeModifiers().modifiers().stream().filter(e->e.attribute().equals(Attributes.ARMOR)).mapToDouble(e->e.modifier().amount()).sum();
  helper.assertTrue(armor.getDefense()>0,"Armor config was not loaded");helper.assertTrue(defense==armor.getDefense(),"NeoForge stack modifiers differ from configured defense: "+defense+" / "+armor.getDefense());helper.assertTrue(stack.getMaxDamage()==armor.getMaxDurability(),"Durability lost during component migration");helper.succeed();
 }
 @GameTest(template="port_empty") public static void networkCodec(GameTestHelper helper){
  var values=ArmorDataManager.getInstance().getNetworkCache();helper.assertTrue(values!=null&&!values.isEmpty(),"No datapack armor config cache");FriendlyByteBuf buf=new FriendlyByteBuf(Unpooled.buffer());try{ArmorDataMessage.CODEC.encode(buf,new ArmorDataMessage(values));var copy=ArmorDataMessage.CODEC.decode(buf);helper.assertTrue(copy.networkCache().equals(values),"Armor payload round-trip changed configuration");}finally{buf.release();}helper.succeed();
 }
 @GameTest(template="port_empty") public static void craftingIngredients(GameTestHelper helper){
   var server=helper.getLevel().getServer();int count=0;
   for(var recipe:server.getRecipeManager().getRecipes())if(recipe.id().getNamespace().equals("lrarmor")){count++;for(var ingredient:recipe.value().getIngredients())helper.assertTrue(ingredient.isEmpty()||ingredient.getItems().length>0,"Missing ingredient tag in "+recipe.id());}
   helper.assertTrue(count==62,"Expected 62 recipes, got "+count);helper.succeed();
 }
 @GameTest(template="port_empty") public static void completeSuitEffect(GameTestHelper helper){
   var player=net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(helper.getLevel());
   for(var type:net.minecraft.world.item.ArmorItem.Type.values())if(type!=net.minecraft.world.item.ArmorItem.Type.BODY)player.setItemSlot(type.getSlot(),new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("lrarmor","attacker_"+type.getName()))));
   LrArmorItem chest=(LrArmorItem)player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).getItem();helper.assertTrue(chest.getSuitCount(player)==4,"Suit count migrated incorrectly");chest.applyEffect(player);helper.assertTrue(player.hasEffect(me.xjqsh.lesraisinsarmor.init.ModEffects.TOUGH),"Full suit effect missing");helper.assertTrue(me.xjqsh.lesraisinsarmor.init.ModEffects.TOUGH.get().applyEffectTick(player,0),"Complete suit effect must continue");player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,ItemStack.EMPTY);helper.assertTrue(!me.xjqsh.lesraisinsarmor.init.ModEffects.TOUGH.get().applyEffectTick(player,0),"Incomplete suit effect must expire safely");helper.succeed();
 }
}
