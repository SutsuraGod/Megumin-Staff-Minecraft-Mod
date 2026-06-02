package sutsura.megumin_staff.item.staff;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class MeguminStaff extends Item {
    private static final int CAST_DURATION_TICKS = 15 * 20;

    public MeguminStaff(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(final ItemStack itemStack, final LivingEntity user) {
        return CAST_DURATION_TICKS;
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity entity) {
        scheduleExplosion(level, entity);
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(itemStack, CAST_DURATION_TICKS);
        }
        return itemStack;
    }

    @Override
    public @NonNull InteractionResult use(Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        player.startUsingItem(hand);

        return InteractionResult.SUCCESS;
    }

    private void scheduleExplosion(Level level, LivingEntity player) {
        if (level.isClientSide() || !(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }

        HitResult hit = player.pick(30.0D, 0.0f, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hit;
            DelayedStaffExplosionManager.schedule(serverLevel, blockHitResult.getLocation(), player);
        } else if (hit.getType() == HitResult.Type.MISS) {
            double lookedX = player.getX() + player.getLookAngle().x() * 30;
            double lookedY = player.getY() + player.getLookAngle().y() * 30;
            double lookedZ = player.getZ() + player.getLookAngle().z() * 30;
            DelayedStaffExplosionManager.schedule(serverLevel, new Vec3(lookedX, lookedY, lookedZ), player);
        }
    }
}
