package org.xyzplus.json;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Type {
    Map((s) -> s.matches("^\\{.*}$")),
    Array((s) -> s.matches("^\\[.*]$")),
    Boolean((s) -> s.matches("^(true|false)$")),
    Long((s) -> s.matches("^[0-9]*$")),
    String((s) -> !(Array.belongTo(s) || Boolean.belongTo(s) || Long.belongTo(s) || Map.belongTo(s)));
    final Function<String, Boolean> predicate;

    Type(Function<String, java.lang.Boolean> predicate) {
        this.predicate = predicate;
    }

    boolean belongTo(String value) {
        return predicate.apply(value);
    }
}

public class JsonValue {

    String value;

    JsonValue(String jsonValue) {
        if (jsonValue != null && Type.String.belongTo(jsonValue)) {
            this.value = jsonValue.substring(1, jsonValue.length() - 1);
        } else {
            this.value = jsonValue;
        }
    }

    static int lengthOf(String value) {
        record method() {
            static int lenOfContainer(String value, String open, String close) {
                Matcher m = Pattern.compile("[\\%s\\%s]".formatted(open, close)).matcher(value);
                for (int level = 0; m.find(); ) if ((level += m.group().equals(open) ? 1 : -1) == 0) return m.end();
                return value.length();
            }

            static int lenOfValue(String value) {
                Matcher m = Pattern.compile("(.*?)[,|\\]}]").matcher(value);
                return m.find() ? m.end(1) : value.length();
            }
        }
        if (Type.Map.belongTo(value)) {
            return method.lenOfContainer(value, "{", "}");
        } else if (Type.Array.belongTo(value)) {
            return method.lenOfContainer(value, "[", "]");
        } else {
            return method.lenOfValue(value);
        }
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean toBoolean() {
        if (Type.Boolean.belongTo(value)) {
            return value.equals("true");
        } else {
            throw new RuntimeException("wrong type conversion");
        }
    }

    public long toLong() {
        if (Type.Long.belongTo(value)) {
            return Long.parseLong(value);
        } else {
            throw new RuntimeException("wrong type conversion");
        }
    }

    public List<JsonValue> toArray() {
        List<JsonValue> jsonValues = new ArrayList<>();
        Matcher m = Pattern.compile("[,|\\[]\"(.*)").matcher(value);
        for (int progress = 0, valEnd; m.find(progress) && progress != value.length(); progress = valEnd) {
            valEnd = m.start(1) + lengthOf(value.substring(m.start(1)));
            jsonValues.add(new JsonValue(value.substring(m.start(1), valEnd)));
        }
        return jsonValues;
    }
}
