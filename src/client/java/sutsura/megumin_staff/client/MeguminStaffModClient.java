package sutsura.megumin_staff.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import sutsura.megumin_staff.client.effect.MagicCircleColumnRenderer;
import sutsura.megumin_staff.client.effect.StaffCastSoundController;
import sutsura.megumin_staff.client.effect.StaffParticleEffects;
import sutsura.megumin_staff.client.particle.MagicCircleParticle;
import sutsura.megumin_staff.client.particle.ManaStreamParticle;
import sutsura.megumin_staff.client.particle.SparkleParticle;
import sutsura.megumin_staff.client.particle.VortexParticle;
import sutsura.megumin_staff.item.staff.MeguminStaff;
import sutsura.megumin_staff.particle.ModParticles;

public class MeguminStaffModClient implements ClientModInitializer {
    private final StaffCastSoundController soundController = new StaffCastSoundController();
    private final StaffParticleEffects particleEffects = new StaffParticleEffects();
    private final MagicCircleColumnRenderer magicCircleRenderer = new MagicCircleColumnRenderer();
    private boolean wasUsingStaff;
    private boolean castCompleted;
    private Vec3 completedCastImpact;

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.SPARKLE_PARTICLE, SparkleParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.MANA_STREAM_PARTICLE, ManaStreamParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VORTEX_PARTICLE, VortexParticle.Provider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.MAGIC_CIRCLE_PARTICLE, MagicCircleParticle.Provider::new);
        ClientTickEvents.END_CLIENT_TICK.register(this::handleStaffEffects);
        LevelRenderEvents.END_MAIN.register(this.magicCircleRenderer::render);
    }

    private void handleStaffEffects(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            this.soundController.stop(client.getSoundManager());
            this.particleEffects.resetAll();
            this.magicCircleRenderer.reset();
            this.wasUsingStaff = false;
            this.castCompleted = false;
            this.completedCastImpact = null;
            return;
        }

        this.magicCircleRenderer.tick();
        boolean usingStaff = player != null && player.isUsingItem() && player.getUseItem().getItem() instanceof MeguminStaff;

        if (!usingStaff) {
            this.soundController.stop(client.getSoundManager());
            if (this.wasUsingStaff && this.castCompleted && this.completedCastImpact != null) {
                this.magicCircleRenderer.trigger(this.completedCastImpact);
            } else {
                this.particleEffects.resetCastEffects();
            }
            this.wasUsingStaff = false;
            this.castCompleted = false;
            this.completedCastImpact = null;
            return;
        }

        this.soundController.play(client.getSoundManager(), player);
        float castProgress = getCastProgress(player);
        this.particleEffects.tickCast(player, castProgress);
        this.wasUsingStaff = true;
        if (castProgress >= 0.999F) {
            this.castCompleted = true;
            this.completedCastImpact = this.particleEffects.getCastImpactPoint(player);
        }
    }

    private float getCastProgress(LocalPlayer player) {
        float castProgress = net.minecraft.util.Mth.clamp(player.getTicksUsingItem() / 300.0F, 0.0F, 1.0F);
        return castProgress * castProgress * (3.0F - 2.0F * castProgress);
    }
}
