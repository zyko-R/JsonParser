import org.xyzplus.json.JsonEntry;

public class demo {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        String json = "{\"body\":{\"urls\":{\"a\":-100, \"b\":100, \"c\":false}}}";
        JsonEntry baseEntry = new JsonEntry(json);
        var body = baseEntry.select("body");
        body.setKey("newBody");
        body.select("urls/b").setKey("newA").setValue("gg");
        baseEntry.select("newBody").insert("new", "new");
        System.out.println(baseEntry);


        JsonEntry baseEntry1 = new JsonEntry("");
        System.out.println(baseEntry1);
    }
}