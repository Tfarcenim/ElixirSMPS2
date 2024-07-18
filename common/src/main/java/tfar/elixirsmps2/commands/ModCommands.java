package tfar.elixirsmps2.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import tfar.elixirsmps2.ElixirSMPS2;
import tfar.elixirsmps2.PlayerDuck;
import tfar.elixirsmps2.elixir.Elixir;
import tfar.elixirsmps2.elixir.Elixirs;
import tfar.elixirsmps2.network.S2CCooldownPacket;
import tfar.elixirsmps2.platform.Services;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ModCommands {

    public static final String ELIXIR_START = "elixir.start";
    public static final String ELIXIR_STOP = "elixir.stop";
    public static final String ELIXIR_EP = "elixir.ep";
    public static final String ELIXIR_EFFECT = "elixir.effect";
    public static final String ELIXIR_COOLDOWN = "elixir.cooldown";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("elixir")
                .then(Commands.literal("start")
                        .requires(requires(ELIXIR_START, 2))
                        .executes(ModCommands::start))
                .then(Commands.literal("stop")
                        .requires(requires(ELIXIR_STOP, 2))
                        .executes(ModCommands::stop))
                .then(Commands.literal("ep")
                        .requires(requires(ELIXIR_EP, 2))
                        .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("ep", IntegerArgumentType.integer()).executes(ModCommands::epAdd))))
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", EntityArgument.player()).executes(ModCommands::epGet)))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("ep", IntegerArgumentType.integer())
                                                .executes(ModCommands::epSet))))
                )
                .then(Commands.literal("effect")
                        .requires(requires(ELIXIR_EFFECT,2))
                        .then(Commands.literal("get").then(Commands.argument("player", EntityArgument.player()).executes(ModCommands::effectGet)))
                        .then(Commands.literal("reroll").then(Commands.argument("player", EntityArgument.player()).executes(ModCommands::reroll)))
                )
                .then(Commands.literal("cooldown")
                        .requires(requires(ELIXIR_COOLDOWN,2))
                        .then(Commands.literal("reset").then(Commands.argument("player", EntityArgument.player()).executes(ModCommands::resetCooldowns)))
                )
        );
    }

    public static int start(CommandContext<CommandSourceStack> ctx) {
        ElixirSMPS2.ENABLED = true;
        List<ServerPlayer> players = ctx.getSource().getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            Elixir elixir = playerDuck.getElixir();
            if (elixir == null) {
                elixir = Elixirs.getRandom(player.getRandom());
                playerDuck.setElixir(elixir);
            }
            elixir.applyPassiveEffects(player);
        }
        return players.size();
    }

    public static int stop(CommandContext<CommandSourceStack> ctx) {
        ElixirSMPS2.ENABLED = false;
        List<ServerPlayer> players = ctx.getSource().getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            PlayerDuck playerDuck = PlayerDuck.of(player);
            if (playerDuck.getElixir() != null) {
                playerDuck.getElixir().disable(player, false);
            }
        }
        return players.size();
    }

    public static int epAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int points = IntegerArgumentType.getInteger(ctx,"ep");
        PlayerDuck.of(player).addElixirPoints(points);
        return 1;
    }

    public static int epGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int points = PlayerDuck.of(player).getElixirPoints();
        ctx.getSource().sendSuccess(() -> Component.empty().append(player.getName()).append(Component.literal(" has "+points+" elixir points")),false);
        return 1;
    }

    public static int epSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int points = IntegerArgumentType.getInteger(ctx,"ep");
        PlayerDuck.of(player).setElixirPoints(points);
        return 1;
    }

    public static int effectGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        PlayerDuck playerDuck = PlayerDuck.of(player);
        Elixir current = playerDuck.getElixir();
        ctx.getSource().sendSuccess(() -> Component.empty().append(player.getName()).append(Component.literal(" has "+current+" elixir")),false);
        return 1;
    }

    public static int reroll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        return reroll(player);
    }

    public static int reroll(ServerPlayer player) {
        PlayerDuck playerDuck = PlayerDuck.of(player);
        Elixir current = playerDuck.getElixir();
        Elixir next = Elixirs.getRandom(player.getRandom());

        if (current == next) return 0;
        if (current != null) {
            current.disable(player, false);
        }
        playerDuck.setElixir(next);
        next.applyPassiveEffects(player);
        return 1;
    }

    public static int resetCooldowns(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        PlayerDuck playerDuck = PlayerDuck.of(player);
        int[] cooldowns = playerDuck.getCooldowns();
        Arrays.fill(cooldowns, 0);
        Services.PLATFORM.sendToClient(new S2CCooldownPacket(cooldowns),player);
        return 1;
    }

    public static Predicate<CommandSourceStack> requires(String node, int defaultValue) {
        return commandSourceStack -> Services.PLATFORM.checkBasicPermission(commandSourceStack, node, defaultValue);
    }

}
