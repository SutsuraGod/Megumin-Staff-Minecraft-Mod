package sutsura.megumin_staff.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import sutsura.megumin_staff.client.effect.StaffCastSoundController;
import sutsura.megumin_staff.client.effect.StaffParticleEffects;
import sutsura.megumin_staff.client.particle.ManaStreamParticle;
import sutsura.megumin_staff.client.particle.SparkleParticle;
import sutsura.megumin_staff.client.particle.VortexParticle;
import sutsura.megumin_staff.item.staff.MeguminStaff;
import sutsura.megumin_staff.particle.ModParticles;

public class MeguminStaffModClient implements ClientModInitializer {
    private final StaffCastSoundController soundController = new StaffCastSoundController();
    private final StaffParticleEffects particleEffects = new StaffParticleEffects();

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.SPARKLE_PARTICLE, SparkleParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.MANA_STREAM_PARTICLE, ManaStreamParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VORTEX_PARTICLE, VortexParticle.Provider::new);
        ClientTickEvents.END_CLIENT_TICK.register(this::handleStaffEffects);
    }

    private void handleStaffEffects(Minecraft client) {
        LocalPlayer player = client.player;
        boolean usingStaff = player != null && player.isUsingItem() && player.getUseItem().getItem() instanceof MeguminStaff;

        if (!usingStaff) {
            this.soundController.stop(client.getSoundManager());
            this.particleEffects.reset();
            return;
        }

        this.soundController.play(client.getSoundManager(), player);
        float castProgress = getCastProgress(player);
        this.particleEffects.tick(player, castProgress);
    }

    private float getCastProgress(LocalPlayer player) {
        float castProgress = net.minecraft.util.Mth.clamp(player.getTicksUsingItem() / 300.0F, 0.0F, 1.0F);
        return castProgress * castProgress * (3.0F - 2.0F * castProgress);
    }
}
