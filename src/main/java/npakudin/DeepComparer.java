package npakudin;

import npakudin.common.Action2;
import npakudin.common.Collections2;
import npakudin.common.Function;
import npakudin.common.Predicate;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Class for helping with Assert.areEqual for objects with deep graph in fields and properties.
 * ATTENTION! Before using methods assertAreEqual(...) you must set assertAction.
 * If object overrides method equals, this method id used for comparing.
 * If not - all public fields and results of get methods are used for comparing.
 */
public class DeepComparer
{
    private static Action2<Boolean, String> assertAction;
    public static Action2<Boolean, String>  getAssertAction() {
        return assertAction;
    }
    /**
     * Method for assert.
     * Necessary for different unit test frameworks.
     * Usage:
     *
     *   @ BeforeClass
     *   public static void setUpClass() throws Exception {
     *       DeepComparer.setAssertAction(new Action2<Boolean, String>() {
     *           @ Override
     *           public void apply(Boolean aBoolean, String s) {
     *               Assert.assertTrue(s, aBoolean);
     *           }
     *       });
     *   }
     */
    public static void setAssertAction(Action2<Boolean, String> action) {
        assertAction = action;
    }


    /**
     * Checks all public fields and getters recursively.
     * It checks iterables and maps item-by-item.
     *
     * Checks cycle reference correctly.
     * For tests only because of using reflection.
     * Excludes excludePaths from equation.
     * Exact type of iterables is not checked.
     *
     * Fails if not equal.
     */
    public static <T> void assertAreEqual(T expected, T actual, String... excludePaths) {
        assertAreEqual(expected, actual, false, excludePaths);
    }
    /**
     * Checks all public fields and getters recursively.
     * It checks iterables and maps item-by-item.
     *
     * Checks cycle reference correctly.
     * For tests only because of using reflection.
     * Excludes excludePaths from equation.
     *
     * @param checkIterableTypesStrictly if true, check exact type of iterables, otherwise - not check
     * Fails if not equal.
     */
    public static <T> void assertAreEqual(T expected, T actual, boolean checkIterableTypesStrictly,  String... excludePaths) {
        String res = getNotEqualPath(expected, actual, checkIterableTypesStrictly, excludePaths);
        assertAction.apply(res == null, res);
    }



    /**
     * Checks all public fields and getters recursively.
     * It checks iterables and maps item-by-item.
     *
     * Checks cycle reference correctly.
     * For tests only because of using reflection.
     * Excludes excludePaths from equation.
     * Exact type of iterables is not checked.
     *
     * @return null if items are equal, otherwise string with error
     */
    public static <T> boolean areEqual(T expected, T actual, String... excludePaths) {
        return areEqual(expected, actual, false, excludePaths);
    }
    /**
     * Checks all public fields and getters recursively.
     * It checks iterables and maps item-by-item.
     *
     * Checks cycle reference correctly.
     * For tests only because of using reflection.
     * Excludes excludePaths from equation.
     *
     * @param checkIterableTypesStrictly if true, check exact type of iterables, otherwise - not check
     * @return null if items are equal, otherwise string with error
     */
    public static <T> boolean areEqual(T expected, T actual, boolean checkIterableTypesStrictly, String... excludePaths) {
        return getNotEqualPath(expected, actual, checkIterableTypesStrictly, excludePaths) == null;
    }

    /**
     * Checks all public fields and getters recursively.
     * It checks iterables and maps item-by-item.
     *
     * Checks cycle reference correctly.
     * For tests only because of using reflection.
     * Excludes excludePaths from equation.
     * Exact type of iterables is not checked.
     *
     * @return null if items are equal, otherwise string with error
     */
    public static <T> String getNotEqualPath(T expected, T actual, String... excludePath) {
        return getNotEqualPath(expected, actual, false, excludePath);
    }
    /**
     * Checks all public fields and getters recursively.
     * It checks iterables and maps item-by-item.
     *
     * Checks cycle reference correctly.
     * For tests only because of using reflection.
     * Excludes excludePaths from equation.
     *
     * @param checkIterableTypesStrictly if true, check exact type of iterables, otherwise - not check
     * @return null if items are equal, otherwise string with error
     */
    public static <T> String getNotEqualPath(T expected, T actual, boolean checkIterableTypesStrictly, String... excludePath) {
        return getNotEqualPath("$root", expected, actual, new ArrayList<StackEntry>(), checkIterableTypesStrictly,
                Collections2.transform(Arrays.asList(excludePath), new Function<String, ExcludePath>() {
                    @Override
                    public ExcludePath apply(String item) {
                        return new ExcludePath(item);
                    }
                }));
    }

