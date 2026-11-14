package me.luligabi.yet_another_industrialization.client.renderer;

import aztech.modern_industrialization.client.machines.MachineRenderState;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import me.luligabi.yet_another_industrialization.common.block.machine.flight_pylon.FlightPylonBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

/** Snapshot the pylon's beam on extraction; submit never reads live block entity state. */
public final class FlightPylonBER extends MultiblockMachineBER {
    public FlightPylonBER(BlockEntityRendererProvider.Context context) { super(context); }

    public static final class State extends MachineRenderState {
        boolean beam;
        float animationTime;
        int color;
        double xOffset, zOffset;
    }
    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(MultiblockMachineBlockEntity entity, MachineRenderState renderState,
            float partialTicks, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(entity, renderState, partialTicks, camera, breakProgress);
        State state = (State) renderState;
        FlightPylonBlockEntity pylon = (FlightPylonBlockEntity) entity;
        state.beam = pylon.isActive.isActive && pylon.beaconComponent.enabled
                && !FlightPylonBlockEntity.TIERS.isEmpty();
        if (!state.beam) return;
        state.animationTime = pylon.getLevel() == null ? 0
                : Math.floorMod(pylon.getLevel().getGameTime(), 40) + partialTicks;
        state.color = pylon.getTier().beaconColor();
        state.xOffset = 0; state.zOffset = 0;
        switch (pylon.orientation.facingDirection) {
            case NORTH -> state.zOffset = 1;
            case SOUTH -> state.zOffset = -1;
            case WEST -> state.xOffset = 1;
            case EAST -> state.xOffset = -1;
            default -> state.zOffset = 1;
        }
    }
    @Override public void submit(MachineRenderState renderState, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(renderState, poseStack, collector, camera);
        State state = (State) renderState;
        if (!state.beam) return;
        poseStack.pushPose();
        poseStack.translate(state.xOffset, 0, state.zOffset);
        BeaconRenderer.submitBeaconBeam(poseStack, collector, BeaconRenderer.BEAM_LOCATION, 1f,
                state.animationTime, 9, 1024, state.color, 0.2f, 0.25f);
        poseStack.popPose();
    }
    @Override public boolean shouldRenderOffScreen() { return true; }
}
