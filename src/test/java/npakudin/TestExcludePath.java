package npakudin;

import org.junit.*;

public class TestExcludePath {
    @Test
    public void testStringMatch() {
        ExcludePath excludePath = new ExcludePath("foo.bar");
        Assert.assertTrue(excludePath.needExcludePath("foo.bar"));
    }
    @Test
    public void testStringNotMatch() {
        ExcludePath excludePath = new ExcludePath("foo.bar");
        Assert.assertFalse(excludePath.needExcludePath("foo.foo"));
    }
    @Test
    public void testRegexMatch1() {
        ExcludePath excludePath = new ExcludePath("foo.[*]");
        Assert.assertTrue(excludePath.needExcludePath("foo.[10]"));
    }
    @Test
    public void testRegexNotMatch1() {
        ExcludePath excludePath = new ExcludePath("foo.[*]");
        Assert.assertFalse(excludePath.needExcludePath("foo.bar"));
    }
    @Test
    public void testRegexMatch2() {
        ExcludePath excludePath = new ExcludePath("foo.[?]");
        Assert.assertTrue(excludePath.needExcludePath("foo.[1]"));
    }
    @Test
    public void testRegexNotMatch2() {
        ExcludePath excludePath = new ExcludePath("foo.[?]");
        Assert.assertFalse(excludePath.needExcludePath("foo.[10]"));
    }
}
