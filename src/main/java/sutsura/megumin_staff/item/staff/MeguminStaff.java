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
import org.jspecify.annotations.NonNull;

public class MeguminStaff extends Item {
    public MeguminStaff(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(final ItemStack itemStack, final LivingEntity user) {
        return 15 * 20;
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity entity) {
        createExplosion(level, entity);
        return itemStack;
    }

    @Override
    public @NonNull InteractionResult use(Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        player.startUsingItem(hand);

        return InteractionResult.SUCCESS;
    }

    private void createExplosion(Level level, LivingEntity player) {
        HitResult hit = player.pick(30.0D, 0.0f, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hit;

            BlockPos pos = blockHitResult.getBlockPos();

            level.explode(
                    player,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    8.0f,
                    Level.ExplosionInteraction.TNT
            );
        } else if (hit.getType() == HitResult.Type.MISS) {
            double lookedX = player.getX() + player.getLookAngle().x() * 30;
            double lookedY = player.getY() + player.getLookAngle().y() * 30;
            double lookedZ = player.getZ() + player.getLookAngle().z() * 30;

            level.explode(
                    player,
                    lookedX,
                    lookedY,
                    lookedZ,
                    8.0f,
                    Level.ExplosionInteraction.TNT
            );
        }
    }
}
