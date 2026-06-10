package sutsura.megumin_staff.item.staff;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import sutsura.megumin_staff.sound.ModSounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class DelayedStaffExplosionManager {
    private static final int DETONATION_DELAY_TICKS = 80;
    private static final float EXPLOSION_RADIUS = 24.0F;
    private static final float LETHAL_DAMAGE = 750.0F;

    private static final List<PendingExplosion> PENDING_EXPLOSIONS = new ArrayList<>();

    private DelayedStaffExplosionManager() {
    }

    public static void schedule(ServerLevel level, Vec3 center, LivingEntity owner) {
        PENDING_EXPLOSIONS.add(new PendingExplosion(level.dimension(), center, owner.getUUID(), DETONATION_DELAY_TICKS));
    }

    public static void tick(MinecraftServer server) {
        Iterator<PendingExplosion> iterator = PENDING_EXPLOSIONS.iterator();
        while (iterator.hasNext()) {
            PendingExplosion pendingExplosion = iterator.next();
            if (!pendingExplosion.tickAndIsReady()) {
                continue;
            }

            ServerLevel level = server.getLevel(pendingExplosion.dimension());
            if (level != null) {
                detonate(level, pendingExplosion.center(), level.getEntity(pendingExplosion.ownerUuid()));
            }
            iterator.remove();
        }
    }

    private static void detonate(ServerLevel level, Vec3 center, Entity owner) {
        DamageSource explosionDamage = level.damageSources().explosion(owner, owner);
        level.playSound(null, center.x, center.y, center.z, ModSounds.MEGUMIN_EXPLOSION, SoundSource.PLAYERS, 8.0F, 1.0F);
        level.explode(owner, explosionDamage, null, center.x, center.y, center.z, EXPLOSION_RADIUS, true, Level.ExplosionInteraction.TNT);

        AABB damageArea = new AABB(center, center).inflate(EXPLOSION_RADIUS);
        for (Entity entity : level.getEntities(owner, damageArea, entity -> entity.isAlive())) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.hurtServer(level, explosionDamage, LETHAL_DAMAGE);
            }
        }

        for (EnderDragon dragon : level.getDragons()) {
            if (dragon.isAlive() && dragon.position().distanceTo(center) <= EXPLOSION_RADIUS + 16.0D) {
                dragon.hurtServer(level, explosionDamage, LETHAL_DAMAGE);
            }
        }
    }

    private static final class PendingExplosion {
        private final net.minecraft.resources.ResourceKey<Level> dimension;
        private final Vec3 center;
        private final UUID ownerUuid;
        private int ticksRemaining;

        private PendingExplosion(net.minecraft.resources.ResourceKey<Level> dimension, Vec3 center, UUID ownerUuid, int ticksRemaining) {
            this.dimension = dimension;
            this.center = center;
            this.ownerUuid = ownerUuid;
            this.ticksRemaining = ticksRemaining;
        }

        private net.minecraft.resources.ResourceKey<Level> dimension() {
            return this.dimension;
        }

        private Vec3 center() {
            return this.center;
        }

        private UUID ownerUuid() {
            return this.ownerUuid;
        }

        private boolean tickAndIsReady() {
            this.ticksRemaining--;
            return this.ticksRemaining <= 0;
        }
    }
}
