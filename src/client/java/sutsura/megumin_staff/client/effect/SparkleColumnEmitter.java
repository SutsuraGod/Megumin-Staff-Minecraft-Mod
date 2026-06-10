package sutsura.megumin_staff.client.effect;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import sutsura.megumin_staff.particle.ModParticles;

final class SparkleColumnEmitter {
    private static final double SPARKLE_COLUMN_HEIGHT = 52.0D;
    private static final double SPARKLE_COLUMN_RADIUS = 1.35D;
    private static final int SPARKLES_PER_TICK = 46;
    private static final float[][] SPARKLE_COLORS = {
            rgb(255, 105, 180),
            rgb(186, 85, 211),
            rgb(90, 160, 255),
            rgb(64, 224, 208),
            rgb(173, 255, 47),
            rgb(255, 223, 64)
    };

    void emit(ClientLevel level, ActiveColumnEffect effect) {
        RandomSource random = level.getRandom();
        float intensity = effect.age() < MagicCirclePhaseRenderer.CIRCLE_START_DELAY_TICKS
                ? 1.0F
                : 1.0F - Mth.clamp((effect.age() - MagicCirclePhaseRenderer.CIRCLE_START_DELAY_TICKS) / (float) Math.max(1, MagicCirclePhaseRenderer.FINAL_CIRCLE_FINISH_TICK - MagicCirclePhaseRenderer.CIRCLE_START_DELAY_TICKS), 0.0F, 0.72F);
        int particleCount = Math.max(18, Math.round(SPARKLES_PER_TICK * intensity));

        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            double radius = random.nextDouble() * SPARKLE_COLUMN_RADIUS;
            double x = effect.center().x + Math.cos(angle) * radius;
            double y = effect.center().y + random.nextDouble() * SPARKLE_COLUMN_HEIGHT;
            double z = effect.center().z + Math.sin(angle) * radius;
            float[] color = SPARKLE_COLORS[random.nextInt(SPARKLE_COLORS.length)];
            level.addParticle(ModParticles.SPARKLE_PARTICLE, x, y, z, color[0], color[1], color[2]);
        }
    }

    private static float[] rgb(int red, int green, int blue) {
        return new float[] {red / 255.0F, green / 255.0F, blue / 255.0F};
    }
}
