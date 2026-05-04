package sutsura.megumin_staff.client.effect;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;
import sutsura.megumin_staff.particle.ModParticles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StaffParticleEffects {
    private static final int MIN_PARTICLES_PER_TICK = 4;
    private static final int MAX_PARTICLES_PER_TICK = 16;
    private static final int THIRD_PERSON_STREAMS_PER_TICK = 1;
    private static final int MIN_STREAM_SEGMENTS = 6;
    private static final int MAX_STREAM_SEGMENTS = 12;
    private static final double MIN_STREAM_SPEED = 0.024D;
    private static final double MAX_STREAM_SPEED = 0.05D;
    private static final double STREAM_TRAIL_LENGTH = 0.24D;
    private static final int VORTEXES_PER_TICK = 1;
    private static final int MIN_VORTEX_SEGMENTS = 8;
    private static final int MAX_VORTEX_SEGMENTS = 14;
    private static final double MIN_VORTEX_SPEED = 0.07D;
    private static final double MAX_VORTEX_SPEED = 0.14D;
    private static final double VORTEX_TRAIL_LENGTH = 0.32D;
    private static final double MIN_PARTICLE_RADIUS = 1.4D;
    private static final double MAX_PARTICLE_RADIUS = 5.0D;
    private static final float[][] PARTICLE_COLORS = {
            rgb(255, 105, 180),
            rgb(186, 85, 211),
            rgb(90, 160, 255),
            rgb(64, 224, 208),
            rgb(173, 255, 47),
            rgb(255, 223, 64)
    };

    private final List<ActiveManaStream> activeManaStreams = new ArrayList<>();
    private final List<ActiveVortexStream> activeVortexStreams = new ArrayList<>();

    public void reset() {
        this.activeManaStreams.clear();
        this.activeVortexStreams.clear();
    }

    public void tick(LocalPlayer player, float castProgress) {
        spawnStaffParticles(player, castProgress);
        updateManaStreams(player);
        updateVortexStreams(player);
    }

    private void spawnStaffParticles(LocalPlayer player, float castProgress) {
        int particleCount = Mth.lerpInt(castProgress, MIN_PARTICLES_PER_TICK, MAX_PARTICLES_PER_TICK);
        RandomSource random = player.getRandom();
        double centerX = player.getX();
        double centerY = player.getY() + 0.35D;
        double centerZ = player.getZ();

        for (int i = 0; i < particleCount; i++) {
            double theta = random.nextDouble() * (Math.PI * 2.0D);
            double phi = random.nextDouble() * (Math.PI / 2.0D);
            double radius = MIN_PARTICLE_RADIUS + random.nextDouble() * (MAX_PARTICLE_RADIUS - MIN_PARTICLE_RADIUS);
            double horizontal = Math.cos(phi) * radius;
            double x = centerX + Math.cos(theta) * horizontal;
            double y = centerY + Math.sin(phi) * radius;
            double z = centerZ + Math.sin(theta) * horizontal;

            float[] color = PARTICLE_COLORS[random.nextInt(PARTICLE_COLORS.length)];
            player.level().addParticle(ModParticles.SPARKLE_PARTICLE, x, y, z, color[0], color[1], color[2]);
        }
    }

    private void updateManaStreams(LocalPlayer player) {
        RandomSource random = player.getRandom();
        Vec3 center = player.position().add(0.0D, player.getBbHeight() * 0.55D, 0.0D);
        Vec3 target = getStaffCrystalTarget(player);
        CameraType cameraType = Minecraft.getInstance().options.getCameraType();
        int streamsPerTick = cameraType.isFirstPerson() ? 0 : THIRD_PERSON_STREAMS_PER_TICK;

        if (cameraType.isFirstPerson() && player.tickCount % 2 == 0) {
            streamsPerTick = 1;
        }

        for (int i = 0; i < streamsPerTick; i++) {
            this.activeManaStreams.add(createManaStream(random, center, target));
        }

        Iterator<ActiveManaStream> iterator = this.activeManaStreams.iterator();
        while (iterator.hasNext()) {
            ActiveManaStream stream = iterator.next();
            stream.end = target;
            stream.progress += stream.speed;
            spawnStreamSegments(player, stream, random);

            if (stream.progress >= 1.0D) {
                iterator.remove();
            }
        }
    }

    private void updateVortexStreams(LocalPlayer player) {
        RandomSource random = player.getRandom();
        Vec3 center = player.position().add(0.0D, 0.05D, 0.0D);

        for (int i = 0; i < VORTEXES_PER_TICK; i++) {
            this.activeVortexStreams.add(createVortexStream(random));
        }

        Iterator<ActiveVortexStream> iterator = this.activeVortexStreams.iterator();
        while (iterator.hasNext()) {
            ActiveVortexStream vortex = iterator.next();
            vortex.phase += vortex.angularSpeed;
            vortex.age++;
            spawnVortexSegments(player, center, vortex, random);

            if (vortex.age >= vortex.lifetime) {
                iterator.remove();
            }
        }
    }

    private ActiveManaStream createManaStream(RandomSource random, Vec3 center, Vec3 target) {
        Vec3 start = randomSpherePoint(random, center);
        Vec3 radial = start.subtract(center);
        if (radial.lengthSqr() < 1.0E-4D) {
            radial = new Vec3(1.0D, 0.0D, 0.0D);
        }
        radial = radial.normalize();

        Vec3 tangent = radial.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (tangent.lengthSqr() < 1.0E-4D) {
            tangent = new Vec3(1.0D, 0.0D, 0.0D);
        }
        tangent = tangent.normalize().scale(random.nextBoolean() ? 1.0D : -1.0D);

        Vec3 midpoint = start.lerp(target, 0.42D);
        Vec3 control = midpoint
                .add(tangent.scale(0.45D + random.nextDouble() * 0.4D))
                .add(radial.scale(0.28D + random.nextDouble() * 0.32D))
                .add(0.0D, Mth.nextDouble(random, -0.05D, 0.24D), 0.0D);

        double speed = Mth.nextDouble(random, MIN_STREAM_SPEED, MAX_STREAM_SPEED);
        int segmentCount = random.nextInt(MIN_STREAM_SEGMENTS, MAX_STREAM_SEGMENTS + 1);
        return new ActiveManaStream(start, control, target, speed, segmentCount);
    }

    private ActiveVortexStream createVortexStream(RandomSource random) {
        double elevation = Mth.nextDouble(random, 0.12D, 1.22D);
        double startAngle = random.nextDouble() * (Math.PI * 2.0D);
        double angularSpeed = Mth.nextDouble(random, MIN_VORTEX_SPEED, MAX_VORTEX_SPEED) * (random.nextBoolean() ? 1.0D : -1.0D);
        int segmentCount = random.nextInt(MIN_VORTEX_SEGMENTS, MAX_VORTEX_SEGMENTS + 1);
        int lifetime = 24 + random.nextInt(24);
        return new ActiveVortexStream(MAX_PARTICLE_RADIUS, elevation, startAngle, angularSpeed, segmentCount, lifetime);
    }

    private void spawnStreamSegments(LocalPlayer player, ActiveManaStream stream, RandomSource random) {
        double head = Math.min(stream.progress, 1.0D);
        double tail = Math.max(0.0D, head - STREAM_TRAIL_LENGTH);

        for (int i = 0; i < stream.segmentCount; i++) {
            double t = Mth.lerp((double) i / Math.max(1, stream.segmentCount - 1), tail, head);
            Vec3 point = quadraticBezier(stream.start, stream.control, stream.end, t);
            double jitterScale = 0.012D * (1.0D - t);
            double jitterX = Mth.nextDouble(random, -jitterScale, jitterScale);
            double jitterY = Mth.nextDouble(random, -jitterScale, jitterScale);
            double jitterZ = Mth.nextDouble(random, -jitterScale, jitterScale);
            player.level().addParticle(ModParticles.MANA_STREAM_PARTICLE, point.x + jitterX, point.y + jitterY, point.z + jitterZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void spawnVortexSegments(LocalPlayer player, Vec3 center, ActiveVortexStream vortex, RandomSource random) {
        double head = vortex.phase;
        double tail = vortex.phase - (Math.signum(vortex.angularSpeed) * VORTEX_TRAIL_LENGTH);

        for (int i = 0; i < vortex.segmentCount; i++) {
            double progress = (double) i / Math.max(1, vortex.segmentCount - 1);
            double angle = Mth.lerp(progress, tail, head);
            double horizontal = Math.cos(vortex.elevation) * vortex.radius;
            double x = center.x + Math.cos(angle) * horizontal;
            double y = center.y + Math.sin(vortex.elevation) * vortex.radius;
            double z = center.z + Math.sin(angle) * horizontal;

            double jitterScale = 0.004D;
            double jitterX = Mth.nextDouble(random, -jitterScale, jitterScale);
            double jitterY = Mth.nextDouble(random, -jitterScale, jitterScale);
            double jitterZ = Mth.nextDouble(random, -jitterScale, jitterScale);
            player.level().addParticle(ModParticles.VORTEX_PARTICLE, x + jitterX, y + jitterY, z + jitterZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private Vec3 randomSpherePoint(RandomSource random, Vec3 center) {
        double theta = random.nextDouble() * (Math.PI * 2.0D);
        double phi = Math.acos(1.0D - 2.0D * random.nextDouble());
        double radius = MIN_PARTICLE_RADIUS + random.nextDouble() * (MAX_PARTICLE_RADIUS - MIN_PARTICLE_RADIUS);
        double sinPhi = Math.sin(phi);
        return center.add(
                Math.cos(theta) * sinPhi * radius,
                Math.cos(phi) * radius,
                Math.sin(theta) * sinPhi * radius
        );
    }

    private Vec3 getStaffCrystalTarget(LocalPlayer player) {
        CameraType cameraType = Minecraft.getInstance().options.getCameraType();
        Vec3 forward;
        Vec3 right;

        if (cameraType.isFirstPerson()) {
            forward = player.getLookAngle().normalize();
            right = new Vec3(forward.z, 0.0D, -forward.x);
            if (right.lengthSqr() < 1.0E-4D) {
                right = new Vec3(1.0D, 0.0D, 0.0D);
            } else {
                right = right.normalize();
            }
        } else {
            float bodyYawRadians = player.yBodyRot * ((float) Math.PI / 180.0F);
            forward = new Vec3(-Mth.sin(bodyYawRadians), 0.0D, Mth.cos(bodyYawRadians));
            right = new Vec3(-forward.z, 0.0D, forward.x).normalize();
        }

        int armSign = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        if (player.getUsedItemHand() == InteractionHand.OFF_HAND) {
            armSign *= -1;
        }

        return player.getEyePosition()
                .add(forward.scale(cameraType.isFirstPerson() ? 0.85D : 0.9D))
                .add(right.scale((cameraType.isFirstPerson() ? -0.6D : 0.42D) * armSign))
                .add(0.0D, -0.3D, 0.0D);
    }

    private Vec3 quadraticBezier(Vec3 start, Vec3 control, Vec3 end, double t) {
        double oneMinusT = 1.0D - t;
        return start.scale(oneMinusT * oneMinusT)
                .add(control.scale(2.0D * oneMinusT * t))
                .add(end.scale(t * t));
    }

    private static float[] rgb(int red, int green, int blue) {
        return new float[] {
                Mth.clamp(red / 255.0F, 0.0F, 1.0F),
                Mth.clamp(green / 255.0F, 0.0F, 1.0F),
                Mth.clamp(blue / 255.0F, 0.0F, 1.0F)
        };
    }

    private static class ActiveManaStream {
        private final Vec3 start;
        private final Vec3 control;
        private Vec3 end;
        private final double speed;
        private final int segmentCount;
        private double progress;

        private ActiveManaStream(Vec3 start, Vec3 control, Vec3 end, double speed, int segmentCount) {
            this.start = start;
            this.control = control;
            this.end = end;
            this.speed = speed;
            this.segmentCount = segmentCount;
            this.progress = 0.0D;
        }
    }

    private static class ActiveVortexStream {
        private final double radius;
        private final double elevation;
        private double phase;
        private final double angularSpeed;
        private final int segmentCount;
        private final int lifetime;
        private int age;

        private ActiveVortexStream(double radius, double elevation, double phase, double angularSpeed, int segmentCount, int lifetime) {
            this.radius = radius;
            this.elevation = elevation;
            this.phase = phase;
            this.angularSpeed = angularSpeed;
            this.segmentCount = segmentCount;
            this.lifetime = lifetime;
            this.age = 0;
        }
    }
}
