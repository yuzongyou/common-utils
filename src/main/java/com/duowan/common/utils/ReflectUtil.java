package com.duowan.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 反射工具类
 *
 * @author Arvin
 */
public abstract class ReflectUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    public static List<Class<?>> getAllSuperclasses(final Class<?> cls, boolean includeSelf) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        if (includeSelf) {
            classes.add(0, cls);
        }
        return classes;
    }

    /**
     * 查找指定类型的属性列表
     *
     * @param clazz     类名
     * @param fieldType 字段类型， 如果为空就返回所有的字段
     * @return 返回指定类型的属性列表，如果为空就返回所有的字段
     */
    public static List<Field> getNoneStaticDeclaredFields(Class<?> clazz, Class<?> fieldType) {
        if (null == clazz) {
            return new ArrayList<Field>();
        }
        List<Field> fields = new ArrayList<>();

        List<Class<?>> classes = getAllSuperclasses(clazz, true);

        for (Class<?> cls : classes) {
            Field[] declaredFields = cls.getDeclaredFields();
            if (null != declaredFields && declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    // 过滤静态属性
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    if (null == fieldType) {
                        fields.add(field);
                    } else if (field.getType().equals(fieldType)) {
                        fields.add(field);
                    }
                }
            }
        }
        return fields;
    }

    /**
     * 获取指定类型所有的非静态属性字段
     *
     * @param clazz 类
     * @return
     */
    public static List<Field> getAllNoneStaticDeclaredFields(Class<?> clazz) {
        return getNoneStaticDeclaredFields(clazz, null);
    }

    /**
     * 获取字段的值
     */
    public static Object getFieldValue(Object obj, Field field) {
        if (null == obj || null == field) {
            return null;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 获取字段的值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (null == obj || StringUtils.isBlank(fieldName)) {
            return null;
        }
        Field field = findDeclaredField(obj.getClass(), fieldName);
        if (null == field) {
            return null;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 搜索字段，包含私有的，从当前类开始搜索，如果当前类没有，继续往父类中查找，直到找到或到Object为止
     */
    public static Field findDeclaredField(Class<?> clazz, String fieldName) {
        if (null == clazz || StringUtils.isBlank(fieldName)) {
            return null;
        }
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    /**
     * 设置属性的值
     */
    public static void setFieldValue(Object obj, Field field, Object value) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            Class<?> type = field.getType();
            if (type.equals(Integer.class) || type.equals(int.class)) {
                field.set(obj, ((Number) value).intValue());
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                field.set(obj, ((Number) value).longValue());
            } else {
                field.set(obj, value);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    /**
     * 设置属性的值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Field field = findDeclaredField(obj.getClass(), fieldName);
        if (null == field) {
            return;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException ignored) {
        }
    }

    /**
     * 获取指定类的指定类型的Annotation
     *
     * @param clazz           类
     * @param annotationClass annotation class
     * @param <A>             返回指定类型的Annotation
     * @return 如果不存在则返回null
     */
    public static <A extends Annotation> A getClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    /**
     * 获取指定类中含有特定注解的方法列表, 同时会搜索父类的方法
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @return 返回方法列表
     */
    public static <A extends Annotation> List<Method> getMethodForSpecificAnnotation(Class<?> clazz, Class<A> annotationClass) {

        List<Method> methodList = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        if (null != methods && methods.length > 0) {

            for (Method method : methods) {
                A annotation = getMethodAnnotation(method, annotationClass);
                if (annotation != null) {
                    methodList.add(method);
                }
            }
        }
        return methodList;
    }

    /**
     * 获取指定属性的指定类型的Annotation
     *
     * @param field           属性
     * @param annotationClass annotation class
     * @param <A>             返回指定类型的Annotation
     * @return 如果不存在则返回null
     */
    public static <A extends Annotation> A getFieldAnnotation(Field field, Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    /**
     * 获取指定方法的指定类型的Annotation
     *
     * @param method          属性
     * @param annotationClass annotation class
     * @param <A>             返回指定类型的Annotation
     * @return 如果不存在则返回null
     */
    public static <A extends Annotation> A getMethodAnnotation(Method method, Class<A> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    /**
     * 搜索 packageName 包下面的 实现Or 继承了 superClass 的类， 并且使用默认构造函数进行实例化
     * <p>
     * 注意，指定的包名下的类，必须是 public 的
     *
     * @param superClass   超类
     * @param packageNames 包名列表，使用英文逗号分隔
     * @param <T>          超类类型
     * @return 返回实例列表
     */
    public static <T> List<T> scanAndInstanceByDefaultConstructor(Class<T> superClass, String packageNames) {

        List<T> instanceList = new ArrayList<>();
        Set<String> packages = splitPackageNames(packageNames);
        if (packages == null || packages.isEmpty()) {
            return instanceList;
        }

        for (String packageName : packages) {
            List<T> subInstanceList = scanAndInstanceByDefaultConstructorWithSinglePackage(superClass, packageName);
            if (null != subInstanceList && !subInstanceList.isEmpty()) {
                instanceList.addAll(subInstanceList);
            }
        }

        return instanceList;

    }

    /**
     * 拆分包名
     *
     * @param packageNames 包名列表，可以是逗号、空白字符，竖线分隔
     * @return 返回包名集合
     */
    public static Set<String> splitPackageNames(String packageNames) {

        if (StringUtils.isBlank(packageNames)) {
            return null;
        }
        Set<String> packages = new HashSet<>();
        String[] array = packageNames.split("[\\s,，\\|;]+");
        for (String packageName : array) {
            if (StringUtils.isNotBlank(packageName)) {
                packages.add(packageName);
            }
        }

        return packages;
    }

    /**
     * 搜索 packageName 包下面的 实现Or 继承了 superClass 的类， 并且使用默认构造函数进行实例化
     * <p>
     * 注意，指定的包名下的类，必须是 public 的
     *
     * @param superClass  超类
     * @param packageName 单个包名
     * @param <T>         超类类型
     * @return 返回实例列表
     */
    public static <T> List<T> scanAndInstanceByDefaultConstructorWithSinglePackage(Class<T> superClass, String packageName) {
        Set<Class<?>> classes = ClassUtil.scan(superClass, packageName);
        List<T> instances = new ArrayList<>();

        if (null != classes && !classes.isEmpty()) {

            for (Class<?> clazz : classes) {

                T instance = newInstanceByDefaultConstructor(superClass, clazz);

                if (null != instance) {
                    instances.add(instance);
                }

            }

        }

        return instances;
    }


    /**
     * 使用默认构造函数生成多个实例
     *
     * @param superClass 超类，要生成的类实例必须继承这个类
     * @param classNames 要实例化的类,注意，该类必须是 public 的,中间使用 , 分割
     * @param <T>        结果
     * @return 返回实例对象
     */
    public static <T> List<T> newInstancesByDefaultConstructor(Class<T> superClass, String classNames) {
        if (StringUtils.isBlank(classNames)) {
            return new ArrayList<>();
        }

        List<T> instanceList = new ArrayList<>();
        String[] classArray = classNames.split("[|，,;；\\s]+");
        for (String className : classArray) {
            if (StringUtils.isNotBlank(className)) {
                T instance = newInstanceByDefaultConstructor(superClass, className.trim());
                if (null != instance) {
                    instanceList.add(instance);
                }
            }
        }
        return instanceList;
    }

    /**
     * 使用默认构造函数生成实例
     *
     * @param superClass 超类，要生成的类实例必须继承这个类
     * @param className  要实例化的类,注意，该类必须是 public 的
     * @param <T>        结果
     * @return 返回实例对象
     */
    public static <T> T newInstanceByDefaultConstructor(Class<T> superClass, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return newInstanceByDefaultConstructor(superClass, clazz);
        } catch (Exception e) {
            logger.warn("实例化类[" + className + "]失败： " + e.getMessage());
            return null;
        }
    }

    /**
     * 使用默认构造函数生成实例
     *
     * @param superClass 超类，要生成的类实例必须继承这个类
     * @param clazz      要实例化的类,注意，该类必须是 public 的
     * @param <T>        结果
     * @return 返回实例对象
     */
    public static <T> T newInstanceByDefaultConstructor(Class<T> superClass, Class<?> clazz) {
        if (null == clazz) {
            return null;
        }

        try {

            if (!superClass.isAssignableFrom(clazz)) {
                return null;
            }

            int mod = clazz.getModifiers();

            if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
                return null;
            }

            Constructor<?> constructor = clazz.getConstructor();

            if (constructor == null) {
                throw new RuntimeException("指定的类[" + clazz.getName() + "] 没有一个默认的无参构造函数！");
            }

            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return superClass.cast(constructor.newInstance());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader) {
        if (null == classLoader) {
            classLoader = ReflectUtil.class.getClassLoader();
        }
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断一个类是否可以通过默认构造函数进行实例化
     *
     * @param clazz 类
     * @return 返回是否允许实例化
     */
    public static boolean canInstanceByDefaultConstructor(Class<?> clazz) {
        int mod = clazz.getModifiers();

        if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
            return false;
        }

        try {
            return clazz.getConstructor() != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
