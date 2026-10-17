from pathlib import Path
import subprocess,json,shutil
r=Path(__file__).resolve().parent;old=r.parent/'action20';tools=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn'];jar=r/'output'/json.loads((r/'validation.json').read_text())['file'];gore=next((old/'output').glob('*gore*.jar'))
def run(args):subprocess.run(list(map(str,args)),check=True)
for n in ['fixtures','test-base','test-extra']:(r/'build'/n).mkdir(exist_ok=True)
fixtures=r/'build/fixtures';shutil.copytree(old/'tests',fixtures,dirs_exist_ok=True);shutil.copytree(old/'tests18',fixtures,dirs_exist_ok=True)
p=fixtures/'dev/zomboidmelee/boxing/CombatChecks18.java';s=p.read_text().replace('public Map<Integer,Object>entities=', 'public Object dim="overworld";public Object dimension(){return dim;}public java.util.List<Object>getEntitiesOfClass(Class<?> c,net.minecraft.world.phys.AABB b){return new java.util.ArrayList<>(entities.values());}public Map<Integer,Object>entities=')
s=s.replace('public double getX(){return 0;}','public double getY(){return 0;}public float getYRot(){return 0;}public float getXRot(){return 50;}public float getEyeHeight(){return 1.6f;}public AABB getBoundingBox(){return new AABB(-.3,0,-.3,.3,1.8,.3);}public double getX(){return 0;}')
s=s.replace('class Zombie extends net.minecraft.world.entity.LivingEntity','class Zombie extends net.minecraft.world.entity.monster.Zombie')
s=s.replace('public Level w;public Data data=', 'public java.util.UUID uuid=java.util.UUID.randomUUID();public java.util.UUID getUUID(){return uuid;}public int getId(){return 1;}public Level w;public Data data=');p.write_text(s)
p=fixtures/'net/minecraft/world/phys/Vec3.java';s=p.read_text().replace('public Vec3 scale','public double distanceToSqr(Vec3 b){return (x-b.x)*(x-b.x)+(y-b.y)*(y-b.y)+(z-b.z)*(z-b.z);}public Vec3 scale');p.write_text(s)
p=fixtures/'net/minecraft/client/renderer/RenderType.java';p.write_text(p.read_text().replace('public static Object entitySolid','public static Object entityCutoutNoCull(Object o){return o;}public static Object entitySolid'))
# Compile mesh construction against fixtures that record actual dimensions and draw operations.
b=fixtures/'net/minecraft/client/model/geom/builders';b.mkdir(exist_ok=True)
for p in (r/'compile-api/net/minecraft/client/model/geom/builders').glob('*.java'):shutil.copy(p,b/p.name)
shutil.copy(r/'compile-api/net/minecraft/client/model/geom/PartPose.java',fixtures/'net/minecraft/client/model/geom/PartPose.java')
(b/'MeshDefinition.java').write_text('package net.minecraft.client.model.geom.builders;public class MeshDefinition{public PartDefinition getRoot(){return new PartDefinition();}}')
(b/'LayerDefinition.java').write_text('package net.minecraft.client.model.geom.builders;public class LayerDefinition{public static LayerDefinition create(MeshDefinition m,int w,int h){return new LayerDefinition();}public net.minecraft.client.model.geom.ModelPart bakeRoot(){return new net.minecraft.client.model.geom.ModelPart();}}')
p=fixtures/'net/minecraft/client/model/geom/ModelPart.java';p.write_text(p.read_text().replace('public float','public static int renders;public ModelPart getChild(String n){return new ModelPart();}public void render(com.mojang.blaze3d.vertex.PoseStack p,Object c,int l,int o){renders++;}public float'))
for name,code in {'net/minecraft/world/entity/monster/Zombie.java':'package net.minecraft.world.entity.monster;public class Zombie extends net.minecraft.world.entity.LivingEntity{}','net/minecraft/world/entity/EquipmentSlot.java':'package net.minecraft.world.entity;public enum EquipmentSlot{FEET}','net/minecraft/world/phys/EntityHitResult.java':'package net.minecraft.world.phys;public class EntityHitResult extends dev.zomboidmelee.boxing.Checks16.Hit{Object e;Vec3 p;public EntityHitResult(Object e,Vec3 p){super("ENTITY");this.e=e;this.p=p;}public Object getEntity(){return e;}public Vec3 getLocation(){return p;}}'}.items():p=fixtures/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(code)
base=r/'build/test-base';run([*ecj,'-cp',str(jar)+':'+str(gore),'-d',base,*fixtures.rglob('*.java'),*(r/'tests').rglob('*.java')])
cp=str(base)+':'+str(jar)+':'+str(gore);results=[]
for name in ['AnimationRegression','ArmorShadowRegression','ProtocolChecks18','MomentumChecks20','StompChecks21']:
 p=subprocess.run(['java','-Xverify:all','-cp',cp,'dev.zomboidmelee.boxing.'+name],text=True,capture_output=True);print(p.stdout,p.stderr,end='');p.check_returncode();results.append(p.stdout.strip())
asm=':'.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run([*ecj,'-cp',asm,'-d',r/'build/patch',r/'Inspect21.java'])
p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/patch')+':'+asm,'Inspect21',str(jar)],text=True,capture_output=True);print(p.stdout,p.stderr,end='');p.check_returncode();results.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps({'checks':results,'minecraft_runtime_tested':False},indent=2)+'\n')
