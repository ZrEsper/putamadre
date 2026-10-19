package dev.zomboid.gore;
import java.util.List;
/** Broad cuboid skull, open jaw and three thick ribs: reference silhouette first. */
public final class Skeleton17 {
 static void b(List<Anatomy14.Piece>p,String name,float x,float y,float z,float X,float Y,float Z){Anatomy14.add(p,name,2,x,y,z,X,Y,Z);}
 public static void add(List<Anatomy14.Piece>p,int region){
 if(region==0){
  b(p,"cranial_roof",.04f,.02f,.06f,.96f,.16f,.94f);
  b(p,"forehead",.04f,.14f,.06f,.96f,.39f,.21f);
  b(p,"occipital",.04f,.14f,.80f,.96f,.73f,.94f);
  b(p,"temporal_left",.04f,.14f,.20f,.17f,.73f,.81f);
  b(p,"temporal_right",.83f,.14f,.20f,.96f,.73f,.81f);
  // These gaps are actual holes, not white or dark surface rectangles.
  b(p,"cheek_left",.04f,.37f,.06f,.19f,.70f,.23f);
  b(p,"cheek_right",.81f,.37f,.06f,.96f,.70f,.23f);
  for(float x:new float[]{.18f,.55f})Anatomy14.add(p,"socket_shadow",4,x,.38f,.22f,x+.27f,.59f,.235f);
  Anatomy14.add(p,"socket_shadow",4,.19f,.755f,.32f,.81f,.825f,.335f);
  b(p,"nose_bridge",.44f,.39f,.06f,.56f,.59f,.23f);
  b(p,"maxilla",.12f,.58f,.06f,.88f,.70f,.26f);
  b(p,"palate",.16f,.66f,.22f,.84f,.73f,.81f);
  b(p,"jaw_ramus_left",.08f,.68f,.20f,.19f,.96f,.81f);
  b(p,"jaw_ramus_right",.81f,.68f,.20f,.92f,.96f,.81f);
  b(p,"jaw_chin",.08f,.86f,.06f,.92f,.96f,.26f);
  b(p,"jaw_floor",.18f,.88f,.22f,.82f,.96f,.75f);
  b(p,"cervical",.42f,.71f,.64f,.58f,1,.80f);
  for(int i=0;i<6;i++){float x=.17f+i*.11f;Anatomy14.add(p,"tooth",5,x,.70f,.075f,x+.08f,.755f,.21f);Anatomy14.add(p,"tooth",5,x,.825f,.075f,x+.08f,.865f,.21f);}
 }else if(region==6){
  b(p,"spine",.45f,0,.62f,.55f,1,.82f);
  for(int i=0;i<7;i++){float y=.08f+i*.12f;b(p,"vertebra",.40f,y,.65f,.60f,y+.075f,.87f);}
  b(p,"clavicle",0,0,.24f,1,.105f,.67f);
  b(p,"sternum",.43f,.10f,.17f,.57f,.63f,.34f);
  for(int i=0;i<3;i++){float y=.17f+i*.17f;
   b(p,"rib_anterior_left",.12f,y,.16f,.44f,y+.09f,.33f);
   b(p,"rib_anterior_right",.56f,y,.16f,.88f,y+.09f,.33f);
   b(p,"rib_lateral_left",.08f,y,.26f,.20f,y+.09f,.76f);
   b(p,"rib_lateral_right",.80f,y,.26f,.92f,y+.09f,.76f);
   b(p,"rib_posterior",.14f,y,.67f,.86f,y+.09f,.80f);
   // Short inward tips connect front bands to the sternum without a dense grille.
   b(p,"rib_tip_left",.39f,y+.035f,.20f,.50f,y+.12f,.34f);
   b(p,"rib_tip_right",.50f,y+.035f,.20f,.61f,y+.12f,.34f);
  }
  b(p,"pelvis_left",.13f,.77f,.28f,.29f,.98f,.76f);
  b(p,"pelvis_right",.71f,.77f,.28f,.87f,.98f,.76f);
  b(p,"pelvis_back",.23f,.84f,.63f,.77f,.99f,.81f);
  b(p,"pubic_arch",.22f,.92f,.22f,.78f,1,.38f);
 }else{
  boolean arm=region==2||region==3;
  if(arm)b(p,"shoulder_link",region==2?0:.29f,0,.31f,region==2?.71f:1,.08f,.69f);
  b(p,"upper_bone",.29f,0,.31f,.71f,.51f,.69f);
  b(p,"joint",.29f,.48f,.31f,.71f,.54f,.69f);
  b(p,arm?"forearm":"shin",.29f,.53f,.31f,.71f,1,.69f);
  if(!arm)b(p,"foot",.26f,.92f,.17f,.74f,1,.69f);
 }
 }
}
