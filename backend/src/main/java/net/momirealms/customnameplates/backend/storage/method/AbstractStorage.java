package net.momirealms.customnameplates.backend.storage.method;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.DataStorageProvider;

public abstract class AbstractStorage implements DataStorageProvider {

	protected CustomNameplates plugin;

	public AbstractStorage(CustomNameplates plugin) {
		this.plugin = plugin;
	}

	@Override
	public void initialize(YamlDocument config) {
		// This method can be overridden in subclasses to perform initialization tasks specific to the storage type.
	}

	@Override
	public void disable() {
		// This method can be overridden in subclasses to perform cleanup or shutdown tasks specific to the storage type.
	}
}
