package be4rjp.asyncobjectlib.util;

import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.*;

public class NMSUtil {
    
    private static Object BLOCK_DATA_AIR;
    
    static {
        try{
            BLOCK_DATA_AIR = NMSUtil.getIBlockData(Material.AIR.createBlockData());
        }catch (Exception e){e.printStackTrace();}
    }
    
    //微妙にCPU負荷が小さくなるおまじないキャッシュ
    private static final Map<String, Class<?>> nmsClassMap = new HashMap<>();
    private static final Map<String, Class<?>> craftBukkitClassMap = new HashMap<>();
    
    public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        Class<?> nmsClass = nmsClassMap.get(nmsClassString);
        
        if(nmsClass == null){
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
            String name = "net.minecraft.server." + version + nmsClassString;
            nmsClass = Class.forName(name);
            nmsClassMap.put(nmsClassString, nmsClass);
        }
        
        return nmsClass;
    }
    
    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        Class<?> craftBukkitClass = craftBukkitClassMap.get(className);
        
        if(craftBukkitClass == null){
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
            craftBukkitClass = Class.forName("org.bukkit.craftbukkit." + version + className);
            craftBukkitClassMap.put(className, craftBukkitClass);
        }
        
        return craftBukkitClass;
    }
    
    
    public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        return con;
    }
    
    
    public static Object getNMSPlayer(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        return nmsPlayer;
    }
    
    
    public static Object getIBlockData(BlockData blockData) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = blockData.getClass().getMethod("getState");
        Object nmsPlayer = getHandle.invoke(blockData);
        return nmsPlayer;
    }
    
    
    public static Channel getChannel(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        
        Field netField = con.getClass().getField("networkManager");
        Object net = netField.get(con);
        
        Field chaField = net.getClass().getField("channel");
        Object channel = chaField.get(net);
        
        return (Channel)channel;
    }
    
    
    public static Object getNMSWorld(World world) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = world.getClass().getMethod("getHandle");
        Object nmsWorld = getHandle.invoke(world);
        return nmsWorld;
    }
    
    
    public static Object getNMSChunk(Chunk chunk) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = chunk.getClass().getMethod("getHandle");
        Object nmsChunk = getHandle.invoke(chunk);
        return nmsChunk;
    }
    
    
    public static int getEntityID(Object entity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Entity = getNMSClass("Entity");
        Method getBukkitEntity = Entity.getMethod("getBukkitEntity");
        Object bukkitEntity = getBukkitEntity.invoke(entity);
        
        return ((org.bukkit.entity.Entity)bukkitEntity).getEntityId();
    }
    
    
    public static Object createChunk(Object nmsWorld, Object chunkCoordIntPair, Object biomeStorage)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Chunk = NMSUtil.getNMSClass("Chunk");
        Class<?> World = NMSUtil.getNMSClass("World");
        Class<?> ChunkCoordIntPair = NMSUtil.getNMSClass("ChunkCoordIntPair");
        Class<?> BiomeStorage = NMSUtil.getNMSClass("BiomeStorage");
        return Chunk.getConstructor(World, ChunkCoordIntPair, BiomeStorage).newInstance(nmsWorld, chunkCoordIntPair, biomeStorage);
    }
    
    
    public static Object createChunkSection(int y)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> ChunkSection = NMSUtil.getNMSClass("ChunkSection");
        return ChunkSection.getConstructor(int.class).newInstance(y);
    }
    
    
    public static Object getChunkSections(Object nmsChunk)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Chunk = NMSUtil.getNMSClass("Chunk");
        return Chunk.getMethod("getSections").invoke(nmsChunk);
    }
    
    
    public static Object getTypeChunkSection(Object chunkSections, int x, int y, int z)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> ChunkSection = NMSUtil.getNMSClass("ChunkSection");
        return ChunkSection.getMethod("getType", int.class, int.class, int.class).invoke(chunkSections, x, y, z);
    }
    
    
    public static void setTypeChunkSection(Object chunkSections, int x, int y, int z, Object iBlockData)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> ChunkSection = NMSUtil.getNMSClass("ChunkSection");
        Class<?> IBlockData = NMSUtil.getNMSClass("IBlockData");
        ChunkSection.getMethod("setType", int.class, int.class, int.class, IBlockData).invoke(chunkSections, x, y, z, iBlockData);
    }
    
    
    public static Object createPacketPlayOutMapChunk(Object nmsChunk, int size)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> PacketPlayOutMapChunk = NMSUtil.getNMSClass("PacketPlayOutMapChunk");
        Class<?> Chunk = NMSUtil.getNMSClass("Chunk");
        return PacketPlayOutMapChunk.getConstructor(Chunk, int.class).newInstance(nmsChunk, size);
    }
    
    
    public static void setIBlockData(Object packet, Object iBlockData)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> PacketPlayOutBlockChange = NMSUtil.getNMSClass("PacketPlayOutBlockChange");
        Field block = PacketPlayOutBlockChange.getField("block");
        block.set(packet, iBlockData);
    }
    
    
    public static void sendChunkUpdatePacket(Player player, Object chunk)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Object packet = createPacketPlayOutMapChunk(chunk, 65535);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
    
    
    public static void sendLegacyMultiBlockChangePacket(Player player, int length, short[] locations, Object nmsChunk)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Chunk = NMSUtil.getNMSClass("Chunk");
        Class<?> PacketPlayOutMultiBlockChange = NMSUtil.getNMSClass("PacketPlayOutMultiBlockChange");
        Constructor<?> constructor = PacketPlayOutMultiBlockChange.getConstructor(int.class, short[].class, Chunk);
        Object packet = constructor.newInstance(length, locations, nmsChunk);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
    
    
    public static void sendEntityTeleportPacket(Player player, Object entity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> packetClass = getNMSClass("PacketPlayOutEntityTeleport");
        Class<?> Entity = getNMSClass("Entity");
        Constructor<?> packetConstructor = packetClass.getConstructor(Entity);
        Object packet = packetConstructor.newInstance(entity);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
    
    
    public static int getCombinedId(Object iBlockData)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Block = getNMSClass("Block");
        Class<?> IBlockData = getNMSClass("IBlockData");
        Method getCombinedId = Block.getMethod("getCombinedId", IBlockData);
        return (int)getCombinedId.invoke(null, iBlockData);
    }
    
    
    public static Object getByCombinedId(int id)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Block = getNMSClass("Block");
        Method getByCombinedId = Block.getMethod("getByCombinedId", int.class);
        return getByCombinedId.invoke(null, id);
    }
    
    
    public static void setCEtoZero(Player player)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        
        Class<?> PlayerConnection = getNMSClass("PlayerConnection");
        Field C = PlayerConnection.getDeclaredField("C");
        Field E = PlayerConnection.getDeclaredField("E");
        C.setAccessible(true);
        E.setAccessible(true);
        
        C.set(con, 0);
        E.set(con, 0);
    }
    
    
    public static BlockData getBlockData(Object iBlockData)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> CraftBlockData = getCraftBukkitClass("block.data.CraftBlockData");
        Class<?> IBlockData = getNMSClass("IBlockData");
        Method fromData = CraftBlockData.getMethod("fromData", IBlockData);
        return (BlockData)fromData.invoke(null, iBlockData);
    }
    
    
}
