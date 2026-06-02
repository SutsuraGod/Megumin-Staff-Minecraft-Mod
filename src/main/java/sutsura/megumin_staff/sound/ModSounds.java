package sutsura.megumin_staff.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import sutsura.megumin_staff.MeguminStaffMod;

public final class ModSounds {
    public static final Identifier MEGUMIN_CAST_ID = Identifier.fromNamespaceAndPath(MeguminStaffMod.MOD_ID, "megumin_cast");
    public static final SoundEvent MEGUMIN_CAST = SoundEvent.createVariableRangeEvent(MEGUMIN_CAST_ID);
    public static final Identifier MEGUMIN_EXPLOSION_ID = Identifier.fromNamespaceAndPath(MeguminStaffMod.MOD_ID, "megumin_explosion");
    public static final SoundEvent MEGUMIN_EXPLOSION = SoundEvent.createVariableRangeEvent(MEGUMIN_EXPLOSION_ID);

    private ModSounds() {
    }

    public static void initialize() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, MEGUMIN_CAST_ID, MEGUMIN_CAST);
        Registry.register(BuiltInRegistries.SOUND_EVENT, MEGUMIN_EXPLOSION_ID, MEGUMIN_EXPLOSION);
    }
}
