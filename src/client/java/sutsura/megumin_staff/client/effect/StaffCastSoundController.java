package sutsura.megumin_staff.client.effect;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import sutsura.megumin_staff.client.sound.MeguminCastSoundInstance;

public class StaffCastSoundController {
    private MeguminCastSoundInstance castSoundInstance;

    public void play(SoundManager soundManager, LocalPlayer player) {
        if (this.castSoundInstance != null && soundManager.isActive(this.castSoundInstance)) {
            return;
        }

        this.castSoundInstance = new MeguminCastSoundInstance(player);
        soundManager.play(this.castSoundInstance);
    }

    public void stop(SoundManager soundManager) {
        if (this.castSoundInstance == null) {
            return;
        }

        soundManager.stop(this.castSoundInstance);
        this.castSoundInstance = null;
    }
}
