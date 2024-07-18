package tfar.elixirsmps2.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ModClientFabric implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> ModClient.clientTick());
        ModClient.registerKeybinds();
    }
}
