package me.dustin.chatbot.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dustin.chatbot.helper.GeneralHelper;

import java.util.ArrayList;
import java.util.Map;

public class BlockState {

    private static final ArrayList<BlockState> blockStates = new ArrayList<>();

    private final int blockId;
    private final String name;
    private final ArrayList<BlockStateData> blockStateData;

    public BlockState(int blockId, String name, ArrayList<BlockStateData> blockStateData) {
        this.blockId = blockId;
        this.name = name;
        this.blockStateData = blockStateData;
    }

    public int getBlockId() {
        return blockId;
    }

    public String getName() {
        return name;
    }

    public ArrayList<BlockStateData> getBlockStateData() {
        return blockStateData;
    }

    public static BlockState get(int blockStateId) {
        for (BlockState blockState : blockStates) {
            for (BlockStateData blockStateDatum : blockState.getBlockStateData()) {
                if (blockStateDatum.getStateId() == blockStateId)
                    return blockState;
            }
        }
        return null;
    }
    public static int totalNumber() {
        return blockStates.size();
    }

    public static void downloadBlockStateData() {
        String data = GeneralHelper.httpRequest("https://gitlab.bixilon.de/bixilon/pixlyzer-data/-/raw/master/version/1.18.2/blocks.json", null, null, "GET").data();
        JsonObject object = GeneralHelper.prettyGson.fromJson(data, JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            ArrayList<BlockStateData> stateDataList = new ArrayList<>();
            String name = entry.getKey();
            JsonObject blockObj = entry.getValue().getAsJsonObject();
            int blockId = 135;
            JsonObject states = blockObj.getAsJsonObject("states");
            for (Map.Entry<String, JsonElement> stateEntry : states.entrySet()) {
                String stateId = stateEntry.getKey();
                JsonObject stateObj = stateEntry.getValue().getAsJsonObject();
                boolean hasCollision = stateObj.has("collision_shape");

                BlockStateData blockStateData = new BlockStateData(Integer.parseInt(stateId), hasCollision, !hasCollision ? -1 : stateObj.get("collision_shape").getAsInt());
                stateDataList.add(blockStateData);
            }
            BlockState blockState = new BlockState(blockId, name, stateDataList);
            blockStates.add(blockState);
        }
    }

    public static ArrayList<BlockState> getBlockStates() {
        return blockStates;
    }

    public static class BlockStateData {
        private final int stateId;
        private final boolean hasCollision;
        private final int collisionShape;

        public BlockStateData(int stateId, boolean hasCollision, int collisionShape) {
            this.stateId = stateId;
            this.hasCollision = hasCollision;
            this.collisionShape = collisionShape;
        }

        public int getStateId() {
            return stateId;
        }

        public boolean isHasCollision() {
            return hasCollision;
        }

        public int getCollisionShape() {
            return collisionShape;
        }
    }
}
