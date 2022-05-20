package me.dustin.chatbot.entity;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.network.ProtocolHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Entity {
    protected final int entityId;
    protected final String type;
    protected double x, y, z;
    protected float yaw, pitch;

    private static final ArrayList<String> nonLivingEntities = new ArrayList<>();
    private static final Map<Integer, String> entityIdMap = new HashMap<>();

    public Entity(int entityId, String type, double x, double y, double z, float yaw, float pitch) {
        this.entityId = entityId;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public int getEntityId() {
        return entityId;
    }

    public String getTypeName() {
        return type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public double distanceToBot() {
        double meX = ChatBot.getClientConnection().getClientPlayer().getX();
        double meY = ChatBot.getClientConnection().getClientPlayer().getY();
        double meZ = ChatBot.getClientConnection().getClientPlayer().getZ();
        double f = getX() - meX;
        double g = getY() - meY;
        double h = getZ() - meZ;
        return Math.sqrt(f * f + g * g + h * h);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entityId=" + entityId +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    public static String getTypeName(int id) {
        if (!entityIdMap.containsKey(id))
            return "unknown";
        return entityIdMap.get(id);
    }

    public static boolean isLiving(String name) {
        return !nonLivingEntities.contains(name);
    }

    static {
        String v = ProtocolHandler.getCurrent().getName().replace(".", "_");
        if (v.toLowerCase().contains("pre") || v.toLowerCase().contains("w"))
            v = "1_19";
        //fat fuckin mess to create the version id needed for the link, i.e. 1_12 from 1.12.2
        if (v.split("_").length > 2)
            v = v.split("_")[0] + "_" + v.split("_")[1];
        String url = "https://raw.githubusercontent.com/DustinRepo/ChatBot/master/entityIds/" + v + "_entity_ids.txt";
        GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest(url, null, null, "GET");
        if (httpResponse.responseCode() != 404) {
            String[] ids = httpResponse.data().split("\n");
            for (String s : ids) {
                int id = Integer.parseInt(s.split("=")[0]);
                String name = s.split("=")[1];
                entityIdMap.put(id, name);
            }
        }
        nonLivingEntities.add("entity.minecraft.area_effect_cloud");
        nonLivingEntities.add("entity.minecraft.armor_stand");
        nonLivingEntities.add("entity.minecraft.arrow");
        nonLivingEntities.add("entity.minecraft.boat");
        nonLivingEntities.add("entity.minecraft.chest_boat");
        nonLivingEntities.add("entity.minecraft.dragon_fireball");
        nonLivingEntities.add("entity.minecraft.end_crystal");
        nonLivingEntities.add("entity.minecraft.evoker_fangs");
        nonLivingEntities.add("entity.minecraft.experience_orb");
        nonLivingEntities.add("entity.minecraft.eye_of_ender");
        nonLivingEntities.add("entity.minecraft.falling_block");
        nonLivingEntities.add("entity.minecraft.firework_rocket");
        nonLivingEntities.add("entity.minecraft.glow_item_frame");
        nonLivingEntities.add("entity.minecraft.item");
        nonLivingEntities.add("entity.minecraft.item_frame");
        nonLivingEntities.add("entity.minecraft.fireball");
        nonLivingEntities.add("entity.minecraft.leash_knot");
        nonLivingEntities.add("entity.minecraft.lightning_bolt");
        nonLivingEntities.add("entity.minecraft.llama_spit");
        nonLivingEntities.add("entity.minecraft.marker");
        nonLivingEntities.add("entity.minecraft.minecart");
        nonLivingEntities.add("entity.minecraft.chest_minecart");
        nonLivingEntities.add("entity.minecraft.command_block_minecart");
        nonLivingEntities.add("entity.minecraft.furnace_minecart");
        nonLivingEntities.add("entity.minecraft.hopper_minecart");
        nonLivingEntities.add("entity.minecraft.spawner_minecart");
        nonLivingEntities.add("entity.minecraft.tnt_minecart");
        nonLivingEntities.add("entity.minecraft.painting");
        nonLivingEntities.add("entity.minecraft.tnt");
        nonLivingEntities.add("entity.minecraft.shulker_bullet");
        nonLivingEntities.add("entity.minecraft.small_fireball");
        nonLivingEntities.add("entity.minecraft.spectral_arrow");
        nonLivingEntities.add("entity.minecraft.egg");
        nonLivingEntities.add("entity.minecraft.ender_pearl");
        nonLivingEntities.add("entity.minecraft.experience_bottle");
        nonLivingEntities.add("entity.minecraft.potion");
        nonLivingEntities.add("entity.minecraft.trident");
        nonLivingEntities.add("entity.minecraft.wither_skull");
        nonLivingEntities.add("entity.minecraft.fishing_bobber");
    }
}
