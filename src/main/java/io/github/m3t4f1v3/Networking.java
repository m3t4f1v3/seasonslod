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
    
    public static final PacketDefinition<InitialSyncPacket, ByteBuf> SEASON_PACKET = NETWORK_REGISTRY.registerClientbound("set_season_data", InitialSyncPacket.CODEC);

    public record InitialSyncPacket(String json, String season, int subSeason, boolean useSubSeasons) implements NetworkPacket {
        public static final NetworkCodec<InitialSyncPacket, ByteBuf> CODEC = CompositeCodecs.composite(
                NetworkCodecs.STRING_UTF8, InitialSyncPacket::json,
                NetworkCodecs.STRING_UTF8, InitialSyncPacket::season,
                NetworkCodecs.INT, InitialSyncPacket::subSeason,
                NetworkCodecs.BOOL, InitialSyncPacket::useSubSeasons,
                InitialSyncPacket::new);

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return SEASON_PACKET;
        }
    }

    public static final PacketDefinition<GameplaySyncPacket, ByteBuf> GAMEPLAY_SYNC_PACKET = NETWORK_REGISTRY
            .registerClientbound("sync_season", GameplaySyncPacket.CODEC);

    public record GameplaySyncPacket(String season, int subSeason, boolean useSubSeasons) implements NetworkPacket {
        public static final NetworkCodec<GameplaySyncPacket, ByteBuf> CODEC = CompositeCodecs.composite(
                NetworkCodecs.STRING_UTF8, GameplaySyncPacket::season,
                NetworkCodecs.INT, GameplaySyncPacket::subSeason,
                NetworkCodecs.BOOL, GameplaySyncPacket::useSubSeasons,
                GameplaySyncPacket::new);

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return GAMEPLAY_SYNC_PACKET;
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