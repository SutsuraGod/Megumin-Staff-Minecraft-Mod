package sutsura.megumin_staff.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sutsura.megumin_staff.item.staff.MeguminStaff;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {
	@Inject(
			method = "renderArmWithItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V")
	)
	private void moveStaffWhileCharging(
			AbstractClientPlayer player,
			float frameInterp,
			float xRot,
			InteractionHand hand,
			float attack,
			ItemStack itemStack,
			float inverseArmHeight,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			int lightCoords,
			CallbackInfo ci
	) {
		if (itemStack.getItem() instanceof MeguminStaff && player.isUsingItem() && player.getUseItem() == itemStack) {
			if (player.getTicksUsingItem() <= 15) {
				float progress = (player.getTicksUsingItem() + frameInterp) / 15.0F;
				progress = Math.min(progress, 1.0F);
				progress = progress * progress * (3.0F - 2.0F * progress);
				poseStack.mulPose(Axis.XP.rotationDegrees(-60.0F * progress));
			} else {
				poseStack.mulPose(Axis.XP.rotationDegrees(-60.0F));
			}
		}
	}
}