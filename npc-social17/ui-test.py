"""Regression: execute real shipped screens against Minecraft's interface-shaped Component ABI."""
from pathlib import Path
import subprocess,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';d=r/'build/ui-src';out=r/'build/ui-test';d.mkdir(exist_ok=True);out.mkdir(exist_ok=True)
files={
'net/minecraft/client/Minecraft.java':'''package net.minecraft.client;import net.minecraft.client.gui.screens.Screen;import net.minecraft.client.player.LocalPlayer;import net.minecraft.client.multiplayer.ClientLevel;public class Minecraft{static final Minecraft INSTANCE=new Minecraft();public LocalPlayer player;public ClientLevel level;public Screen screen;public static Minecraft getInstance(){return INSTANCE;}public void setScreen(Screen s){screen=s;}}''',
'net/minecraft/client/gui/Font.java':'''package net.minecraft.client.gui;public class Font{public java.util.List<Object> split(net.minecraft.network.chat.Component c,int width){return java.util.List.of(c);}}''',
'dev/zomboid/firstaid/DownedSupport.java':'''package dev.zomboid.firstaid;public class DownedSupport{public static boolean canSelf(Object p){return false;}public static int seconds(Object p){return 50;}}''',
'dev/zomboid/firstaid/UnconsciousClient.java':'''package dev.zomboid.firstaid;public class UnconsciousClient{public static void tick(){}}''',
'UiChecks.java':'''import net.minecraft.client.gui.screens.Screen;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.gui.Font;import java.lang.reflect.*;public class UiChecks{public static void main(String[] args)throws Exception{if(!net.minecraft.network.chat.Component.class.isInterface())throw new AssertionError("Component must be an interface");Screen s=args[0].equals("aid")?new dev.zomboid.firstaid.UnconsciousScreen():new mod.cdv.tnpc.social.client.ConversationScreen("00000000-0000-0000-0000-000000000001\\nSurvivor\\nnormal\\nbrave\\n50\\n0\\nally\\nfollow\\n100\\ndialog.soc.greet.0\\n1\\n12");s.width=800;s.height=600;Field f=Screen.class.getDeclaredField("font");f.setAccessible(true);f.set(s,new Font());Method init=s.getClass().getDeclaredMethod("init");init.setAccessible(true);init.invoke(s);s.render(new GuiGraphics(),0,0,0);if(s.isPauseScreen())throw new AssertionError();System.out.println("UI_"+args[0]+"_CONSTRUCTOR_INIT_RENDER_OK");}}'''
}
for n,s in files.items():p=d/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
report=json.loads((r/'validation.json').read_text());jars=[str(r/'output'/report[k]['output'])for k in ['npc','aid']];cp=':'.join(jars+[str(r/'build/api')]);subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(out),*map(str,d.rglob('*.java'))],check=True)
count=0
for kind,old in [('npc','tacznpcs-2.1.0-1.21.1-tactical.17-social-companions.jar'),('aid','firstaid-1.21.1-ZOMBOID-v3.6.6-npc-medical-bridge.jar')]:
 basecp=':'.join([str(out),str(root/'mods'/old)]+jars+[str(r/'build/api')]);p=subprocess.run(['java','-Xverify:all','-cp',basecp,'UiChecks',kind],capture_output=True,text=True);assert p.returncode!=0 and 'IncompatibleClassChangeError' in p.stderr,p.stderr;count+=1
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+cp,'UiChecks',kind],capture_output=True,text=True);assert p.returncode==0,p.stderr;print(p.stdout.strip());count+=1
print('NPC_UI_LINKAGE_REGRESSION_CHECKS='+str(count))
