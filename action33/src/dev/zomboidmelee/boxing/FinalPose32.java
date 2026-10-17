package dev.zomboidmelee.boxing;
/** Applied after TaCZ's priority-500 player-model tails, not in the earlier HumanoidModel callback. */
public final class FinalPose32{
 public static void apply(Object model,Object entity){if(Slide28.lowVisual(entity)||MoveClient25.slide(entity)>0){Arms23.clear(model);double w=MoveClient25.slide(entity);if(w<.01&&Slide28.lowVisual(entity))w=1;SlidePose28.apply(model,entity,w);return;}if(Mantle31.weight(entity)>0)Mantle31.body(model,entity);}
}
