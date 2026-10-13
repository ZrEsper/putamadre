package port.apocalypticarsenal;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.gametest.*;
import net.minecraft.resources.ResourceLocation;
import me.xjqsh.lrtactical.api.LrTacticalAPI;
import me.xjqsh.lrtactical.api.item.IMeleeWeapon;
@GameTestHolder("apocalypticarsenal") @PrefixGameTestTemplate(false)
public class PortGameTests {
 @GameTest(template="port_empty") public static void melees(GameTestHelper h) {
 for(String name:java.util.List.of("fire_axe","hardened_katana","wooden_baseball_bat")) {
 var id=ResourceLocation.fromNamespaceAndPath("lrtactical",name); var index=LrTacticalAPI.getMeleeIndexes().stream().filter(i->i.getId().equals(id)).findFirst();h.assertTrue(index.isPresent(),"Melee pack did not load: "+id);
 var stack=index.orElseThrow().createItemStack();h.assertTrue(stack.getItem() instanceof IMeleeWeapon,"Not a LesRaisins melee");h.assertTrue(((IMeleeWeapon)stack.getItem()).getId(stack).equals(id),"Incorrect melee component ID");h.assertTrue(LrTacticalAPI.getMeleeIndex(stack).isPresent(),"Factory stack not recognized");
 var modifiers=index.orElseThrow().getDefaultModifiers().get(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE.value());double damage=modifiers.stream().mapToDouble(m->m.amount()).sum();double expected=name.equals("fire_axe")?12:name.equals("hardened_katana")?8:6;h.assertTrue(damage==expected,"Incorrect damage for "+name+": "+damage);
 var recipe=h.getLevel().getServer().getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("lrtactical","melee/"+name));h.assertTrue(recipe.isPresent(),"Missing smith recipe "+name);var smith=(com.tacz.guns.crafting.GunSmithTableRecipe)recipe.orElseThrow().value();var output=smith.getResultItem(h.getLevel().registryAccess());
 for(var ingredient:smith.getInputs()) h.assertTrue(ingredient.getIngredient().getItems().length>0,"Missing crafting material tag for "+name);h.assertTrue(output.getItem() instanceof IMeleeWeapon&&((IMeleeWeapon)output.getItem()).getId(output).equals(id),"Smith recipe makes wrong melee "+name);

 }h.succeed();
 }
}
