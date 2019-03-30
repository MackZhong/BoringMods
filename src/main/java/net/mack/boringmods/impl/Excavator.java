package net.mack.boringmods.impl;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.mack.boringmods.client.options.ModOption;
import net.mack.boringmods.client.options.ModOptions;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Excavator {
    private final static Excavator instance = new Excavator();

    public static Excavator getInstance() {
        return instance;
    }

    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    private Vec3i[] neighborPos = {
            new Vec3i(0, 0, -1),
            new Vec3i(-1, 0, -1),
            new Vec3i(-1, 0, 0),
            new Vec3i(-1, 0, 1),
            new Vec3i(0, 0, 1),
            new Vec3i(1, 0, 1),
            new Vec3i(1, 1, 1),
            new Vec3i(0, 1, 1),
            new Vec3i(-1, 1, 1),
            new Vec3i(-1, 1, 0),
            new Vec3i(-1, 1, -1),
            new Vec3i(0, 1, -1),
            new Vec3i(0, 1, 0),
            new Vec3i(1, 1, 0),
            new Vec3i(1, 1, -1),
            new Vec3i(1, 0, -1),
            new Vec3i(1, 0, 0),
            new Vec3i(0, -1, 0),
            new Vec3i(0, -1, -1),
            new Vec3i(0, -1, 1),
            new Vec3i(-1, -1, 0),
            new Vec3i(1, -1, 0),
            new Vec3i(-1, -1, -1),
            new Vec3i(1, -1, -1),
            new Vec3i(-1, -1, 1),
            new Vec3i(1, -1, 1)
    };
    private FabricKeyBinding keyExcavate;
    private final Identifier EXCAVATE_END = new Identifier("boringmods", "excavate_end");
    private final Identifier BREAK_BLOCK = new Identifier("boringmods", "break_block");
    private FabricKeyBinding keyTunnel;

    public boolean keyBinding(String category) {
        this.keyExcavate = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:excavate"),
                InputUtil.Type.KEY_KEYBOARD,
                96,
                category
        ).build();
        KeyBindingRegistryImpl.INSTANCE.register(this.keyExcavate);
        this.keyTunnel = FabricKeyBinding.Builder.create(
                new Identifier("boringmods:tunnel"),
                InputUtil.Type.KEY_KEYBOARD,
                220,
                category
        ).build();
        KeyBindingRegistryImpl.INSTANCE.register(this.keyTunnel);

        return true;
    }

    private boolean enableExcavate() {
        return this.keyExcavate.isPressed();
    }

    private boolean enableTunnel() {
        return this.keyTunnel.isPressed();
    }

    class PacketBreakBlock implements PacketConsumer {
        @Override
        public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
            if (!packetByteBuf.readBoolean()) {
                return;
            }
            BlockPos pos = packetByteBuf.readBlockPos();
            PlayerEntity player = packetContext.getPlayer();
            World world = player.getEntityWorld();
            logger.debug("PacketBreakBlock: This world is {} side.", world.isClient() ? "CLIENT" : "SERVER");
            world.breakBlock(pos, !player.isCreative());
        }
    }

    class PacketExcavateEnd implements PacketConsumer {
        @Override
        public void accept(PacketContext packetContext, PacketByteBuf packetByteBuf) {
            int damage = packetByteBuf.readInt();
            float exhaust = packetByteBuf.readFloat();
            PlayerEntity player = packetContext.getPlayer();
            player.getMainHandStack().applyDamage(damage, player);
//            player.addExhaustion(exhaust);
        }
    }

    public void registerServerSidePacket() {
        ServerSidePacketRegistry.INSTANCE.register(BREAK_BLOCK, new PacketBreakBlock());
        ServerSidePacketRegistry.INSTANCE.register(EXCAVATE_END, new PacketExcavateEnd());
    }

    private CustomPayloadC2SPacket createBreackPacket(BlockPos pos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        boolean validPos = null != pos;
        buf.writeBoolean(validPos);
        if (validPos) {
            buf.writeBlockPos(pos);
        }
        return new CustomPayloadC2SPacket(BREAK_BLOCK, buf);
    }

    private CustomPayloadC2SPacket createEndPacket(int damage, float exhaust) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(damage);
        buf.writeFloat(exhaust);

        return new CustomPayloadC2SPacket(EXCAVATE_END, buf);
    }

    private ArrayList<BlockPos> getNeighborBlocks(World world, BlockPos pos, Block block, int neighbors) {
        ArrayList<BlockPos> neighbourBlocks = new ArrayList<>();
        if (neighbors > neighborPos.length) {
            neighbors = neighborPos.length;
        }
        for (int i = 0; i < neighbors; ++i) {
            BlockPos nextPos = pos.add(neighborPos[i]);
            BlockState state = world.getBlockState(nextPos);
            if (!state.isAir() && state.getBlock() == block && !neighbourBlocks.contains(nextPos)) {
                neighbourBlocks.add(nextPos);
            }
        }
        return neighbourBlocks;
    }

    public void handle(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient() &&
                player.isUsingEffectiveTool(state) &&
                player.getHungerManager().getFoodLevel() > 0) {
            if (enableExcavate()) {
                this.excavate(world, pos, state, player);
            } else if (enableTunnel()) {
                this.tunnel(world, pos, player);
            }
        }
    }

    private void excavate(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Block block = state.getBlock();
        boolean isLogBlock = false;

        if (block instanceof LogBlock) {
            logger.debug("LogBlock breack.");
            isLogBlock = true;
        } else if (block instanceof OreBlock || block instanceof RedstoneOreBlock || block == Blocks.OBSIDIAN) {
            logger.debug("Ore breack.");
//            isLogBlock = false;
        } else {
            return;
        }

        ArrayList<BlockPos> brokenBlocks = new ArrayList<>();
        brokenBlocks.add(pos);
        BlockPos currentPos = pos;
        ArrayList<BlockPos> nextToBreak = new ArrayList<>();
        float exhaust = 0;

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (null == networkHandler) {
            logger.debug("Network handler not found.");
            return;
        }
        ClientConnection connection = networkHandler.getClientConnection();
        int brokenCount = 1;
        int excavateMaxBlocks = ModOption.EXCAVATE_MAX_BLOCKS.getValue(ModOptions.INSTANCE).intValue();
        double excavateRange = ModOption.EXCAVATE_RANGE.getValue(ModOptions.INSTANCE);
        ModOptions.LOGGER.info("Excavate max blocks is {}", excavateMaxBlocks);
        ModOptions.LOGGER.info("Excavate range is {}", excavateRange);
        while (brokenCount < excavateMaxBlocks &&
//                player.isUsingEffectiveTool(state) &&
                player.getHungerManager().getFoodLevel() > 0) {
            ArrayList<BlockPos> blocksNeighbour = getNeighborBlocks(world, currentPos, block, isLogBlock ? 17 : 26);
            logger.debug("{} neighbor blocks found.", blocksNeighbour.size());
            blocksNeighbour.removeAll(brokenBlocks);
            logger.debug("{} neighbor blocks preserved.", blocksNeighbour.size());
            for (BlockPos p : blocksNeighbour) {
                if (brokenCount >= excavateMaxBlocks ||
                        player.getHungerManager().getFoodLevel() <= exhaust / 2 ||
                        brokenCount >= (player.getMainHandStack().getDurability() - player.getMainHandStack().getDamage())) {
                    break;
                }
                if (!brokenBlocks.contains(p) &&
                        player.isUsingEffectiveTool(world.getBlockState(p))) {
                    nextToBreak.add(p);
//                    if (p.isWithinDistance(pos, excavateRange)) {
//                    }
                    logger.info("Excavator: breakBlock {}", p);
                    world.breakBlock(p, !player.isCreative());
                    connection.sendPacket(createBreackPacket(p));
                    brokenBlocks.add(p);
                    brokenCount = brokenBlocks.size();
                    exhaust = (0.005F * brokenCount) * (brokenCount / 8.0F + 1);
                }
            }
            if (nextToBreak.size() == 0) {
                break;
            }
            currentPos = nextToBreak.get(0);
            nextToBreak.remove(currentPos);
        }
        if (!player.isCreative()) {
            connection.sendPacket(createEndPacket(brokenCount - 1, exhaust));
        }
    }

    private void tunnel(World world, BlockPos pos, PlayerEntity player) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (null == networkHandler) {
            logger.debug("Network handler not found.");
            return;
        }
        ClientConnection connection = networkHandler.getClientConnection();

        int left = ModOptions.INSTANCE.tunnelWidth / 2;
        int bottom = ModOptions.INSTANCE.tunnelHeight / 2;
        Direction facing = player.getHorizontalFacing();
        Vec3i dir = facing.getVector();
        BlockPos posLB, posRB;
        if (Direction.EAST == facing) {
            posLB = pos.north(left).down(bottom);
            posRB = posLB.south(ModOptions.INSTANCE.tunnelWidth - 1);
        } else if (Direction.WEST == facing) {
            posLB = pos.south(left).down(bottom);
            posRB = posLB.north(ModOptions.INSTANCE.tunnelWidth - 1);
        } else if (Direction.SOUTH == facing) {
            posLB = pos.east(left).down(bottom);
            posRB = posLB.west(ModOptions.INSTANCE.tunnelWidth - 1);
        } else if (Direction.NORTH == facing) {
            posLB = pos.west(left).down(bottom);
            posRB = posLB.east(ModOptions.INSTANCE.tunnelWidth - 1);
        } else {
            return;
        }

        float exhaust = 0;
        int brokenCount = 1;
        int maxBlocks = ModOptions.INSTANCE.tunnelHeight * ModOptions.INSTANCE.tunnelWidth * ModOptions.INSTANCE.tunnelLong;
        for (BlockPos posB : BlockPos.iterateBoxPositions(posLB, posRB)) {
            BlockPos posT = posB.up(ModOptions.INSTANCE.tunnelHeight - 1);
            for (BlockPos posN : BlockPos.iterateBoxPositions(posB, posT)) {
                BlockPos p = posN;
                for (int d = 0; d < ModOptions.INSTANCE.tunnelLong; ++d) {
                    if (brokenCount >= maxBlocks ||
                            player.getHungerManager().getFoodLevel() <= exhaust / 2 ||
                            brokenCount >= (player.getMainHandStack().getDurability() - player.getMainHandStack().getDamage())) {
                        break;
                    }
                    if (pos == p) continue;

                    BlockState blockState = world.getBlockState(p);
                    Block block = blockState.getBlock();
                    if (null != block &&
                            !blockState.isAir() &&
                            world.getFluidState(p).isEmpty() &&
                            player.isUsingEffectiveTool(blockState)) {
                        world.breakBlock(p, !player.isCreative());
                        connection.sendPacket(createBreackPacket(p));
                        brokenCount++;
                        exhaust = (0.005F * brokenCount) * (brokenCount / 8.0F + 1);
                    }
                    p = p.add(dir);
                }
            }
        }

        if (!player.isCreative()) {
            connection.sendPacket(createEndPacket(brokenCount - 1, exhaust));
        }
    }

//
//    private ArrayList<BlockPos> getNeighbours(World world, BlockPos pos, Block block, boolean isLogBlock) {
//        ArrayList<BlockPos> neighbours = new ArrayList<>();
//        int startY = isLogBlock ? 0 : -1;
//        for (int x = -1; x <= 1; ++x) {
//                for (int z = -1; z <= 1; ++z) {
//                    for (int y = startY; y <= 1; ++y) {
//                    BlockPos currentPos = pos.add(x, y, z);
//                    if (!(0 == x && 0 == y && 0 == z)
//                            && world.getBlockState(currentPos).getBlock() == block) {
//                        neighbours.add(currentPos);
//                    }
//                }
//            }
//        }
//        return neighbours;
//    }
}
