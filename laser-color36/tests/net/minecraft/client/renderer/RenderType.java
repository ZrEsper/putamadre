package net.minecraft.client.renderer;
import java.util.*;import com.mojang.blaze3d.vertex.VertexFormat;
public class RenderType{
 public final Map<String,Object>states;RenderType(Map<String,Object>s){states=s;}
 public static Object create(String name,Object format,VertexFormat.Mode mode,int size,boolean crumbling,boolean sorted,CompositeState state){return new RenderType(state.values);}
 public static class CompositeState{public Map<String,Object>values;CompositeState(Map<String,Object>m){values=m;}public static Builder builder(){return new Builder();}}
 public static class Builder{
  Map<String,Object>m=new HashMap<>();public Builder setShaderState(Object x){m.put("shader",x);return this;}public Builder setTransparencyState(Object x){m.put("blend",x);return this;}public Builder setOutputState(Object x){m.put("target",x);return this;}public Builder setLightmapState(Object x){m.put("lightmap",x);return this;}public Builder setWriteMaskState(Object x){m.put("write",x);return this;}public Builder setCullState(Object x){m.put("cull",x);return this;}public Builder setTextureState(Object x){m.put("texture",x);return this;}public CompositeState createCompositeState(boolean outline){return new CompositeState(m);}
 }
}
