package tfar.elixirsmps2.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import tfar.elixirsmps2.network.C2SKeybindPacket;
import tfar.elixirsmps2.platform.Services;

import java.util.ArrayList;
import java.util.List;

public class ModKeybinds {
    public static List<ModKeybinding> keyMappingList = new ArrayList<>();
    public static final ModKeybinding ABILITY_0 = register(GLFW.GLFW_KEY_KP_0,"Ability 0", () -> sendKey(0));
    public static final ModKeybinding ABILITY_1 = register(GLFW.GLFW_KEY_KP_1,"Ability 1", () -> sendKey(1));
    public static final ModKeybinding ABILITY_2 = register(GLFW.GLFW_KEY_KP_2,"Ability 2", () -> sendKey(2));
    public static final ModKeybinding ABILITY_3 = register(GLFW.GLFW_KEY_KP_3,"Ability 3", () -> sendKey(3));
    public static final ModKeybinding ABILITY_4 = register(GLFW.GLFW_KEY_KP_4,"Ability 4", () -> sendKey(4));
    public static final ModKeybinding ABILITY_5 = register(GLFW.GLFW_KEY_KP_5,"Ability 5", () -> sendKey(5));

    private static void sendKey(int i) {
        Services.PLATFORM.sendToServer(new C2SKeybindPacket(i));
    }

    public static class ModKeybinding extends KeyMapping{

        public final Runnable onPress;

        public ModKeybinding(String $$0, int $$1, String $$2, Runnable onPress) {
            this($$0, InputConstants.Type.KEYSYM, $$1, $$2,onPress);
        }

        public ModKeybinding(String $$0, InputConstants.Type $$1, int $$2, String $$3, Runnable onPress) {
            super($$0, $$1, $$2, $$3);
            this.onPress = onPress;
        }
    }

    static ModKeybinding register(int keyCode,String name,Runnable onPress) {
        ModKeybinding keyMapping = new ModKeybinding(name, keyCode, "Elixir Abilities",onPress);
        keyMappingList.add(keyMapping);
        return keyMapping;
    }
}
