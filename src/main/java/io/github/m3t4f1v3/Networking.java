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

    public record SeasonPacket(String season) implements NetworkPacket {
        public static final NetworkCodec<SeasonPacket, ByteBuf> CODEC = CompositeCodecs.composite(
                NetworkCodecs.STRING_UTF8, SeasonPacket::season,
                SeasonPacket::new);

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return SEASON_PACKET;
        }
    }

    public static final PacketDefinition<SubSeasonPacket, ByteBuf> SUBSEASON_PACKET = NETWORK_REGISTRY.registerClientbound("sync_subseason", SubSeasonPacket.CODEC);

    public record SubSeasonPacket(int subSeason) implements NetworkPacket {
        public static final NetworkCodec<SubSeasonPacket, ByteBuf> CODEC = CompositeCodecs.composite(
                NetworkCodecs.INT, SubSeasonPacket::subSeason,
                SubSeasonPacket::new);

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return SUBSEASON_PACKET;
        }
    }

    public static final PacketDefinition<BiomePacket, ByteBuf> BIOME_PACKET = NETWORK_REGISTRY.registerClientbound("sync_biomes", BiomePacket.CODEC);

    public record BiomePacket(String json) implements NetworkPacket {
        public static final NetworkCodec<BiomePacket, ByteBuf> CODEC = CompositeCodecs.composite(
                NetworkCodecs.STRING_UTF8, BiomePacket::json,
                BiomePacket::new);

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return BIOME_PACKET;
        }
    }

    public static final PacketDefinition<ReloadPacket, ByteBuf> RELOAD_RENDERER_PACKET = NETWORK_REGISTRY.registerClientbound("reload_renderer", ReloadPacket.CODEC);

    public record ReloadPacket() implements NetworkPacket {
        public static final NetworkCodec<ReloadPacket, ByteBuf> CODEC = NetworkCodecs.unit(new ReloadPacket());

        @Override
        public PacketDefinition<? extends NetworkPacket, ? extends ByteBuf> getDefinition() {
            return RELOAD_RENDERER_PACKET;
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