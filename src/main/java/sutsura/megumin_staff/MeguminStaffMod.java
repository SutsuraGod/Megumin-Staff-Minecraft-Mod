package sutsura.megumin_staff;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sutsura.megumin_staff.item.ModItems;
import sutsura.megumin_staff.particle.ModParticles;
import sutsura.megumin_staff.sound.ModSounds;

public class MeguminStaffMod implements ModInitializer {
	public static final String MOD_ID = "megumin-staff";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModParticles.initialize();
		ModSounds.initialize();
	}
}
