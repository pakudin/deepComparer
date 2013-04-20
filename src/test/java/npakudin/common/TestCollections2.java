package npakudin.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCollections2 {

    private static List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);

    @Test
    public void testAnyMatches() {
        Assert.assertTrue(Collections2.any(source, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer item) {
                return item >= 3;
            }
        }));
    }
    @Test
    public void testAnyNotMatches() {
        Assert.assertFalse(Collections2.any(source, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer item) {
                return item >= 10;
            }
        }));
    }
    @Test
    public void testAnyNotMatchesEmptyCollection() {
        Assert.assertFalse(Collections2.any(new ArrayList<Integer>(), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer item) {
                return true;
            }
        }));
    }

    @Test
    public void testFilter() {
        Assert.assertArrayEquals(new Integer[] { 3, 4, 5 },
                Collections2.filter(source, new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer item) {
                        return item >= 3;
                    }
                }).toArray());
    }
    @Test
    public void testFilterToEmpty() {
        Assert.assertArrayEquals(new Integer[] { },
                Collections2.filter(source, new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer item) {
                        return item >= 10;
                    }
                }).toArray());
    }
    @Test
    public void testFilterEmpty() {
        Assert.assertArrayEquals(new Integer[] { },
                Collections2.filter(new ArrayList<Integer>(), new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer item) {
                        return true;
                    }
                }).toArray());
    }

    @Test
    public void testTransform() {
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4", "5"},
                Collections2.transform(source, new Function<Integer, String>() {
                    @Override
                    public String apply(Integer item) {
                        return item.toString();
                    }
                }).toArray());
    }
    @Test
    public void testTransformEmpty() {
        Assert.assertArrayEquals(new String[] {},
                Collections2.transform(new ArrayList<Integer>(), new Function<Integer, String>() {
                    @Override
                    public String apply(Integer item) {
                        return item.toString();
                    }
                }).toArray());
    }

}
