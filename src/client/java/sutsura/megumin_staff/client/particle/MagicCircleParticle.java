package sutsura.megumin_staff.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class MagicCircleParticle extends SimpleAnimatedParticle {
    private static final float[] DEFAULT_COLOR = new float[] {1.0F, 0.74F, 0.28F};
    private final float baseQuadSize;

    protected MagicCircleParticle(ClientLevel level, double x, double y, double z, int lifetime, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0);
        this.lifetime = lifetime;
        this.scale(1.08F);
        this.baseQuadSize = this.quadSize;
        this.setAlpha(0.95F);
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        if (this.age >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) this.age / (float) Math.max(1, this.lifetime - 1);
        float pulse = 0.88F + 0.16F * Mth.sin(progress * (float) Math.PI);
        this.quadSize = this.baseQuadSize * pulse;
        this.setAlpha(0.42F + (1.0F - progress) * 0.56F);
        this.setSpriteFromAge(this.sprites);
        this.age++;
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
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double red, double green, double blue, RandomSource random) {
            MagicCircleParticle particle = new MagicCircleParticle(level, x, y, z, 18 + random.nextInt(5), this.sprites);
            float[] color = toColor(red, green, blue);
            particle.setColor(color[0], color[1], color[2]);
            particle.setFadeColor(toRgb(lighten(color)));
            return particle;
        }

        private static float[] toColor(double red, double green, double blue) {
            float redChannel = toChannel(red);
            float greenChannel = toChannel(green);
            float blueChannel = toChannel(blue);
            if (redChannel == 0.0F && greenChannel == 0.0F && blueChannel == 0.0F) {
                return DEFAULT_COLOR;
            }
            return new float[] {redChannel, greenChannel, blueChannel};
        }

        private static float toChannel(double value) {
            return (float) Math.max(0.0D, Math.min(1.0D, value));
        }

        private static float[] lighten(float[] color) {
            return new float[] {
                    Math.min(1.0F, color[0] + (1.0F - color[0]) * 0.35F),
                    Math.min(1.0F, color[1] + (1.0F - color[1]) * 0.35F),
                    Math.min(1.0F, color[2] + (1.0F - color[2]) * 0.35F)
            };
        }

        private static int toRgb(float[] color) {
            int red = Math.max(0, Math.min(255, Math.round(color[0] * 255.0F)));
            int green = Math.max(0, Math.min(255, Math.round(color[1] * 255.0F)));
            int blue = Math.max(0, Math.min(255, Math.round(color[2] * 255.0F)));
            return (red << 16) | (green << 8) | blue;
        }
    }
}
