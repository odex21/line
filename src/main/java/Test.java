import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static ArrayList<String> main(String str) {
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

    public static ArrayList<Line> convert(HashMap<String, List<HashMap<String, Object>>> delta) {
        List<HashMap<String, Object>> ops = delta.get("ops");

        Line tempLine = new Line();

        ArrayList<Line> lines = new ArrayList<Line>();
        for (HashMap<String, Object> cur : ops) {
            // const { insert, ...attr } = cur;

            Object insert = cur.get("insert");
            cur.remove("insert");
            HashMap<String, Object> attr = cur;

            if (insert instanceof String) {
                // 文本span

                // 统一转为字符串数组
                // 一般文本则长度为一，换行的长度至少为2
                // ArrayList<Line> _lines = new ArrayList<Line>();
                String str = (String) insert;
                ArrayList<HashMap<String, String>> _lines = Test.convertListToMapList(Test.main(str));

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

    public static <T> ArrayList<HashMap<String, T>> convertListToMapList(ArrayList<T> list) {

        ArrayList<HashMap<String, T>> result = new ArrayList<HashMap<String, T>>();
        for (T t : list) {
            HashMap<String, T> data = new HashMap<String, T>();
            data.put("insert", t);
            result.add(data);
        }

        return result;
    }
}

class Line {
    public ArrayList<Object> spans = new ArrayList<Object>();
    public Object attributes;

    void push(HashMap<String, ?> item) {
        if (item.get("insert") != null || item.get("attributes") != null)
            this.spans.add(item);
    }

    void set(HashMap<String, Object> attrs) {
        this.attributes = attrs.get("attributes");
    }
}

class M {
    public static void main(String[] args) {
        Test t = new Test();

        // t.convert("")
        String str = "{"ops":[{"insert":"标题h1"},{"attributes":{"header":1},"insert":"\n"},{"attributes":{"bold":true},"insert":"加粗"},{"insert":"\n"},{"insert":{"image":"https://image-1258234461.cos.ap-guangzhou.myqcloud.com/icon/OJScVUCuH.jpg"}},{"insert":"\n"},{"attributes":{"background":"#cce0f5"},"insert":"浅蓝色"},{"attributes":{"background":"#cce0f5","color":"#008a00"},"insert":"背景的拉风大姐"},{"attributes":{"background":"#cce0f5"},"insert":"激"},{"attributes":{"background":"#cce0f5","color":"#ff9900"},"insert":"发打卡"},{"attributes":{"background":"#cce0f5"},"insert":"机"},{"insert":"\n\n\n\n\n居中"},{"attributes":{"background":"yellow"},"insert":"————"},{"attributes":{"align":"center"},"insert":"\n"},{"attributes":{"background":"#0066cc"},"insert":"靠右"},{"attributes":{"align":"right"},"insert":"\n"},{"attributes":{"underline":true},"insert":"下划线"},{"insert":"\n"},{"attributes":{"strike":true},"insert":"中划线"},{"insert":"\n"},{"insert":{"formula":"e=mc^2"}},{"insert":" \n"}]}"

        t.convert(str);
    }
}