package org.xyzplus.json;


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JsonNode extends HashMap<String, JsonNode> {
    private String stringValue;

    JsonNode(String jsonValue) {
        if (Type.String.belongTo(jsonValue)) {
            jsonValue = "\"%s\"".formatted(jsonValue);
        }
        this.stringValue = jsonValue;
    }

    public JsonNode spread(String... keys) {
        JsonNode node = this;
        for (String key : keys) {
            node = node.load().getOrDefault(key, this);
        }
        return node.load();
    }

    @Override
    public String toString() {
        if (!this.isEmpty()) {
            StringBuilder sb = new StringBuilder().append('{');
            this.forEach((k, v) -> sb.append("\"%s\":%s,".formatted(k, v.toString())));
            return sb.deleteCharAt(sb.length() - 1).append('}').toString();
        } else {
            return this.stringValue;
        }
    }

    private JsonNode load() {
        if (this.isEmpty() && Type.Map.belongTo(stringValue)) {
            Matcher m = Pattern.compile("\"(.*?)\":(.*)").matcher(this.stringValue);
            for (int progress = 0, valEnd; m.find(progress); progress = valEnd) {
                valEnd = m.start(2) + JsonValue.lengthOf(m.group(2));
                this.put(m.group(1), new JsonNode(this.stringValue.substring(m.start(2), valEnd)));
            }
            this.stringValue = null;
        }
        return this;
    }
}



