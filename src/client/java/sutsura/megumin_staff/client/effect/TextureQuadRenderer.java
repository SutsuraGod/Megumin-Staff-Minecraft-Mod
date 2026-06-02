package sutsura.megumin_staff.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

final class TextureQuadRenderer {
    private TextureQuadRenderer() {
    }

    static void renderHorizontalQuad(VertexConsumer buffer, PoseStack poseStack, Vec3 center, double width, double depth, double rotation,
                                     float[] color, float alpha, float u0, float v0, float u1, float v1) {
        PoseStack.Pose pose = poseStack.last();
        double halfWidth = width * 0.5D;
        double halfDepth = depth * 0.5D;
        double sin = Math.sin(rotation);
        double cos = Math.cos(rotation);

        Vec3 p0 = rotateHorizontal(center, -halfWidth, -halfDepth, sin, cos);
        Vec3 p1 = rotateHorizontal(center, -halfWidth, halfDepth, sin, cos);
        Vec3 p2 = rotateHorizontal(center, halfWidth, halfDepth, sin, cos);
        Vec3 p3 = rotateHorizontal(center, halfWidth, -halfDepth, sin, cos);
        Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
        int argb = toArgb(color, alpha);

        addTexturedVertex(buffer, pose, p0, argb, u0, v0, normal);
        addTexturedVertex(buffer, pose, p1, argb, u0, v1, normal);
        addTexturedVertex(buffer, pose, p2, argb, u1, v1, normal);
        addTexturedVertex(buffer, pose, p3, argb, u1, v0, normal);
    }

    static void renderBillboardQuad(VertexConsumer buffer, PoseStack poseStack, Vec3 center, double width, double height,
                                    float[] color, float alpha, float u0, float v0, float u1, float v1) {
        PoseStack.Pose pose = poseStack.last();
        Vec3 toCamera = new Vec3(-center.x, 0.0D, -center.z);
        if (toCamera.lengthSqr() < 1.0E-5D) {
            toCamera = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            toCamera = toCamera.normalize();
        }

        Vec3 right = new Vec3(-toCamera.z, 0.0D, toCamera.x).normalize().scale(width * 0.5D);
        Vec3 up = new Vec3(0.0D, height * 0.5D, 0.0D);
        Vec3 p0 = center.subtract(right).subtract(up);
        Vec3 p1 = center.subtract(right).add(up);
        Vec3 p2 = center.add(right).add(up);
        Vec3 p3 = center.add(right).subtract(up);
        Vector3f normal = new Vector3f((float) toCamera.x, 0.0F, (float) toCamera.z);
        int argb = toArgb(color, alpha);

        addTexturedVertex(buffer, pose, p0, argb, u0, v1, normal);
        addTexturedVertex(buffer, pose, p1, argb, u0, v0, normal);
        addTexturedVertex(buffer, pose, p2, argb, u1, v0, normal);
        addTexturedVertex(buffer, pose, p3, argb, u1, v1, normal);
    }

    private static void addTexturedVertex(VertexConsumer buffer, PoseStack.Pose pose, Vec3 point, int argb, float u, float v, Vector3f normal) {
        buffer.addVertex(pose, (float) point.x, (float) point.y, (float) point.z)
                .setColor(argb)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(pose, normal);
    }

    private static Vec3 rotateHorizontal(Vec3 center, double localX, double localZ, double sin, double cos) {
        double x = localX * cos - localZ * sin;
        double z = localX * sin + localZ * cos;
        return new Vec3(center.x + x, center.y, center.z + z);
    }

    private static int toArgb(float[] color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
        int r = Math.max(0, Math.min(255, Math.round(color[0] * 255.0F)));
        int g = Math.max(0, Math.min(255, Math.round(color[1] * 255.0F)));
        int b = Math.max(0, Math.min(255, Math.round(color[2] * 255.0F)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
