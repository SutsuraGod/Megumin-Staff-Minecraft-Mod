package sutsura.megumin_staff.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ManaStreamParticle extends SimpleAnimatedParticle {
    private static final float RED = 0.08F;
    private static final float GREEN = 0.20F;
    private static final float BLUE = 0.85F;

    protected ManaStreamParticle(ClientLevel level, double x, double y, double z, int lifetime, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0);
        this.lifetime = lifetime;
        this.scale(0.72F);
        this.setColor(RED, GREEN, BLUE);
        this.setFadeColor(0x4A7CFF);
        this.setAlpha(0.98F);
        this.hasPhysics = false;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ManaStreamParticle(level, x, y, z, 7 + random.nextInt(3), this.sprites);
        }
    }
}
