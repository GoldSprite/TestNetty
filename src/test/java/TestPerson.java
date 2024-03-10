//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import goldsprite.myUdpNettyTest.testJson.Person;
//
//import java.util.*;
//
//public class TestPerson {
//    public static void main(String[] args) {
////        testBeanToJson();
////        testListToJson();
//        testMapToJson();
//    }
//
//    public static void testBeanToJson() {
//        // 创建Bean
//        Person p = new Person("艾伦·耶格尔", 16, true, Arrays.asList("自由", "迫害莱纳"));
//        // 创建Gson对象
//        Gson gson = new Gson();
//        // 调用Gson的String toJson(Object)方法将Bean转换为json字符串
//        String pJson = gson.toJson(p);
//
//        LogTools.NLog("personStr: " + p);
//        LogTools.NLog("json: " + pJson);
//        Person person = gson.fromJson(pJson, Person.class);
//        LogTools.NLog("JsonToPerson: " + person);
//        // {"name":"艾伦·耶格尔","age":16,"isMale":true,"hobbies":["自由","迫害莱纳"]}
//    }
//
//    public static void testListToJson() {
//        // 先准备一个List集合
//        List<Person> list = new ArrayList<Person>();
//        list.add(new Person("三笠·阿克曼", 16, false, Arrays.asList("砍巨人", "保护艾伦")));
//        list.add(new Person("阿明·阿诺德", 16, true, Arrays.asList("看书", "玩海螺")));
//        LogTools.NLog(list);
//        // 创建Gson实例
//        Gson gson = new Gson();
//        // 调用Gson的toJson方法
//        String listJson = gson.toJson(list);
//        LogTools.NLog(listJson);
//        // [{"name":"三笠·阿克曼","age":16,"isMale":false,"hobbies":["砍巨人","保护艾伦"]},{"name":"阿明·阿诺德","age":16,"isMale":true,"hobbies":["看书","玩海螺"]}]
//
//        List fromJson = gson.fromJson(listJson, List.class);
//        LogTools.NLog(fromJson.get(0).getClass());
//        // class com.google.gson.internal.LinkedTreeMap
//
//        // 调用Gson的 T fromJson(String, Type)将List集合的json串反序列化为List对象
//        List<Person> plist = gson.fromJson(listJson, new TypeToken<List<Person>>(){}.getType());
//        LogTools.NLog(plist);
//        // [Person{name='三笠·阿克曼', age=16, isMale=false, hobbies=[砍巨人, 保护艾伦]}, Person{name='阿明·阿诺德', age=16, isMale=true, hobbies=[看书, 玩海螺]}]
//    }
//
//    public static void testMapToJson()
//    {
//        Map<String, Person> map = new HashMap<>();
//        map.put("p1", new Person("利威尔·阿克曼", 35, true, Arrays.asList("砍猴儿", "打扫卫生")));
//        map.put("p2", new Person("韩吉·佐耶", 33, false, Arrays.asList("研究巨人", "讲故事")));
//
//        Gson gson = new Gson();
//        String mapJson = gson.toJson(map);
//
//        LogTools.NLog(mapJson);
//        // {"p1":{"name":"利威尔·阿克曼","age":35,"isMale":true,"hobbies":["砍猴儿","打扫卫生"]},"p2":{"name":"韩吉·佐耶","age":33,"isMale":false,"hobbies":["研究巨人","讲故事"]}}
//        Map<String, Person> jsonMap = gson.fromJson(mapJson, new TypeToken<Map<String, Person>>() { }.getType());
//        LogTools.NLog(jsonMap);
//    }
//
//}
