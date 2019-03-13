package net.mack.boringmods.impl;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModInitializer implements net.fabricmc.api.ModInitializer {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");
    private static final Identifier END_EXCAVATE = new Identifier("boringmods", "end_excavate");
    private static final Identifier BREAK_BLOCK = new Identifier("boringmods", "break_block");

    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(BREAK_BLOCK, ((packetContext, packetByteBuf) -> {
            if (!packetByteBuf.readBoolean()){
                return;
            }
            BlockPos pos = packetByteBuf.readBlockPos();
            PlayerEntity player = packetContext.getPlayer();
            World world = player.getEntityWorld();
            world.breakBlock(pos, !player.isCreative());
        }));

        ServerSidePacketRegistry.INSTANCE.register(END_EXCAVATE, ((packetContext, packetByteBuf) -> {
            int damage = packetByteBuf.readInt();
            float exhaust = packetByteBuf.readFloat();
            PlayerEntity player = packetContext.getPlayer();
            player.getMainHandStack().applyDamage(damage, player);
//            player.addExhaustion(exhaust);
        }));
        logger.info("Boring Mods Initialization.");
    }


    public static CustomPayloadC2SPacket createBreackPacket(BlockPos pos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        boolean validPos = null != pos;
        buf.writeBoolean(validPos);
        if (validPos){
            buf.writeBlockPos(pos);
        }
        return new CustomPayloadC2SPacket(BREAK_BLOCK, buf);
    }

    public static CustomPayloadC2SPacket createEndPacket(int damage, float exhaust) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(damage);
        buf.writeFloat(exhaust);

        return new CustomPayloadC2SPacket(END_EXCAVATE, buf);
    }
}
