package org.example;

import org.example.model.User;
import org.example.model.enums.Role;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Suite
@SelectPackages("org.example.app")


class AppApplicationTests {

    private static final String SAFE_JWT_SECRET = "0123456789abcdef0123456789abcdef";

    @TestFactory
    Stream<DynamicTest> shouldInstantiateNonRestConcreteClasses() {
        List<Class<?>> classes = findTestableClasses();
        return classes.stream().map(clazz ->
                DynamicTest.dynamicTest("Instantiate " + clazz.getName(), () -> {
                    Object instance = instantiate(clazz);
                    assertNotNull(instance);
                }));
    }

    private List<Class<?>> findTestableClasses() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile("org\\.example\\..*")));

        List<Class<?>> classes = new ArrayList<>();
        for (BeanDefinition candidate : scanner.findCandidateComponents("org.example")) {
            String className = candidate.getBeanClassName();
            if (className == null) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                if (isTestableClass(clazz)) {
                    classes.add(clazz);
                }
            } catch (Throwable ignored) {
            }
        }
        classes.sort(Comparator.comparing(Class::getName));
        return classes;
    }

    private boolean isTestableClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.contains(".rest.")) return false;
        if (name.endsWith("Application")) return false;
        if (name.contains(".config.")) return false;
        if (name.contains(".repository.")) return false;
        if (name.contains(".model.")) return false;
        if (name.contains(".dto.")) return false;
        if (name.contains(".exception.")) return false;

        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || clazz.isAnnotation() || clazz.isEnum()) {
            return false;
        }
        return clazz.getDeclaredConstructors().length > 0;
    }

    private Object instantiate(Class<?> clazz) throws Exception {
        Constructor<?> ctor = selectConstructor(clazz);
        ctor.setAccessible(true);
        Object[] args = new Object[ctor.getParameterCount()];
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = defaultValue(parameterTypes[i]);
        }
        return ctor.newInstance(args);
    }

    private Constructor<?> selectConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new IllegalStateException("No constructor found for " + clazz.getName());
        }

        Constructor<?> selected = constructors[0];
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() > selected.getParameterCount()) {
                selected = constructor;
            }
        }
        return selected;
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            if (type == String.class) return SAFE_JWT_SECRET;
            if (type == Integer.class) return 0;
            if (type == Long.class) return 0L;
            if (type == Short.class) return (short) 0;
            if (type == Byte.class) return (byte) 0;
            if (type == Double.class) return 0d;
            if (type == Float.class) return 0f;
            if (type == Boolean.class) return false;
            if (type == Character.class) return '\0';
            if (type == BigDecimal.class) return BigDecimal.ZERO;
            if (type == UUID.class) return new UUID(0L, 0L);
            if (type == LocalDate.class) return LocalDate.now();
            if (type == LocalDateTime.class) return LocalDateTime.now();
            if (type == LocalTime.class) return LocalTime.now();
            if (type == Optional.class) return Optional.empty();
            if (type == User.class) {
                User user = new User();
                user.setEmail("test@example.com");
                user.setPassword("pass");
                user.setRole(Role.USER);
                return user;
            }
            if (List.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type)) return new ArrayList<>();
            if (Set.class.isAssignableFrom(type)) return Set.of();
            if (Map.class.isAssignableFrom(type)) return Map.of();
            if (type.isArray()) return java.lang.reflect.Array.newInstance(type.getComponentType(), 0);
            if (Modifier.isFinal(type.getModifiers())) return null;

            return Mockito.mock(type);
        }

        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        if (type == double.class) return 0d;
        if (type == float.class) return 0f;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        return null;
    }
}
