package sutsura.megumin_staff.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import sutsura.megumin_staff.item.staff.MeguminStaff;
import sutsura.megumin_staff.sound.ModSounds;

public class MeguminCastSoundInstance extends AbstractTickableSoundInstance {
    private final LocalPlayer player;

    public MeguminCastSoundInstance(LocalPlayer player) {
        super(ModSounds.MEGUMIN_CAST, SoundSource.PLAYERS, RandomSource.create());
        this.player = player;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.looping = false;
        this.x = (float) player.getX();
        this.y = (float) player.getY();
        this.z = (float) player.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return !this.player.isSilent();
    }

    @Override
    public void tick() {
        if (this.player.isRemoved() || !this.player.isUsingItem() || !(this.player.getUseItem().getItem() instanceof MeguminStaff)) {
            this.stop();
            return;
        }

        this.x = (float) this.player.getX();
        this.y = (float) this.player.getY();
        this.z = (float) this.player.getZ();
    }
}
