package org.jsonoperate.JsonController;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JsonMapper {
    String jsonValue;

    Map<String, JsonMapper> valMap = null;

    JsonMapper(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    private static Integer lengthOf(String value) {
        record lengthOf(String value) {
            int container(String p, String q) {
                Matcher m = Pattern.compile("[\\%s\\%s]".formatted(p, q)).matcher(value);
                for (int level = 0; m.find(); ) if ((level += m.group().equals(p) ? 1 : -1) == 0) return m.end();
                return value.length();
            }
            int val() {
                Matcher m = Pattern.compile("(.*?)[,}]").matcher(value);
                return m.find() ? m.end(1) : value.length();
            }
        }
        lengthOf len = new lengthOf(value);
        return switch (value.charAt(0)) {
            case '{' -> len.container("{", "}");
            case '[' -> len.container("[", "]");
            default -> len.val();
        };
    };

    public Map<String, JsonMapper> getValMap() {
        if (this.valMap == null){
            this.valMap = new HashMap<>();
            Matcher m = Pattern.compile("\"(.*?)\":(.*)").matcher(this.jsonValue);
            for (int progress = 0, valEnd; m.find(progress); progress = valEnd){
                valEnd = m.start(2) + lengthOf(this.jsonValue.substring(m.start(2)));
                this.valMap.put(m.group(1), new JsonMapper(this.jsonValue.substring(m.start(2), valEnd)));
            }
            return this.valMap;
        }
        return this.valMap;
    }

    @Override
    public String toString() {
        if (this.valMap == null) {
            return this.jsonValue;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, JsonMapper> entry: this.valMap.entrySet()) {
            sb.append(",\"%s\":%s".formatted(entry.getKey(), entry.getValue().toString()));
        }
        this.jsonValue = "{" + sb.substring(1) + "}";
        return this.jsonValue;
    }
}
