package sutsura.megumin_staff.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class SparkleParticle extends SimpleAnimatedParticle {
    private static final float[] DEFAULT_COLOR = new float[] {1.0F, 1.0F, 1.0F};
    private final float baseQuadSize;

    protected SparkleParticle(ClientLevel level, double x, double y, double z, int lifetime, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0);
        this.lifetime = lifetime;
        this.scale(1.02F);
        this.baseQuadSize = this.quadSize;
        this.setAlpha(1.0F);
    }

    @Override
    public void setColor(int rgb) {
        super.setColor(rgb);
    }

    @Override
    public void setFadeColor(int rgb) {
        super.setFadeColor(rgb);
    }

    @Override
    public Layer getLayer() {
        return super.getLayer();
    }

    @Override
    public void tick() {
        if (this.age >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) this.age / (float) (this.lifetime - 1);
        progress = Math.min(progress, 1.0F);

        double eased = 0.5 + 0.5 * Math.sin((progress - 0.5) * Math.PI);

        int frame = (int) Math.round(eased * 5);
        frame = Math.min(frame, 5);

        this.setSprite(this.sprites.get(frame, 5));
        this.quadSize = this.baseQuadSize * (0.96F + (float) eased * 0.12F);
        this.setAlpha(0.72F + (float) eased * 0.28F);

        this.age++;
    }

    @Override
    public int getLightCoords(float a) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
            SparkleParticle particle = new SparkleParticle(level, x, y, z, 15, this.sprites);
            float[] color = toColor(xAux, yAux, zAux);
            float[] fadeColor = lighten(color);
            particle.setColor(color[0], color[1], color[2]);
            particle.setFadeColor(toRgb(fadeColor));
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
            float red = color[0];
            float green = color[1];
            float blue = color[2];

            red = Math.min(1.0F, red + (1.0F - red) * 0.55F);
            green = Math.min(1.0F, green + (1.0F - green) * 0.55F);
            blue = Math.min(1.0F, blue + (1.0F - blue) * 0.55F);

            return new float[] {red, green, blue};
        }

        private static int toRgb(float[] color) {
            int red = Math.max(0, Math.min(255, Math.round(color[0] * 255.0F)));
            int green = Math.max(0, Math.min(255, Math.round(color[1] * 255.0F)));
            int blue = Math.max(0, Math.min(255, Math.round(color[2] * 255.0F)));
            return (red << 16) | (green << 8) | blue;
        }
    }
}
