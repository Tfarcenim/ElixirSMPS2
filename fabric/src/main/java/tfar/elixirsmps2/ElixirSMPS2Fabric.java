package tfar.elixirsmps2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import tfar.elixirsmps2.commands.ModCommands;
import tfar.elixirsmps2.network.PacketHandler;

public class ElixirSMPS2Fabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register(ElixirSMPS2::onDeath);
        ServerPlayerEvents.COPY_FROM.register(ElixirSMPS2::onClone);
        ServerPlayerEvents.AFTER_RESPAWN.register(ElixirSMPS2::onAfterRespawn);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ModCommands.register(dispatcher));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ElixirSMPS2.onLogin(handler.player));
        PacketHandler.registerPackets();
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        ElixirSMPS2.init();
    }
}
