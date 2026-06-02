package sutsura.megumin_staff.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

final class ExplosionCubeRenderer {
    private ExplosionCubeRenderer() {
    }

    static void renderSolidCube(VertexConsumer buffer, PoseStack poseStack, Vec3 center, float halfSize, float[] color, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        int argb = toArgb(color, alpha);

        float minX = (float) center.x - halfSize;
        float maxX = (float) center.x + halfSize;
        float minY = (float) center.y - halfSize;
        float maxY = (float) center.y + halfSize;
        float minZ = (float) center.z - halfSize;
        float maxZ = (float) center.z + halfSize;

        renderFace(buffer, pose, minX, minY, maxZ, maxX, maxY, maxZ, argb, 0.0F, 0.0F, 1.0F);
        renderFace(buffer, pose, maxX, minY, minZ, minX, maxY, minZ, argb, 0.0F, 0.0F, -1.0F);
        renderFace(buffer, pose, minX, minY, minZ, minX, maxY, maxZ, argb, -1.0F, 0.0F, 0.0F);
        renderFace(buffer, pose, maxX, minY, maxZ, maxX, maxY, minZ, argb, 1.0F, 0.0F, 0.0F);
        renderHorizontalFace(buffer, pose, minX, maxX, maxY, minZ, maxZ, argb, 0.0F, 1.0F, 0.0F);
        renderHorizontalFace(buffer, pose, minX, maxX, minY, maxZ, minZ, argb, 0.0F, -1.0F, 0.0F);
    }

    static void renderWireCube(VertexConsumer buffer, PoseStack poseStack, Vec3 center, float halfSize, float[] color, float alpha) {
        float minX = (float) center.x - halfSize;
        float maxX = (float) center.x + halfSize;
        float minY = (float) center.y - halfSize;
        float maxY = (float) center.y + halfSize;
        float minZ = (float) center.z - halfSize;
        float maxZ = (float) center.z + halfSize;

        Vec3 p000 = new Vec3(minX, minY, minZ);
        Vec3 p001 = new Vec3(minX, minY, maxZ);
        Vec3 p010 = new Vec3(minX, maxY, minZ);
        Vec3 p011 = new Vec3(minX, maxY, maxZ);
        Vec3 p100 = new Vec3(maxX, minY, minZ);
        Vec3 p101 = new Vec3(maxX, minY, maxZ);
        Vec3 p110 = new Vec3(maxX, maxY, minZ);
        Vec3 p111 = new Vec3(maxX, maxY, maxZ);

        renderLine(buffer, poseStack, p000, p001, color, alpha);
        renderLine(buffer, poseStack, p001, p011, color, alpha);
        renderLine(buffer, poseStack, p011, p010, color, alpha);
        renderLine(buffer, poseStack, p010, p000, color, alpha);

        renderLine(buffer, poseStack, p100, p101, color, alpha);
        renderLine(buffer, poseStack, p101, p111, color, alpha);
        renderLine(buffer, poseStack, p111, p110, color, alpha);
        renderLine(buffer, poseStack, p110, p100, color, alpha);

        renderLine(buffer, poseStack, p000, p100, color, alpha);
        renderLine(buffer, poseStack, p001, p101, color, alpha);
        renderLine(buffer, poseStack, p010, p110, color, alpha);
        renderLine(buffer, poseStack, p011, p111, color, alpha);
    }

    private static void renderFace(VertexConsumer buffer, PoseStack.Pose pose, float x0, float y0, float z0, float x1, float y1, float z1,
                                   int argb, float normalX, float normalY, float normalZ) {
        Vector3f normal = new Vector3f(normalX, normalY, normalZ);
        buffer.addVertex(pose, x0, y0, z0).setColor(argb).setNormal(pose, normal);
        buffer.addVertex(pose, x0, y1, z0).setColor(argb).setNormal(pose, normal);
        buffer.addVertex(pose, x1, y1, z1).setColor(argb).setNormal(pose, normal);
        buffer.addVertex(pose, x1, y0, z1).setColor(argb).setNormal(pose, normal);
    }

    private static void renderHorizontalFace(VertexConsumer buffer, PoseStack.Pose pose, float minX, float maxX, float y, float minZ, float maxZ,
                                             int argb, float normalX, float normalY, float normalZ) {
        Vector3f normal = new Vector3f(normalX, normalY, normalZ);
        buffer.addVertex(pose, minX, y, minZ).setColor(argb).setNormal(pose, normal);
        buffer.addVertex(pose, minX, y, maxZ).setColor(argb).setNormal(pose, normal);
        buffer.addVertex(pose, maxX, y, maxZ).setColor(argb).setNormal(pose, normal);
        buffer.addVertex(pose, maxX, y, minZ).setColor(argb).setNormal(pose, normal);
    }

    private static void renderLine(VertexConsumer buffer, PoseStack poseStack, Vec3 start, Vec3 end, float[] color, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        float dx = (float) (end.x - start.x);
        float dy = (float) (end.y - start.y);
        float dz = (float) (end.z - start.z);
        float length = Math.max(0.0001F, (float) Math.sqrt(dx * dx + dy * dy + dz * dz));
        Vector3f normal = new Vector3f(dx / length, dy / length, dz / length);
        int argb = toArgb(color, alpha);

        buffer.addVertex(pose, (float) start.x, (float) start.y, (float) start.z)
                .setColor(argb)
                .setNormal(pose, normal)
                .setLineWidth(1.5F);
        buffer.addVertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .setColor(argb)
                .setNormal(pose, normal)
                .setLineWidth(1.5F);
    }

    private static int toArgb(float[] color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
        int r = Math.max(0, Math.min(255, Math.round(color[0] * 255.0F)));
        int g = Math.max(0, Math.min(255, Math.round(color[1] * 255.0F)));
        int b = Math.max(0, Math.min(255, Math.round(color[2] * 255.0F)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
