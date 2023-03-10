package org.jsonoperate.JsonController;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class JsonController {
    private final JsonMapper rootObject;
    private Map<String, JsonMapper> operandMap;
    private String operand;

    public JsonController(String originalJson) {
        this.rootObject = new JsonMapper(originalJson);
        this.operandMap = this.rootObject.getValMap();
    }

    public JsonController select(String path) {
        String[] keys = path.split("/");
        for (int i = 0; i < keys.length - 1; i++) {
            operandMap = operandMap.get(keys[i]).getValMap();
        }
        this.operand = keys[keys.length - 1];
        return this;
    }

    public JsonController setKey(String key) {
        operandMap.put(key, operandMap.get(this.operand));
        operandMap.remove(this.operand);
        operand = key;
        return this;
    }

    public JsonController setVal(String val) {
        if (JsonValue.Type.String.belongTo(val)) {
            val = "\"%s\"".formatted(val);
        }
        operandMap.put(this.operand, new JsonMapper(val));
        return this;
    }

    public JsonValue getVal() {
        JsonMapper target = this.operandMap.getOrDefault(this.operand, rootObject);
        return new JsonValue(target.toString());
    }

    public JsonController remove() {
        operandMap.remove(this.operand);
        return this;
    }

    public JsonController revert() {
        this.operandMap = this.rootObject.getValMap();
        this.operand = null;
        return this;
    }
}
