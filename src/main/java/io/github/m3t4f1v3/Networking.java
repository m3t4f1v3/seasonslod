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
    private static final BukkitNetworkRegistry NETWORK_REGISTRY = new BukkitNetworkRegistry(
            JavaPlugin.getPlugin(SeasonalLods.class), "voxy");
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
}