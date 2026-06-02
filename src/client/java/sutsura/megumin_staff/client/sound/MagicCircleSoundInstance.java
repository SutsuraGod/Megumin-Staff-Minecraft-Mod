package sutsura.megumin_staff.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import sutsura.megumin_staff.client.effect.ActiveColumnEffect;
import sutsura.megumin_staff.client.effect.MagicCirclePhaseRenderer;
import sutsura.megumin_staff.sound.ModSounds;

public class MagicCircleSoundInstance extends AbstractTickableSoundInstance {
    private final ActiveColumnEffect effect;

    public MagicCircleSoundInstance(ActiveColumnEffect effect) {
        super(ModSounds.MEGUMIN_CAST, SoundSource.PLAYERS, RandomSource.create());
        this.effect = effect;
        this.volume = 1.35F;
        this.pitch = 0.92F;
        this.looping = true;
        this.x = (float) effect.center().x;
        this.y = (float) effect.center().y;
        this.z = (float) effect.center().z;
    }

    @Override
    public void tick() {
        if (this.effect.age() > MagicCirclePhaseRenderer.CIRCLE_END_TICK) {
            this.stop();
            return;
        }

        this.x = (float) this.effect.center().x;
        this.y = (float) this.effect.center().y;
        this.z = (float) this.effect.center().z;
    }
}
