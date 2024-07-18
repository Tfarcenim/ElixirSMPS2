package tfar.elixirsmps2.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;
import tfar.elixirsmps2.network.S2CCooldownPacket;
import tfar.elixirsmps2.platform.Services;

public class ModClient {
    public static int[] local_cooldowns = new int[6];

    public static void registerKeybinds() {
        ModKeybinds.keyMappingList.forEach(Services.PLATFORM::registerKeybinding);
    }

    public static void registerKeybind(KeyMapping keyMapping) {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings,keyMapping);
    }

    public static void clientTick() {
        for (ModKeybinds.ModKeybinding keyMapping : ModKeybinds.keyMappingList) {
            while (keyMapping.consumeClick()) {
                keyMapping.onPress.run();
                break;
            }
        }
    }

    public static void handle(S2CCooldownPacket s2CCooldownPacket) {
        local_cooldowns = s2CCooldownPacket.cooldowns;
        Minecraft.getInstance().player.displayClientMessage(buildCooldown(), true);
    }

    public static MutableComponent buildCooldown() {
        MutableComponent component = Component.empty();
        for (int i = 0; i < local_cooldowns.length;i++) {
            component.append(getRomanNumeral(i)).append(" ");
            int cooldown = local_cooldowns[i];
            String sec = String.format("%.1f", cooldown/20d);
            component.append(Component.literal(sec).withStyle(cooldown > 0 ? ChatFormatting.RED : ChatFormatting.GREEN));

            if (i < local_cooldowns.length - 1) {
                component.append(" | ");
            }

        }
        return component;
    }

    static Component getRomanNumeral(int i) {
        return switch (i) {
            case 0 -> Component.literal("O").withStyle(ChatFormatting.BOLD,ChatFormatting.AQUA);
            case 1 -> Component.literal("I").withStyle(ChatFormatting.BOLD,ChatFormatting.AQUA);
            case 2 -> Component.literal("II").withStyle(ChatFormatting.BOLD,ChatFormatting.AQUA);
            case 3 -> Component.literal("III").withStyle(ChatFormatting.BOLD,ChatFormatting.AQUA);
            case 4 -> Component.literal("IV").withStyle(ChatFormatting.BOLD,ChatFormatting.AQUA);
            case 5 -> Component.literal("V").withStyle(ChatFormatting.BOLD,ChatFormatting.AQUA);
            default -> Component.empty();
        };
    }

}
