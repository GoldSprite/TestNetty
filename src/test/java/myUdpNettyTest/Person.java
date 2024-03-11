package myUdpNettyTest;

import java.util.List;

public class Person
{
    private String name;
    private int age;
    private boolean isMale;
    private List<String> hobbies;

    public <T> Person(String name, int age, boolean isMale, List<String> hobbiles) {
        this.name = name;
        this.age = age;
        this.isMale = isMale;
        this.hobbies = hobbiles;
    }

    // 省略构造器和getter/setter方法，注意重写toString方法，便于查看控制台输出。
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", isMale=").append(isMale);
        sb.append(", hobbies=").append(hobbies);
        sb.append('}');
        return sb.toString();
    }
}

