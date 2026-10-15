package dev.zomboid.isotacz;
import java.util.Locale;
/** Recognition never reads keyUse: that would recurse through KeyBindingMixin. */
public final class MedicalInput30 {
 static Object lastPlayer;static boolean wasMedical;
 static Class<?> consumable;static boolean checked;
 static boolean consumable(Object item){if(!checked){checked=true;try{consumable=Class.forName("me.xjqsh.lrtactical.api.item.IConsumable");}catch(ClassNotFoundException absent){consumable=null;}}return consumable!=null&&consumable.isInstance(item);}
 public static boolean stack(Object s){if(s==null||R.yes(R.call(s,"isEmpty")))return false;Object item=R.call(s,"getItem");if(consumable(item))return true;String id=String.valueOf(R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM"),"getKey",item)).toLowerCase(Locale.ROOT);return id.startsWith("firstaid:")||id.contains("bandage")||id.contains("medkit")||id.contains("syringe")||id.contains("splint")||id.contains("medical");}
 static boolean critical(Object p){try{Class<?> utils=Class.forName("ichttt.mods.firstaid.common.util.CommonUtils");Object model=R.call(utils,"getDamageModel",p);return model!=null&&R.yes(R.get(model,"criticalConditionActive"));}catch(ClassNotFoundException absent){return false;}}
 public static boolean held(){Object mc=Bridge.mc(),p=R.get(mc,"player");if(!Bridge.active()||p==null)return false;if(R.yes(R.call(p,"isUsingItem"))&&stack(R.call(p,"getUseItem")))return true;boolean main=stack(R.call(p,"getMainHandItem")),off=stack(R.call(p,"getOffhandItem"));return main||off&&(!Bridge.gun(p)||critical(p));}
 public static boolean nativeInput(){boolean medical=held();Object mc=Bridge.mc(),p=R.get(mc,"player");if(medical&&(p!=lastPlayer||!wasMedical)){R.call(mc,"setLocation",new Object[]{null});R.call(mc,"setOriginalLocation",new Object[]{null});Bridge.rightAim=false;Bridge.mouseClaimed=false;R.set(Bridge.mod(),"using",false);}lastPlayer=p;wasMedical=medical;return medical;}
 public static boolean medical(){if(!held())return false;Object mc=Bridge.mc(),p=R.get(mc,"player");return R.yes(R.call(p,"isUsingItem"))||Bridge.key(mc,"keyUse");}
 public static boolean skipMovement(){return medical()||NativeActions.riding();}
}
