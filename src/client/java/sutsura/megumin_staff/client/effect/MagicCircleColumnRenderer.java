package sutsura.megumin_staff.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagicCircleColumnRenderer {
    private static final int COLUMN_LIFETIME_TICKS = ExplosionPhaseRenderer.EXPLOSION_START_TICK + ExplosionPhaseRenderer.EXPLOSION_DURATION_TICKS + 20;

    private final List<ActiveColumnEffect> activeEffects = new ArrayList<>();
    private final SparkleColumnEmitter sparkleColumnEmitter = new SparkleColumnEmitter();
    private final MagicCircleSoundController soundController = new MagicCircleSoundController();
    private final MagicCirclePhaseRenderer magicCirclePhaseRenderer = new MagicCirclePhaseRenderer();
    private final ExplosionPhaseRenderer explosionPhaseRenderer = new ExplosionPhaseRenderer();

    public void trigger(Vec3 center) {
        ActiveColumnEffect effect = new ActiveColumnEffect(center);
        this.activeEffects.add(effect);
        this.soundController.play(Minecraft.getInstance().getSoundManager(), effect);
    }

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Iterator<ActiveColumnEffect> iterator = this.activeEffects.iterator();
        while (iterator.hasNext()) {
            ActiveColumnEffect effect = iterator.next();
            if (level != null && effect.age() <= MagicCirclePhaseRenderer.FINAL_CIRCLE_FINISH_TICK) {
                this.sparkleColumnEmitter.emit(level, effect);
            }
            effect.tick();
            if (effect.age() >= COLUMN_LIFETIME_TICKS) {
                iterator.remove();
            }
        }
    }

    public void reset() {
        this.activeEffects.clear();
        this.soundController.stopAll(Minecraft.getInstance().getSoundManager());
    }

    public void render(LevelRenderContext context) {
        if (this.activeEffects.isEmpty()) {
            return;
        }

        PoseStack poseStack = context.poseStack();
        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().position();

        poseStack.pushPose();
        this.magicCirclePhaseRenderer.render(poseStack, context, cameraPosition, this.activeEffects);
        context.bufferSource().endBatch(RenderTypes.lines());
        this.explosionPhaseRenderer.render(poseStack, context, cameraPosition, this.activeEffects);
        context.bufferSource().endBatch();
        poseStack.popPose();
    }
}
