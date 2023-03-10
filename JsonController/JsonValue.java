package org.jsonoperate.JsonController;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonValue {
    enum Type {
        Array((s) -> s.matches("^\\[.*]$")),
        Boolean((s) -> s.matches("^(true|false)$")),
        Long((s) -> s.matches("^[0-9]*$")),
        String((s) -> !(Array.belongTo(s) || Boolean.belongTo(s) || Long.belongTo(s)));
        final Function<String, Boolean> predicate;

        Type(Function<String, java.lang.Boolean> predicate) {
            this.predicate = predicate;
        }

        boolean belongTo(String value) {
            return predicate.apply(value);
        }
    }

    String jsonValue;

    JsonValue(String jsonValue) {
        if (jsonValue != null && Type.String.belongTo(jsonValue)) {
            this.jsonValue = jsonValue.substring(1, jsonValue.length() - 1);
        } else {
            this.jsonValue = jsonValue;
        }
    }

    public List<JsonValue> toArray() {
        Function<String, Integer> length = (value) -> {
            if (Type.Array.belongTo(value)) {
                Matcher m = Pattern.compile("[\\[\\]]").matcher(value);
                for (int level = 0; m.find(); ) if ((level += m.group().equals("[") ? 1 : -1) == 0) return m.end();
                return value.length();
            } else {
                Matcher m = Pattern.compile("(.*)[,>]").matcher(value);
                return m.find() ? m.end(1) : value.length();
            }
        };

        if (!Type.Array.belongTo(jsonValue)){
            return null;
        }
        this.jsonValue = "< " + jsonValue.substring(1, jsonValue.length() - 1) + "  >" ;
        List<JsonValue> jsonArray = new Vector<>();
        Matcher m = Pattern.compile("[<,](.*)").matcher(jsonValue);
        for (int progress = 0, valEnd; m.find(progress) && progress != jsonValue.length(); progress = valEnd) {
            valEnd = m.start() + length.apply(m.group(1));
            jsonArray.add(new JsonValue(jsonValue.substring(m.start(1), valEnd)));
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        return jsonValue;
    }

    public boolean toBoolean() {
        if (Type.Boolean.belongTo(jsonValue)) {
            return jsonValue.equals("true");
        } else {
            throw new RuntimeException("!wrong type conversion!");
        }
    }

    public long toLong() {
        if (Type.Long.belongTo(jsonValue)) {
            return Long.parseLong(jsonValue);
        } else {
            throw new RuntimeException("!wrong type conversion!");
        }
    }
}
