
// import java.lang.reflect.Type;
import java.io.Console;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// import com.google.gson.reflect.TypeToken;
import com.google.gson.reflect.TypeToken;

public class Test {
    public static ArrayList<String> split(String str) {
        String pattern = "\n";

        Pattern r = Pattern.compile(pattern);

        ArrayList<String> lines = new ArrayList<String>();
        int lastIndex = 0;

        Matcher lineBreak = r.matcher(str);
        while (lineBreak.find()) {
            lines.add(str.substring(lastIndex, lineBreak.end()));
            lastIndex = lineBreak.end();
        }

        lines.add(str.substring(lastIndex, str.length()));

        return lines;
    }

    public static ArrayList<Line> convert(Delta delta) {
        List<Op> ops = delta.ops;

        Line tempLine = new Line();

        ArrayList<Line> lines = new ArrayList<Line>();
        for (Op cur : ops) {
            // const { insert, ...attr } = cur;

            Object insert = cur.insert;
            HashMap<String, String> attr = cur.attributes;

            if (insert instanceof String) {
                // 文本span

                // 统一转为字符串数组
                // 一般文本则长度为一，换行的长度至少为2
                // ArrayList<Line> _lines = new ArrayList<Line>();
                String str = (String) insert;
                ArrayList<Op> _lines = Test.convertListToMapList(Test.split(str));

                // 是否换行
                if (_lines.size() > 1) {
                    tempLine.push(_lines.remove(0)); // 百度一下这个方法

                    while (_lines.size() > 0) {
                        // 设置行属性
                        tempLine.set(attr);
                        // 结束一行
                        lines.add(tempLine);

                        // 新的一行
                        tempLine = new Line();
                        tempLine.push(_lines.remove(0));
                    }
                } else {
                    // 不换行
                    tempLine.push(cur);
                }
            } else {
                // 特殊span
                tempLine.push(cur);
            }

            return lines;
        }

        return lines;
    }

    public static <T> ArrayList<Op> convertListToMapList(ArrayList<T> list) {

        ArrayList<Op> result = new ArrayList<>();
        for (T t : list) {
            Op data = new Op();
            data.insert = t;
            result.add(data);
        }

        return result;
    }
}

class Line {
    public ArrayList<Object> spans = new ArrayList<Object>();
    public Object attributes;

    void push(Op item) {
        if (item.insert != null || item.attributes != null)
            this.spans.add(item);
    }

    void set(HashMap<String, String> attrs) {
        this.attributes = attrs;
    }

}

class M {
    public static void main(String[] args) {
        // Gson gson =
        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<Delta>() {
        }.getType();

        builder.setPrettyPrinting();
        Gson gson = builder.create();

        String json = "{\"ops\":[{\"insert\":\"标题h1\"},{\"attributes\":{\"header\":1},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"加粗\"},{\"insert\":\"\\n\"},{\"insert\":{\"image\":\"https://image-1258234461.cos.ap-guangzhou.myqcloud.com/icon/OJScVUCuH.jpg\"}},{\"insert\":\"\\n\"},{\"attributes\":{\"background\":\"#cce0f5\"},\"insert\":\"浅蓝色\"},{\"attributes\":{\"background\":\"#cce0f5\",\"color\":\"#008a00\"},\"insert\":\"背景的拉风大姐\"},{\"attributes\":{\"background\":\"#cce0f5\"},\"insert\":\"激\"},{\"attributes\":{\"background\":\"#cce0f5\",\"color\":\"#ff9900\"},\"insert\":\"发打卡\"},{\"attributes\":{\"background\":\"#cce0f5\"},\"insert\":\"机\"},{\"insert\":\"\\n\\n\\n\\n\\n居中\"},{\"attributes\":{\"background\":\"yellow\"},\"insert\":\"————\"},{\"attributes\":{\"align\":\"center\"},\"insert\":\"\\n\"},{\"attributes\":{\"background\":\"#0066cc\"},\"insert\":\"靠右\"},{\"attributes\":{\"align\":\"right\"},\"insert\":\"\\n\"},{\"attributes\":{\"underline\":true},\"insert\":\"下划线\"},{\"insert\":\"\\n\"},{\"attributes\":{\"strike\":true},\"insert\":\"中划线\"},{\"insert\":\"\\n\"},{\"insert\":{\"formula\":\"e=mc^2\"}},{\"insert\":\" \\n\"}]}";
        // String json = "{}";

        Delta delta = gson.fromJson(json, type);

        System.out.println(delta);

        // Test c = new Test();
        // c.convert(delta);
    }
}

class Delta {
    List<Op> ops;

    @Override
    public String toString() {
        String sb = "";
        for (Op op : ops) {
            sb = sb + op + '\n';// op. + ": " + op.attributes + " | ";
        }
        return sb;
    }
}

class Op {
    String insert;
    HashMap<String, String> attributes;

    @Override
    public String toString() {
        return this.insert + " | attrs:" + this.attributes;
    }
}

abstract class Image {
    String image;
}

abstract class Video {
    String video;
}