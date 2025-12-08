package io.github.m3t4f1v3;

import org.bukkit.plugin.java.JavaPlugin;

import io.netty.buffer.ByteBuf;

import xyz.bluspring.modernnetworking.api.CompositeCodecs;
import xyz.bluspring.modernnetworking.api.NetworkCodec;
import xyz.bluspring.modernnetworking.api.NetworkCodecs;
import xyz.bluspring.modernnetworking.api.NetworkPacket;
import xyz.bluspring.modernnetworking.api.PacketDefinition;
import xyz.bluspring.modernnetworking.bukkit.api.BukkitNetworkRegistry;

public class Networking {
    public static final BukkitNetworkRegistry NETWORK_REGISTRY = new BukkitNetworkRegistry(
            JavaPlugin.getPlugin(SeasonalLods.class), "seasonallods");
    
    public static final PacketDefinition<SeasonPacket, ByteBuf> SEASON_PACKET = NETWORK_REGISTRY.registerClientbound("sync_season", SeasonPacket.CODEC);

    public record SeasonPacket(String json, String season, int subSeason, boolean useSubSeasons) implements NetworkPacket {
        public static final NetworkCodec<SeasonPacket, ByteBuf> CODEC = CompositeCodecs.composite(
                NetworkCodecs.STRING_UTF8, SeasonPacket::json,
                NetworkCodecs.STRING_UTF8, SeasonPacket::season,
                NetworkCodecs.INT, SeasonPacket::subSeason,
                NetworkCodecs.BOOL, SeasonPacket::useSubSeasons,
                SeasonPacket::new);

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return SEASON_PACKET;
        }
    }

    public static final PacketDefinition<DiscoverPacket, ByteBuf> DISCOVER_PACKET = NETWORK_REGISTRY
            .registerServerbound("discover_packet", DiscoverPacket.CODEC);

    public record DiscoverPacket() implements NetworkPacket {
        public static final NetworkCodec<DiscoverPacket, ByteBuf> CODEC = NetworkCodecs.unit(new DiscoverPacket());

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return DISCOVER_PACKET;
        }
    }
}