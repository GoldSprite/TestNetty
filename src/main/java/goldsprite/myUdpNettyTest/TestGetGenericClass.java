package goldsprite.myUdpNettyTest;

//import lombok.var;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * 测试获取泛型方法T实际类型: 结果失败, 只能获取类的泛型类型, 方法不可以, 匿名子类没试
 */
public class TestGetGenericClass {
    public static void main(String[] args) {
        new TestGetGenericClass().run();
    }

    public void run(){

//        goldsprite.myUdpNettyTest.Foo<String> foo = new goldsprite.myUdpNettyTest.Foo<String>() {
//        };
//        // 在类的外部这样获取
//        Type type = ((ParameterizedType) foo.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        System.out.println(type);
//        var foo2 = new goldsprite.myUdpNettyTest.Foo<String>();
//        System.out.println(foo.getClass().getGenericInterfaces());
//        System.out.println(foo.getClass().getGenericInterfaces()[0]);
//        Type type2 = ((ParameterizedType) foo.getClass().toGenericString()).getActualTypeArguments()[0];
//        System.out.println(type2);
//        在类的内部这样获取
//        System.out.println(foo.getTClass());
        var k3 = new B<C>();
        hello(k3);
    }

    public <T extends A> void hello(B<T> k){
        var kk = new B<T>(){};
        var clazz = ((ParameterizedType) kk.getClass().getGenericSuperclass()).getActualTypeArguments();
        System.out.println(clazz);
        System.out.println(clazz[0]);
    }
    public class A{}
    public class C extends A{}
    public class B<T>{}
}

class Foo<T> {
    public <T2> Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }
}
