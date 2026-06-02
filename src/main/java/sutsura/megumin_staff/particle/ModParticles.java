package sutsura.megumin_staff.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import sutsura.megumin_staff.MeguminStaffMod;

public class ModParticles {
    public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType MANA_STREAM_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType VORTEX_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType MAGIC_CIRCLE_PARTICLE = FabricParticleTypes.simple();

    public static void initialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(MeguminStaffMod.MOD_ID, "sparkle_particle"), SPARKLE_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(MeguminStaffMod.MOD_ID, "mana_stream_particle"), MANA_STREAM_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(MeguminStaffMod.MOD_ID, "vortex_particle"), VORTEX_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(MeguminStaffMod.MOD_ID, "magic_circle_particle"), MAGIC_CIRCLE_PARTICLE);
    }
}
