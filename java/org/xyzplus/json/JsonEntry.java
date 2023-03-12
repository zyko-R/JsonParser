package org.xyzplus.json;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class JsonEntry {
    private final JsonNode baseNode;
    private String operand;

    public JsonEntry(String jsonObject) {
        this.baseNode = new JsonNode(jsonObject);
    }

    private JsonEntry(JsonNode baseNode, String key) {
        if (baseNode.containsKey(key)) {
            this.baseNode = baseNode;
            this.operand = key;
        } else {
            throw new RuntimeException("key:\"%s\" does not exist".formatted(key));
        }
    }

    public JsonEntry select(@NotNull String... subPaths) {
        JsonEntry service = new JsonEntry(this.baseNode.spread(this.operand), subPaths[0]);
        if (subPaths.length == 1) {
            return service;
        } else {
            String[] prefix = Arrays.copyOfRange(subPaths, 0, subPaths.length - 1);
            return new JsonEntry(service.baseNode.spread(prefix), subPaths[subPaths.length - 1]);
        }
    }

    public JsonEntry select(@NotNull String subPaths) {
        return select(subPaths.split("/"));
    }

    public JsonEntry insert(String key, String value) {
        baseNode.get(this.operand).put(key, new JsonNode(value));
        return this;
    }

    public JsonEntry setKey(String newKey) {
        this.baseNode.put(newKey, this.baseNode.get(this.operand));
        this.baseNode.remove(this.operand);
        this.operand = newKey;
        return this;
    }

    public JsonValue getValue() {
        JsonNode subNode = this.baseNode.getOrDefault(this.operand, null);
        return subNode != null ? new JsonValue(subNode.toString()) : null;
    }

    public JsonEntry setValue(String newValue) {
        baseNode.put(this.operand, new JsonNode(newValue));
        return this;
    }

    public JsonEntry remove() {
        baseNode.remove(this.operand);
        return this;
    }

    @Override
    public String toString() {
        return this.baseNode.toString();
    }

    @Override
    public int hashCode() {
        return baseNode.hashCode();
    }
}