    /**
     * Deeply compares maps item-by-item.
     * @return null if items are equal, otherwise string with error
     */
    private static <T> String getMapsNotEqualPath(Map expectedMap, Map actualMap, List<StackEntry> referencesStack,
                                                  boolean checkIterableTypesStrictly, Collection<ExcludePath> excludePath) {
        // check sizes
        if (expectedMap.size() != actualMap.size())
            return stackToPath(referencesStack, String.format("Size of expected: %d; size of actual: %d.", expectedMap.size(), actualMap.size()));

        // check key-by-key
        for (Object key : expectedMap.keySet()) {
            if (!actualMap.containsKey(key))
                return stackToPath(referencesStack, String.format("actual object does not contain key %s.", key));

            Object expectedVal = expectedMap.get(key);
            Object actualVal = actualMap.get(key);
            String tmp = getNotEqualPath(String.format("[%s]", key), expectedVal, actualVal,
                    referencesStack, checkIterableTypesStrictly, excludePath);
            if (tmp != null)
                return tmp;
        }

        return null;
    }

    private static List arrayToList(Object array) {
        Class clazz = array.getClass().getComponentType();
        ArrayList res = new ArrayList();

        if (!clazz.isPrimitive()) {
            Collections.addAll(res, (Object[])array);
            return res;
        }

        for (int i=0; i<Array.getLength(array); i++) {
            if (clazz == boolean.class) {
                res.add(Array.getBoolean(array, i));
            } else if (clazz == char.class) {
                res.add(Array.getChar(array, i));
            } else if (clazz == long.class) {
                res.add(Array.getLong(array, i));
            } else if (clazz == int.class) {
                res.add(Array.getInt(array, i));
            } else if (clazz == short.class) {
                res.add(Array.getShort(array, i));
            } else if (clazz == byte.class) {
                res.add(Array.getByte(array, i));
            } else if (clazz == double.class) {
                res.add(Array.getDouble(array, i));
            } else if (clazz == float.class) {
                res.add(Array.getFloat(array, i));
            }
        }
        return res;
    }

    /**
     * Deeply compares collections item-by-item.
     * @return null if items are equal, otherwise string with error
     */
    private static String getIterableNotEqualPath(Iterable expected, Iterable actual, List<StackEntry> referencesStack,
                                                  boolean checkIterableTypesStrictly, Collection<ExcludePath> excludePath) {
        // Check sizes of Collection<T> and T[]
        if (expected instanceof Collection && actual instanceof Collection) {
            Collection expectedCollection = (Collection)expected;
            Collection actualCollection = (Collection)actual;

            if (expectedCollection.size() != actualCollection.size()) {
                return stackToPath(referencesStack, String.format(
                        "Size of expected: %d; size of actual: %d.",
                        expectedCollection.size(), actualCollection.size()));
            }
        }

        Iterator expectedIterator = expected.iterator();
        Iterator actualIterator = actual.iterator();
        {
            int index = 0;
            boolean expectedMove;
            boolean actualMove;

            // check item-by-item
            while (true) {
                expectedMove = expectedIterator.hasNext();
                actualMove = actualIterator.hasNext();

                if (!expectedMove || !actualMove)
                    break;

                Object exp = expectedIterator.next();
                Object act = actualIterator.next();

                String tmp = getNotEqualPath(String.format("[%s]", index), exp, act,
                        referencesStack, checkIterableTypesStrictly, excludePath);
                if (tmp != null)
                    return tmp;

                index++;
            }

            // If there are no items in one collection and there are in other, return false
            if (expectedMove)
                return stackToPath(referencesStack, String.format(
                        "Sizes are different. Size of expected: >%d; size of actual: =%d.", index, index));
            if (actualMove)
                return stackToPath(referencesStack, String.format(
                        "Sizes are different. Size of expected: =%d; size of actual: >%d.", index, index));
        }
        return null;
    }


