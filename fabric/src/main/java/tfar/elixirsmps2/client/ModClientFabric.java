package tfar.elixirsmps2.client;

import net.fabricmc.api.ClientModInitializer;

public class ModClientFabric implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        ModClient.registerKeybinds();
    }
}
