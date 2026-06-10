package sutsura.megumin_staff.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

final class ExplosionPhaseRenderer {
    static final int EXPLOSION_START_TICK = MagicCirclePhaseRenderer.CIRCLE_END_TICK - 1;
    static final int EXPLOSION_DURATION_TICKS = 124;
    private static final double EXPLOSION_CENTER_HEIGHT = 6.5D;
    private static final float[][] CORE_COLORS = {
            rgb(255, 251, 210),
            rgb(255, 210, 120),
            rgb(255, 158, 62)
    };
    private static final float[][] SHELL_COLORS = {
            rgb(255, 132, 48),
            rgb(255, 92, 34),
            rgb(214, 42, 26)
    };
    private static final float[][] DEBRIS_COLORS = {
            rgb(118, 22, 18),
            rgb(74, 12, 12),
            rgb(28, 10, 14)
    };

    void render(PoseStack poseStack, LevelRenderContext context, Vec3 camera, List<ActiveColumnEffect> effects) {
        RenderType fillRenderType = RenderTypes.debugQuads();
        BufferBuilder fillBuffer = Tesselator.getInstance().begin(fillRenderType.mode(), fillRenderType.format());
        VertexConsumer lineBuffer = context.bufferSource().getBuffer(RenderTypes.linesTranslucent());
        int renderedCubeCount = 0;

        for (ActiveColumnEffect effect : effects) {
            renderedCubeCount += renderEffect(poseStack, camera, fillBuffer, lineBuffer, effect);
        }

        if (renderedCubeCount > 0) {
            MeshData mesh = fillBuffer.build();
            if (mesh != null) {
                fillRenderType.draw(mesh);
                mesh.close();
            }
        }
    }

    private int renderEffect(PoseStack poseStack, Vec3 camera, VertexConsumer fillBuffer, VertexConsumer lineBuffer, ActiveColumnEffect effect) {
        if (effect.age() < EXPLOSION_START_TICK || effect.age() >= EXPLOSION_START_TICK + EXPLOSION_DURATION_TICKS) {
            return 0;
        }

        int localAge = effect.age() - EXPLOSION_START_TICK;
        Vec3 origin = effect.center().add(0.0D, EXPLOSION_CENTER_HEIGHT, 0.0D).subtract(camera);

        int rendered = 0;
        rendered += renderWave(fillBuffer, lineBuffer, poseStack, effect.seed(), origin, localAge, 0, 22, 34, 132, 0.85D, 7.2D, 0.55F, 2.35F, CORE_COLORS);
        rendered += renderWave(fillBuffer, lineBuffer, poseStack, effect.seed() ^ 0x9E3779B97F4A7C15L, origin, localAge, 2, 30, 44, 220, 3.6D, 17.6D, 0.70F, 3.25F, SHELL_COLORS);
        rendered += renderWave(fillBuffer, lineBuffer, poseStack, effect.seed() ^ 0xC2B2AE3D27D4EB4FL, origin, localAge, 8, 40, 52, 160, 9.2D, 25.6D, 0.80F, 4.60F, DEBRIS_COLORS);
        return rendered;
    }

    private static float[] rgb(int red, int green, int blue) {
        return new float[] {red / 255.0F, green / 255.0F, blue / 255.0F};
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private int renderWave(VertexConsumer fillBuffer, VertexConsumer lineBuffer, PoseStack poseStack, long seed, Vec3 origin,
                           int localAge, int waveStart, int spawnWindow, int cubeLifetime, int maxCubes,
                           double minRadius, double maxRadius, float minSize, float maxSize, float[][] palette) {
        if (localAge < waveStart) {
            return 0;
        }

        int ageInWave = localAge - waveStart;
        int visibleCubeCount = Math.min(maxCubes, Math.max(0, ((ageInWave + 1) * maxCubes) / Math.max(1, spawnWindow)));
        int rendered = 0;
        for (int cubeIndex = 0; cubeIndex < visibleCubeCount; cubeIndex++) {
            int spawnAge = (cubeIndex * spawnWindow) / Math.max(1, maxCubes);
            int cubeAge = ageInWave - spawnAge;
            if (cubeAge < 0 || cubeAge >= cubeLifetime) {
                continue;
            }

            renderCube(fillBuffer, lineBuffer, poseStack, seed, origin, cubeIndex, cubeAge, cubeLifetime, minRadius, maxRadius, minSize, maxSize, palette);
            rendered++;
        }

        return rendered;
    }

    private void renderCube(VertexConsumer fillBuffer, VertexConsumer lineBuffer, PoseStack poseStack, long seed, Vec3 origin,
                            int cubeIndex, int cubeAge, int cubeLifetime, double minRadius, double maxRadius,
                            float minSize, float maxSize, float[][] palette) {
        double normalizedAge = cubeAge / (double) Math.max(1, cubeLifetime - 1);
        double spawnTheta = randomSigned(seed, cubeIndex, 0) * Math.PI;
        double spawnPhi = randomRange(seed, cubeIndex, 1, -0.35D, 1.0D);
        double baseRadius = randomRange(seed, cubeIndex, 2, minRadius, maxRadius);
        double driftRadius = baseRadius * (0.28D + normalizedAge * 1.34D);
        double verticalLift = randomRange(seed, cubeIndex, 3, -0.65D, 1.3D) * (1.3D + normalizedAge * 4.6D);

        Vec3 direction = new Vec3(
                Math.cos(spawnTheta) * Math.cos(spawnPhi),
                Math.sin(spawnPhi),
                Math.sin(spawnTheta) * Math.cos(spawnPhi)
        ).normalize();

        Vec3 center = origin.add(direction.scale(driftRadius)).add(0.0D, verticalLift, 0.0D);
        float growth = (float) easeOutExpo(clamp01((float) (normalizedAge * 1.2D)));
        float fade = 1.0F - (float) Math.pow(normalizedAge, 1.45D);
        float halfSize = Mth.lerp(growth, minSize, maxSize);
        float[] color = palette[Math.floorMod((int) Math.round(randomSigned(seed, cubeIndex, 4) * 1000.0D), palette.length)];
        float outlineAlpha = fade * 0.9F;

        ExplosionCubeRenderer.renderSolidCube(fillBuffer, poseStack, center, halfSize, color, fade * 0.78F);
        ExplosionCubeRenderer.renderWireCube(lineBuffer, poseStack, center, halfSize, brighten(color, 0.22F), outlineAlpha);
    }

    private static float[] brighten(float[] color, float amount) {
        return new float[] {
                Math.min(1.0F, color[0] + amount),
                Math.min(1.0F, color[1] + amount),
                Math.min(1.0F, color[2] + amount)
        };
    }

    private static double easeOutExpo(float t) {
        return t >= 1.0F ? 1.0D : 1.0D - Math.pow(2.0D, -10.0D * t);
    }

    private static double randomRange(long seed, int cubeIndex, int salt, double min, double max) {
        double unit = (hash(seed, cubeIndex, salt) & 0xFFFFFFL) / (double) 0xFFFFFFL;
        return min + (max - min) * unit;
    }

    private static double randomSigned(long seed, int cubeIndex, int salt) {
        return randomRange(seed, cubeIndex, salt, -1.0D, 1.0D);
    }

    private static long hash(long seed, int cubeIndex, int salt) {
        long value = seed + cubeIndex * 0x9E3779B97F4A7C15L + salt * 0xC2B2AE3D27D4EB4FL;
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        value ^= value >>> 33;
        return value;
    }
}
