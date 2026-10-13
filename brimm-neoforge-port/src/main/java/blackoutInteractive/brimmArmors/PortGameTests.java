package blackoutInteractive.brimmArmors;
import net.minecraft.gametest.framework.*;
import net.neoforged.neoforge.gametest.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.core.component.DataComponents;
import blackoutInteractive.brimmArmors.common.items.*;
import blackoutInteractive.brimmArmors.common.registries.*;
import blackoutInteractive.brimmArmors.common.workbench.*;
@GameTestHolder("brimm") @PrefixGameTestTemplate(false)
public class PortGameTests {
 @GameTest(template="port_empty") public static void equipment(GameTestHelper h) {
 long armor=BuiltInRegistries.ITEM.stream().filter(i->i instanceof BrimmArmor).count();h.assertTrue(armor>100,"Missing BRIMM pieces: "+armor);
 for(var i:BuiltInRegistries.ITEM) if(i instanceof BrimmArmor a) {var s=new ItemStack(a);h.assertTrue(s.getMaxDamage()>0,"Missing durability "+a.unlocName);h.assertTrue(!s.getAttributeModifiers().modifiers().isEmpty(),"Missing armor stats "+a.unlocName);}
 h.succeed();
 }
 @GameTest(template="port_empty") public static void patches(GameTestHelper h) {
 var armor=(BrimmArmor)ItemRegistry.UKRAINIAN.get(); var stack=new ItemStack(armor);var patch=ItemRegistry.AMERICAN_FLAG_PATCH.get();armor.setPatch(stack,patch);h.assertTrue(armor.getPatch(stack)==patch,"Patch did not save to components");var copy=stack.copy();armor.removePatch(copy);h.assertTrue(armor.getPatch(copy)==null&&armor.getPatch(stack)==patch,"Patch removal changed source stack");
 var input=CraftingInput.of(2,1,java.util.List.of(new ItemStack(armor),new ItemStack(patch)));var add=new blackoutInteractive.brimmArmors.common.recipes.PatchAddRecipe(CraftingBookCategory.MISC);h.assertTrue(add.matches(input,h.getLevel()),"Patch recipe invalid");var result=add.assemble(input,h.getLevel().registryAccess());h.assertTrue(armor.getPatch(result)==patch,"Patch recipe failed");
 var remove=new blackoutInteractive.brimmArmors.common.recipes.PatchRemoveRecipe(CraftingBookCategory.MISC);var removal=CraftingInput.of(1,1,java.util.List.of(result));h.assertTrue(remove.matches(removal,h.getLevel()),"Removal recipe invalid");h.assertTrue(armor.getPatch(remove.assemble(removal,h.getLevel().registryAccess()))==null,"Removal did not clear patch");h.assertTrue(remove.getRemainingItems(removal).get(0).is(patch),"Patch was lost");h.succeed();
 }
 @GameTest(template="port_empty") public static void recipes(GameTestHelper h) {
 int count=0;for(var recipe:h.getLevel().getServer().getRecipeManager().getRecipes())if(recipe.id().getNamespace().equals("brimm")){count++;}h.assertTrue(count>=3,"BRIMM crafting recipes missing: "+count);
 var craft=CraftsManager.byId(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("brimm","wbcraft_base"));h.assertTrue(craft!=null&&!craft.ingredients().isEmpty(),"Workbench armor recipes missing");h.succeed();
 }
}
