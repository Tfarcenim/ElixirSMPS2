package tfar.elixirsmps2.client;

import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffects;
import org.apache.commons.lang3.ArrayUtils;
import tfar.elixirsmps2.init.ModMobEffects;
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

    public static void modifyMovement(Input input) {
        if (input instanceof KeyboardInput keyboardInput) {
            if (Minecraft.getInstance().player.hasEffect(ModMobEffects.STUNNED)) {
                keyboardInput.jumping = false;
            }
        }
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

    public static String convertToRomanNumeral(int number) {
        // CACHE THIS STATICALLY SOMEWHERE
        final IntObjectPair<String>[] LOOKUP = new IntObjectPair[] {
                IntObjectPair.of(1000, "M"),
                IntObjectPair.of(900, "CM"),
                IntObjectPair.of(500, "D"),
                IntObjectPair.of(400, "CD"),
                IntObjectPair.of(100, "C"),
                IntObjectPair.of(90, "XC"),
                IntObjectPair.of(50, "L"),
                IntObjectPair.of(40, "XL"),
                IntObjectPair.of(10, "X"),
                IntObjectPair.of(9, "IX"),
                IntObjectPair.of(5, "V"),
                IntObjectPair.of(4, "IV"),
                IntObjectPair.of(1, "I")
        };

        final StringBuilder builder = new StringBuilder();

        for (IntObjectPair<String> pair : LOOKUP) {
            int value = pair.leftInt();

            while (number >= value) {
                builder.append(pair.right());
                number -= value;
            }
        }

        return builder.toString();
    }

}
