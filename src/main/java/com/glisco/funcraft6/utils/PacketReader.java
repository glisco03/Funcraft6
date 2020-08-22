package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_16_R2.Packet;
import net.minecraft.server.v1_16_R2.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PacketReader {

    Player p;
    Channel channel;

    public PacketReader(Player p) {
        this.p = p;
    }

    public void attach() {
        CraftPlayer cplayer = (CraftPlayer) p;
        channel = cplayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "SignPacketIntercept", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) {
                if (packet instanceof PacketPlayInUpdateSign) {
                    PacketPlayInUpdateSign signPacket = (PacketPlayInUpdateSign) packet;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.p, () -> {
                        Sign s = (Sign) p.getWorld().getBlockAt(signPacket.b().getX(), signPacket.b().getY(), signPacket.b().getZ()).getState();
                        for (int i = 0; i < 4; i++) {
                            s.setLine(i, signPacket.c()[i]);
                        }
                        if (!s.getPersistentDataContainer().has(Main.key("owner"), PersistentDataType.STRING)) {
                            s.getPersistentDataContainer().set(Main.key("owner"), PersistentDataType.STRING, p.getUniqueId().toString());
                        }
                        s.update();
                    }, 0);
                } else {
                    list.add(packet);
                }
            }
        });
        GlobalVars.activeReaders.add(this);
    }

    public void detach() {
        GlobalVars.activeReaders.remove(this);
        if (channel.pipeline().get("SignPacketIntercept") == null) return;
        channel.pipeline().remove("SignPacketIntercept");
    }

    public static void detachAll() {
        List<PacketReader> toDetach = new ArrayList<>(GlobalVars.activeReaders);
        for (PacketReader r : toDetach) {
            r.detach();
        }
    }

    public static void attachAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketReader r = new PacketReader(player);
            r.attach();
        }
    }
}