    private static <T> String getNotEqualPath(String name, T expected, T actual, List<StackEntry> referencesStack,
                                              boolean checkIterableTypesStrictly, Collection<ExcludePath> excludePath) {
        try {
            // push current object to checking stack
            referencesStack.add(new StackEntry(name, expected, actual));

            // do not check this path
            if (needExclude(stackToPath(referencesStack), excludePath))
                return null;


            // Obvious checks
            {
                if (expected == actual) return null;

                if (expected == null)
                    return stackToPath(referencesStack, String.format("expected is null, actual is \"%s\".", actual));
                if (actual == null)
                    return stackToPath(referencesStack, String.format("actual is null, expected is \"%s\".", expected));

                if ((!(expected instanceof Iterable) || !(actual instanceof Iterable)) &&
                        expected.getClass() != actual.getClass())
                {
                    // if expected is Iterable, then we can compare items only, without details of collection type
                    // else - return error
                    return stackToPath(referencesStack, String.format("Expected type: %s; actual type: %s.",
                            expected.getClass(), actual.getClass()));
                }
            }
            Class clazz = expected.getClass();

            // If primitive type
            if (clazz.isPrimitive() || clazz.isEnum() || clazz == String.class || clazz == Long.class
                    || clazz == Integer.class || clazz == Short.class || clazz == Byte.class || clazz == Double.class
                    || clazz == Float.class || clazz == Boolean.class || clazz == Character.class) {
                return expected.equals(actual) ? null : stackToPath(referencesStack, String.format("Expected value: \"%s\"; actual value: \"%s\".", expected, actual));
            }

            // If object A references to A, and object B references to B, then true.
            // There are current object in last item in the stack, do not check it
            for (int i = 0; i < referencesStack.size() - 1; i++) {
                if (referencesStack.get(i).getExpected() == expected &&
                        referencesStack.get(i).getActual() == actual)
                {
                    return null;
                }
            }

            // if class overrides method equals - use it
            try {
                clazz.getDeclaredMethod("equals", Object.class); // throws exception if no method
                return expected.equals(actual) ? null : stackToPath(referencesStack,
                        String.format("Expected value: \"%s\"; actual value: \"%s\".", expected, actual));
            } catch (NoSuchMethodException e) {
                // if method equals if not overridden - do nothing
            }


            // for maps
            if (expected instanceof Map)
                return getMapsNotEqualPath((Map) expected, (Map) actual, referencesStack, checkIterableTypesStrictly, excludePath);

            // for iterables
            if (expected instanceof Iterable) {
                return getIterableNotEqualPath((Iterable) expected, (Iterable) actual,
                        referencesStack, checkIterableTypesStrictly, excludePath);
            }

            // for arrays
            if (clazz.isArray()) {
                // converting to List - it's fraud, but for testing purposes is OK
                return getIterableNotEqualPath(arrayToList(expected), arrayToList(actual),
                        referencesStack, checkIterableTypesStrictly, excludePath);
            }

            // loop at all public fields
            for (Field field : clazz.getFields()) {
                try {
                    Object expectedValue = field.get(expected);
                    Object actualValue = field.get(actual);

                    // recursive call for each field
                    String tmp = getNotEqualPath(field.getName(), expectedValue, actualValue, referencesStack, checkIterableTypesStrictly, excludePath);
                    if (tmp != null)
                        return tmp;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }

            // loop at all getters
            for (Method method : clazz.getMethods()) {
                if (!method.getName().startsWith("get")) {
                    // of course, it's some dirty, but for small and medium projects is OK
                    continue;
                }
                try {
                    Object expectedValue = method.invoke(expected);
                    Object actualValue = method.invoke(actual);

                    // recursive call for each property
                    String tmp = getNotEqualPath(method.getName(), expectedValue, actualValue, referencesStack, checkIterableTypesStrictly, excludePath);
                    if (tmp != null)
                        return tmp;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }


            return null;
        } finally {
            // pop current object from the stack
            referencesStack.remove(referencesStack.size() - 1);
        }
    }

    private static boolean needExclude(final String path, Collection<ExcludePath> excludePath) {
        return Collections2.any(excludePath, new Predicate<ExcludePath>() {
            @Override
            public boolean apply(ExcludePath item) {
                return item.needExcludePath(path);
            }
        });
    }
    private static String stackToPath(List<StackEntry> referencesStack, String reason) {
        return stackToPath(referencesStack) + String.format("; %s", reason);
    }
    private static String stackToPath(List<StackEntry> referencesStack) {
        StringBuilder sb = new StringBuilder();
        for (StackEntry stackEntry : referencesStack) {
            sb.append(String.format(".%s", stackEntry.getName()));
        }
        sb.replace(0, 1, "");
        return sb.toString();
    }

}