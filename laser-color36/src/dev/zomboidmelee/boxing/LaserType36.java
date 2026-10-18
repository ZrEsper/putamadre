package dev.zomboidmelee.boxing;
/** Additive emissive color only: beam fragments must never become solid depth occluders. */
public final class LaserType36 {
 static Object cached;
 public static Object get(){if(cached!=null)return cached;Class<?> shard=R.type("net.minecraft.client.renderer.RenderStateShard");Object builder=R.call(R.type("net.minecraft.client.renderer.RenderType$CompositeState"),"builder");
  R.call(builder,"setShaderState",R.get(shard,"POSITION_COLOR_TEX_LIGHTMAP_SHADER"));
  R.call(builder,"setTransparencyState",R.get(R.type("com.tacz.guns.client.model.functional.BeamRenderer$LaserBeamRenderState"),"LIGHTNING_ADDITIVE_TRANSPARENCY"));
  R.call(builder,"setOutputState",R.get(shard,"ITEM_ENTITY_TARGET"));R.call(builder,"setLightmapState",R.get(shard,"LIGHTMAP"));
  R.call(builder,"setWriteMaskState",R.get(shard,"COLOR_WRITE"));R.call(builder,"setCullState",R.get(shard,"NO_CULL"));
  R.call(builder,"setTextureState",R.make("net.minecraft.client.renderer.RenderStateShard$TextureStateShard",Laser32.texture(null),false,false));
  Object state=R.call(builder,"createCompositeState",false);
  cached=R.call(R.type("net.minecraft.client.renderer.RenderType"),"create","zomboid_laser_color_only36",R.get(R.type("com.mojang.blaze3d.vertex.DefaultVertexFormat"),"POSITION_COLOR_TEX_LIGHTMAP"),R.get(R.type("com.mojang.blaze3d.vertex.VertexFormat$Mode"),"QUADS"),256,true,true,state);return cached;
 }
}
