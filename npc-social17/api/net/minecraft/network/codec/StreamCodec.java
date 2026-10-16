package net.minecraft.network.codec;public interface StreamCodec<B,V>{V decode(B b);void encode(B b,V v);}
