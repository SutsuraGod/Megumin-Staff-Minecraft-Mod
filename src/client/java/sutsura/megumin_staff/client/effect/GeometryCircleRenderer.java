package sutsura.megumin_staff.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

final class GeometryCircleRenderer {
    private GeometryCircleRenderer() {
    }

    static void renderRing(VertexConsumer buffer, PoseStack poseStack, Vec3 center, double radius, double rotation, int segments,
                           float[] color, float alpha) {
        Vec3 previous = null;
        Vec3 first = null;

        for (int i = 0; i <= segments; i++) {
            double angle = rotation + (Math.PI * 2.0D * i / segments);
            Vec3 point = horizontalPoint(center, radius, angle);
            if (first == null) {
                first = point;
            }
            if (previous != null) {
                renderLine(buffer, poseStack, previous, point, color, alpha);
            }
            previous = point;
        }

        if (previous != null && first != null) {
            renderLine(buffer, poseStack, previous, first, color, alpha);
        }
    }

    static void renderPetals(VertexConsumer buffer, PoseStack poseStack, Vec3 center, double innerRadius, double outerRadius,
                             double rotation, float[][] palette, float alpha, int petalCount, int petalSegments) {
        for (int i = 0; i < petalCount; i++) {
            double angle = rotation + (Math.PI * 2.0D * i / petalCount);
            renderPetalArc(buffer, poseStack, center, innerRadius, outerRadius, angle, 1.0D, palette, alpha, petalSegments);
            renderPetalArc(buffer, poseStack, center, innerRadius, outerRadius, angle, -1.0D, palette, alpha, petalSegments);
        }
    }

    private static void renderPetalArc(VertexConsumer buffer, PoseStack poseStack, Vec3 center, double innerRadius, double outerRadius,
                                        double baseAngle, double direction, float[][] palette, float alpha, int segments) {
        double innerAngle = baseAngle - 0.22D * direction;
        double outerAngle = baseAngle + 0.78D * direction;
        Vec3 start = horizontalPoint(center, innerRadius, innerAngle);
        Vec3 end = horizontalPoint(center, outerRadius, outerAngle);
        double controlAngle = baseAngle + 0.38D * direction;
        Vec3 control = horizontalPoint(center, innerRadius + (outerRadius - innerRadius) * 0.62D, controlAngle)
                .add(Math.cos(controlAngle + Math.PI / 2.0D) * outerRadius * 0.28D * direction, 0.0D,
                        Math.sin(controlAngle + Math.PI / 2.0D) * outerRadius * 0.28D * direction);

        Vec3 previous = start;
        for (int step = 1; step <= segments; step++) {
            double t = step / (double) segments;
            Vec3 next = quadraticBezier(start, control, end, t);
            float[] color = palette[(step + (direction > 0.0D ? 0 : 1)) % palette.length];
            renderLine(buffer, poseStack, previous, next, color, alpha);
            previous = next;
        }
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
                .setLineWidth(2.0F);
        buffer.addVertex(pose, (float) end.x, (float) end.y, (float) end.z)
                .setColor(argb)
                .setNormal(pose, normal)
                .setLineWidth(2.0F);
    }

    private static Vec3 horizontalPoint(Vec3 center, double radius, double angle) {
        return new Vec3(
                center.x + Math.cos(angle) * radius,
                center.y,
                center.z + Math.sin(angle) * radius
        );
    }

    private static Vec3 quadraticBezier(Vec3 start, Vec3 control, Vec3 end, double t) {
        double oneMinusT = 1.0D - t;
        return start.scale(oneMinusT * oneMinusT)
                .add(control.scale(2.0D * oneMinusT * t))
                .add(end.scale(t * t));
    }

    private static int toArgb(float[] color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
        int r = Math.max(0, Math.min(255, Math.round(color[0] * 255.0F)));
        int g = Math.max(0, Math.min(255, Math.round(color[1] * 255.0F)));
        int b = Math.max(0, Math.min(255, Math.round(color[2] * 255.0F)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
