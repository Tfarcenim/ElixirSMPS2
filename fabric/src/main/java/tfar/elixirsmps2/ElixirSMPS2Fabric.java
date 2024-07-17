package tfar.elixirsmps2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class ElixirSMPS2Fabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register(ElixirSMPS2::onDeath);
        ServerPlayerEvents.COPY_FROM.register(ElixirSMPS2::onClone);
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        ElixirSMPS2.init();
    }
}
