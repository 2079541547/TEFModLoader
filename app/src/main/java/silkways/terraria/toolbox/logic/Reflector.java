package silkways.terraria.toolbox.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 封装反射相关逻辑的工具类
 * 该封装类会维持链式调用
 */
public class Reflector {

    /**
     * 反射的类型
     */
    private Class<?> mClass;

    /**
     * 反射针对的实例对象
     * 如获取 Object 某个字段的值
     */
    private Object mCaller;

    /**
     * 反射的字段
     */
    private Field mField;

    /**
     * 反射的方法
     */
    private Method mMethod;

    /**
     * 反射某个类的入口方法
     *
     * @param type 要反射的类
     * @return
     */
    public static Reflector on(Class<?> type) {
        Reflector reflector = new Reflector();
        reflector.mClass = type;
        return reflector;
    }

    /**
     * 反射某个类的入口方法
     *
     * @param className 要反射的类名
     * @return
     */
    public static Reflector on(String className) {
        try {
            return on(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反射某个类的入口方法
     *
     * @param object 反射类对应的实例对象
     * @return
     */
    public static Reflector on(Object object) {
        return on(object.getClass()).with(object);
    }

    /**
     * 设置反射对应的实例对象
     *
     * @param object
     * @return
     */
    public Reflector with(Object object) {
        mCaller = object;
        return this;
    }

    /**
     * 创建 mClass 类型的实例对象
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T newInstance() {
        try {
            return (T) mClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反射类中的某个字段
     *
     * @param name 要反射的字段名称
     * @return
     */
    public Reflector field(String name) {
        mField = findField(name);
        mField.setAccessible(true);
        return this;
    }

    /**
     * 查找字段名称
     *      首先在本类中查找
     *          如果找到直接返回字段
     *          如果在本类中没有找到 , 就去遍历它的父类 , 尝试在父类中查找该字段
     *              如果有父类 , 则在父类中查找
     *                  如果在父类中找到 , 返回该字段
     *                  如果在父类中没有找到 , 则返回空
     *              如果没有父类 , 返回空
     *
     * 尽量传具体的正确的类 , 不要传子类
     * @param fieldName
     * @return
     */
    private Field findField(String fieldName) {
        try {
            // 首先在本类中查找 , 如果找到直接返回字段
            return mClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 如果在本类中没有找到 , 就去遍历它的父类 , 尝试在父类中查找该字段
            for (Class<?> clazz = mClass; clazz != null; clazz = clazz.getSuperclass()) {
                try {
                    // 如果在父类中找到 , 返回该字段
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ex) {
                    // 如果在父类中没有找到 , 则返回空
                    return null;
                }
            }
            // 如果没有父类, 则返回空
            return null;
        }
    }

    /**
     * 获取 mCaller 对象中的 mField 属性值
     *
     * @return
     */
    public Object get() {
        try {
            return mField.get(mCaller);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置 mCaller 对象中的 mField 属性值
     *
     * @param value
     * @return 链式调用 , 返回 Reflector
     */
    public Reflector set(Object value) {
        try {
            mField.set(mCaller, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 反射类中的某个方法
     *
     * @param name
     * @param args
     * @return
     */
    public Reflector method(String name, Class<?>... args) {
        mMethod = findMethod(name, args);
        mMethod.setAccessible(true);
        return this;
    }

    /**
     * 根据方法名 和 参数名称 , 查找 Method 方法
     *      首先在本类中查找
     *          如果找到直接返回字段
     *          如果在本类中没有找到 , 就去遍历它的父类 , 尝试在父类中查找该字段
     *              如果有父类 , 则在父类中查找
     *                  如果在父类中找到 , 返回该字段
     *                  如果在父类中没有找到 , 则返回空
     *              如果没有父类 , 返回空
     *
     * 尽量传具体的正确的类 , 不要传子类
     * @param name
     * @param args
     * @return
     */
    private Method findMethod(String name, Class<?>... args) {
        try {
            // 首先在本类中查找 , 如果找到直接返回方法
            return mClass.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e) {
            // 如果在本类中没有找到 , 就去遍历它的父类 , 尝试在父类中查找该方法
            for (Class<?> cls = mClass; cls != null; cls = cls.getSuperclass()) {
                try {
                    // 如果在父类中找到 , 返回该字段
                    return cls.getDeclaredMethod(name);
                } catch (NoSuchMethodException ex) {
                    // 如果在父类中没有找到 , 则返回空
                    return null;
                }
            }
            // 如果没有父类, 则返回空
            return null;
        }
    }

    /**
     * 调用 mCaller 的 mMethod 方法
     *
     * @param args
     * @param <T>
     * @return
     */
    public <T> T call(Object... args) {
        try {
            return (T) mMethod.invoke(mCaller, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

}