package tfar.elixirsmps2.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModKeybinds {
    public static List<KeyMapping> keyMappingList = new ArrayList<>();
    public static final KeyMapping ABILITY_0 = register(GLFW.GLFW_KEY_KP_0,"Ability 0");
    public static final KeyMapping ABILITY_1 = register(GLFW.GLFW_KEY_KP_1,"Ability 1");
    public static final KeyMapping ABILITY_2 = register(GLFW.GLFW_KEY_KP_2,"Ability 2");
    public static final KeyMapping ABILITY_3 = register(GLFW.GLFW_KEY_KP_3,"Ability 3");
    public static final KeyMapping ABILITY_4 = register(GLFW.GLFW_KEY_KP_4,"Ability 4");
    public static final KeyMapping ABILITY_5 = register(GLFW.GLFW_KEY_KP_5,"Ability 5");

    static KeyMapping register(int keyCode,String name) {
        KeyMapping keyMapping = new KeyMapping("Elixir Abilities", keyCode, name);
        keyMappingList.add(keyMapping);
        return keyMapping;
    }
}
