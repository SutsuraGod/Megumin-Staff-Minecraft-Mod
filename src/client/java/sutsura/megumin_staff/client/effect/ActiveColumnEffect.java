package sutsura.megumin_staff.client.effect;

import net.minecraft.world.phys.Vec3;

public final class ActiveColumnEffect {
    private final Vec3 center;
    private final long seed;
    private int age;

    ActiveColumnEffect(Vec3 center) {
        this.center = center;
        this.seed = mixSeed(center);
    }

    public Vec3 center() {
        return this.center;
    }

    long seed() {
        return this.seed;
    }

    public int age() {
        return this.age;
    }

    void tick() {
        this.age++;
    }

    private static long mixSeed(Vec3 center) {
        long x = Double.doubleToLongBits(center.x);
        long y = Double.doubleToLongBits(center.y);
        long z = Double.doubleToLongBits(center.z);
        long seed = x * 0x9E3779B97F4A7C15L;
        seed ^= y * 0xC2B2AE3D27D4EB4FL;
        seed ^= z * 0x165667B19E3779F9L;
        seed ^= seed >>> 33;
        seed *= 0xFF51AFD7ED558CCDL;
        seed ^= seed >>> 33;
        seed *= 0xC4CEB9FE1A85EC53L;
        seed ^= seed >>> 33;
        return seed;
    }
}
