package tfar.elixirsmps2;

import net.fabricmc.api.ModInitializer;

public class ElixirSMPS2Fabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        ElixirSMPS2.LOG.info("Hello Fabric world!");
        ElixirSMPS2.init();
    }
}
