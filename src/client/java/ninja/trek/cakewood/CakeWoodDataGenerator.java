package ninja.trek.cakewood;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * CakeWood's generated model set is checked into {@code src/main/generated}.
 *
 * <p>The pre-26.2 model generator used APIs that were removed as part of the
 * client model rewrite. Keeping this entrypoint allows Fabric datagen to run
 * while the committed models remain the authoritative generated assets.</p>
 */
public class CakeWoodDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        // No providers: the complete generated asset set is committed.
    }
}
