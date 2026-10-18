package net.flashlight.battery;
import net.irisshaders.iris.Iris;import net.irisshaders.iris.pipeline.WorldRenderingPipeline;import net.irisshaders.iris.gl.blending.BlendModeStorage;
public class IrisChecks12 {
 static int checks;static void check(boolean b){if(!b)throw new AssertionError();checks++;}
 static class Pack implements WorldRenderingPipeline{int phases,pbr;Object program=new Object();public Object getSodiumPrograms(){return program;}public void setPhase(String p){phases++;}public void setOverridePhase(String p){phases++;}public String getPhase(){return "TRANSLUCENT";}public boolean shouldDisableOcclusionCulling(){return true;}public void onSetShaderTexture(int id){pbr++;}public void fail(){throw new IllegalArgumentException("fixture terrain error");}}
 static class Level {private WorldRenderingPipeline pipeline;Level(WorldRenderingPipeline p){pipeline=p;}}
 public static void main(String[]args){
  Pack pack=new Pack();Level level=new Level(pack);var manager=Iris.getPipelineManager();manager.use(pack);Object blend=BlendModeStorage.originalBlend;
  for(int i=0;i<2000;i++){
   try(Iris12 scope=Iris12.enter(level)){
    var capture=manager.getPipelineNullable();check(capture!=pack&&capture==level.pipeline);check(capture.getSodiumPrograms()==null);check(pack.getSodiumPrograms()==pack.program);
    capture.onSetShaderTexture(99);check(pack.pbr==0);capture.setPhase("SOLID");capture.setOverridePhase("CUTOUT");check(pack.phases==0);check(capture.getPhase().equals("TRANSLUCENT"));check(capture.shouldDisableOcclusionCulling());check(!BlendModeStorage.blendLocked);
    BlendModeStorage.originalBlendEnable=false;BlendModeStorage.originalBlend=new Object();
   }
   check(manager.getPipelineNullable()==pack&&level.pipeline==pack);check(BlendModeStorage.blendLocked&&BlendModeStorage.originalBlendEnable&&BlendModeStorage.originalBlend==blend);
  }
  try(Iris12 scope=Iris12.enter(level)){manager.getPipelineNullable().fail();throw new AssertionError();}catch(IllegalArgumentException expected){check(manager.getPipelineNullable()==pack&&level.pipeline==pack&&BlendModeStorage.blendLocked);}
  try{Iris12.enter(new Object());throw new AssertionError();}catch(IllegalStateException expected){check(manager.getPipelineNullable()==pack&&BlendModeStorage.blendLocked);}
  manager.use(null);try(Iris12 scope=Iris12.enter(level)){check(manager.getPipelineNullable()==null&&level.pipeline==pack);}
  System.out.println("IRIS_ACTIVE_PACK_SCOPE_RESTORE_CHECKS="+checks);
 }
}
