package sutsura.megumin_staff.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class MagicCirclePhaseRenderer {
    static final int CIRCLE_START_DELAY_TICKS = 10;
    static final int CIRCLE_APPEAR_STAGGER_TICKS = 3;
    static final int CIRCLE_GROW_TICKS = 8;
    static final double[] OUTER_RADII = {3.4D, 8.6D, 4.1D, 4.8D, 13.2D, 5.2D};
    static final int FINAL_CIRCLE_FINISH_TICK = CIRCLE_START_DELAY_TICKS + (OUTER_RADII.length - 1) * CIRCLE_APPEAR_STAGGER_TICKS + CIRCLE_GROW_TICKS;
    static final int CIRCLE_HOLD_TICKS = 40;
    static final int CIRCLE_SHRINK_TICKS = 8;
    public static final int CIRCLE_END_TICK = FINAL_CIRCLE_FINISH_TICK + CIRCLE_HOLD_TICKS + CIRCLE_SHRINK_TICKS;

    private static final int CIRCLE_COUNT = OUTER_RADII.length;
    private static final double TOTAL_HEIGHT = 58.0D;
    private static final double BASE_HEIGHT = 1.5D;
    private static final int OUTER_SEGMENTS = 80;
    private static final int INNER_SEGMENTS = 52;
    private static final int PETAL_COUNT = 6;
    private static final int PETAL_SEGMENTS = 18;
    private static final float[][] CIRCLE_TINTS = {
            rgb(255, 224, 132),
            rgb(255, 166, 84),
            rgb(255, 118, 46)
    };

    void render(PoseStack poseStack, LevelRenderContext context, Vec3 camera, List<ActiveColumnEffect> effects) {
        VertexConsumer lineBuffer = context.bufferSource().getBuffer(RenderTypes.lines());
        for (ActiveColumnEffect effect : effects) {
            renderEffect(lineBuffer, poseStack, camera, effect);
        }
    }

    private void renderEffect(VertexConsumer buffer, PoseStack poseStack, Vec3 camera, ActiveColumnEffect effect) {
        if (effect.age() >= CIRCLE_END_TICK) {
            return;
        }

        boolean shrinking = effect.age() > FINAL_CIRCLE_FINISH_TICK + CIRCLE_HOLD_TICKS;
        float shrinkProgress = shrinking
                ? clamp01((effect.age() - (FINAL_CIRCLE_FINISH_TICK + CIRCLE_HOLD_TICKS)) / (float) CIRCLE_SHRINK_TICKS)
                : 0.0F;
        float phaseScale = shrinking ? (1.0F - (shrinkProgress * 0.88F)) : 1.0F;
        float fadeAlpha = shrinking ? (1.0F - shrinkProgress) : 1.0F;
        double verticalSpacing = TOTAL_HEIGHT / Math.max(1, CIRCLE_COUNT - 1);
        double time = effect.age() * 0.07D;

        for (int i = 0; i < CIRCLE_COUNT; i++) {
            int startTick = CIRCLE_START_DELAY_TICKS + i * CIRCLE_APPEAR_STAGGER_TICKS;
            float appearProgress = clamp01((effect.age() - startTick) / (float) CIRCLE_GROW_TICKS);
            if (appearProgress <= 0.0F) {
                continue;
            }

            float easedAppear = easeOutBack(appearProgress);
            float alpha = fadeAlpha * Math.min(1.0F, appearProgress * 1.25F);
            double scale = (0.12D + easedAppear * 0.88D) * phaseScale;
            double height = BASE_HEIGHT + i * verticalSpacing;
            double outerRadius = OUTER_RADII[i] * scale;
            double innerRadius = outerRadius * 0.34D;
            double spin = time * (1.0D + i * 0.12D) * (i % 2 == 0 ? 1.0D : -1.0D);
            Vec3 center = effect.center().add(0.0D, height, 0.0D).subtract(camera);
            GeometryCircleRenderer.renderRing(buffer, poseStack, center, outerRadius, spin, OUTER_SEGMENTS, CIRCLE_TINTS[1], alpha);
            GeometryCircleRenderer.renderRing(buffer, poseStack, center, innerRadius, -spin * 1.18D, INNER_SEGMENTS, CIRCLE_TINTS[0], alpha);
            GeometryCircleRenderer.renderPetals(buffer, poseStack, center, innerRadius, outerRadius, spin, CIRCLE_TINTS, alpha, PETAL_COUNT, PETAL_SEGMENTS);
        }
    }

    private static float[] rgb(int red, int green, int blue) {
        return new float[] {red / 255.0F, green / 255.0F, blue / 255.0F};
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static float easeOutBack(float t) {
        float overshoot = 1.70158F;
        float x = t - 1.0F;
        return 1.0F + (overshoot + 1.0F) * x * x * x + overshoot * x * x;
    }
}
