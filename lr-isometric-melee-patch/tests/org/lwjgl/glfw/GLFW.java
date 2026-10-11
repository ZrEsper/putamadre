package org.lwjgl.glfw;public class GLFW {public static boolean pressed;public static int glfwGetMouseButton(long window,int button){return pressed?1:0;}}
