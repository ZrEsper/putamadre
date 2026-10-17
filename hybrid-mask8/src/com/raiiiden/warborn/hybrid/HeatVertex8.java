package com.raiiiden.warborn.hybrid;
import com.mojang.blaze3d.vertex.VertexConsumer;
/** Native consumer: bulk/default vertex methods execute on this object, not its delegate. */
public final class HeatVertex8 implements VertexConsumer {
 public static final int MASK_ALPHA=253;private final VertexConsumer delegate;
 public HeatVertex8(VertexConsumer delegate){this.delegate=delegate;}
 public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}
 public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,HeatVertex8.MASK_ALPHA);return this;}
 public VertexConsumer setUv(float u,float v){delegate.setUv(u,v);return this;}
 public VertexConsumer setUv1(int u,int v){delegate.setUv1(0,10);return this;}
 public VertexConsumer setUv2(int u,int v){delegate.setUv2(240,240);return this;}
 public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}
}
