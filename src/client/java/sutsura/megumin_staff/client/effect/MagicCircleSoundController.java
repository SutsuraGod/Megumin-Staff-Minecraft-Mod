package sutsura.megumin_staff.client.effect;

import net.minecraft.client.sounds.SoundManager;
import sutsura.megumin_staff.client.sound.MagicCircleSoundInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class MagicCircleSoundController {
    private final List<MagicCircleSoundInstance> activeSounds = new ArrayList<>();

    void play(SoundManager soundManager, ActiveColumnEffect effect) {
        Iterator<MagicCircleSoundInstance> iterator = this.activeSounds.iterator();
        while (iterator.hasNext()) {
            MagicCircleSoundInstance sound = iterator.next();
            if (!soundManager.isActive(sound)) {
                iterator.remove();
            }
        }

        MagicCircleSoundInstance soundInstance = new MagicCircleSoundInstance(effect);
        this.activeSounds.add(soundInstance);
        soundManager.play(soundInstance);
    }

    void stopAll(SoundManager soundManager) {
        for (MagicCircleSoundInstance sound : this.activeSounds) {
            soundManager.stop(sound);
        }
        this.activeSounds.clear();
    }
}
