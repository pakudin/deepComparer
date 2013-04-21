// This file necessary only for writing documentation
//package npakudin;
//
//import npakudin.common.Action2;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * Some tests here fails.
// * It's necessary for documentation
// */
//public class Examples {
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        DeepComparer.setAssertAction(new Action2<Boolean, String>() {
//            @Override
//            public void apply(Boolean aBoolean, String s) {
//                Assert.assertTrue(s, aBoolean);
//            }
//        });
//    }
//
//    // fails with message
//    // $root; expected is null, actual is "qqq"
//    @Test
//    public void nullAndStringAreDifferent() {
//        DeepComparer.assertAreEqual(null, "qqq");
//    }
//
//    // fails with message
//    // $root; actual is null, expected is ""
//    @Test
//    public void stringAndNullAreDifferent() {
//        DeepComparer.assertAreEqual("", null);
//    }
//
//    // OK
//    @Test
//    public void nullsAreEqual() {
//        DeepComparer.assertAreEqual(null, null);
//    }
//
//
//    // fails with message
//    // $root; Expected type: class java.lang.String; actual type: class java.lang.Integer.
//    @Test
//    public void stringAndIntAreDifferent() {
//        DeepComparer.assertAreEqual("1", 1);
//    }
//
//    // OK
//    @Test
//    public void stringsAreEqual() {
//        DeepComparer.assertAreEqual("Hello 1", "Hello " + new Integer(1));
//    }
//
//    // OK
//    @Test
//    public void intsAreEqual() {
//        DeepComparer.assertAreEqual(1, 1);
//    }
//
//    // fails with message
//    // $root; Expected value: "1"; actual value: "2".
//    @Test
//    public void intsAreDifferent() {
//        DeepComparer.assertAreEqual(1, 2);
//    }
//
//
//
//
//    class PublicField {
//        public int x;
//        public PublicField(int x) {
//            this.x = x;
//        }
//    }
//    // OK
//    @Test
//    public void publicFieldsAreEqual() {
//        DeepComparer.assertAreEqual(new PublicField(1), new PublicField(1));
//    }
//
//    // fails with message
//    // $root.x; Expected value: "1"; actual value: "2"
//    @Test
//    public void publicFieldsAreDifferent() {
//        DeepComparer.assertAreEqual(new PublicField(1), new PublicField(2));
//    }
//
//
//
//    class PublicProperty {
//        private int x;
//        public int getX() {
//            return x;
//        }
//        public PublicProperty(int x) {
//            this.x = x;
//        }
//
//    }
//
//    //OK
//    @Test
//    public void publicPropertiesAreEqual() {
//        DeepComparer.assertAreEqual(new PublicProperty(1), new PublicProperty(1));
//    }
//
//    // fails with message
//    // $root.getX; Expected value: "1"; actual value: "2".
//    @Test
//    public void publicPropertiesAreDifferent() {
//        DeepComparer.assertAreEqual(new PublicProperty(1), new PublicProperty(2));
//    }
//
//
//
//    class ListField {
//        private List<Integer> x;
//        public ListField(List<Integer> x) {
//            this.x = x;
//        }
//        public List<Integer> getX() {
//            return x;
//        }
//        public void setX(List<Integer> x) {
//            this.x = x;
//        }
//    }
//
//    //OK
//    @Test
//    public void listFieldsAreEqual() {
//        DeepComparer.assertAreEqual(
//                new ListField(Arrays.asList( 1, 2, 3 )),
//                new ListField(Arrays.asList( 1, 2, 3 )));
//    }
//
//    // fails with message
//    // $root.getX.[2]; Expected value: "3"; actual value: "4"
//    @Test
//    public void listFieldsAreDifferent() {
//        DeepComparer.assertAreEqual(
//                new ListField(Arrays.asList( 1, 2, 3 )),
//                new ListField(Arrays.asList( 1, 2, 4 )));
//    }
//
//
//
//
//    class CycleReference {
//        private int id;
//        private CycleReference reference;
//        public int getId() {
//            return id;
//        }
//        public CycleReference getReference() {
//            return reference;
//        }
//        public CycleReference(int id, CycleReference reference) {
//            this.id = id;
//            this.reference = reference;
//        }
//    }
//
//    //OK
//    @Test
//    public void cycleReferencesAreEqual() {
//        CycleReference expected = new CycleReference(0, null);
//        expected.reference = expected;
//
//        CycleReference actual = new CycleReference(0, null);
//        actual.reference = actual;
//
//        DeepComparer.assertAreEqual(expected, actual);
//    }
//
//    // fails with message
//    // $root.getReference.getReference; actual is null, expected is "npakudin.Examples$CycleReference@4229ab3e".
//    @Test
//    public void cycleReferencesAreDifferent() {
//        CycleReference expected = new CycleReference(0, null);
//        expected.reference = expected;
//
//        CycleReference actual = new CycleReference(0, new CycleReference(0, null));
//
//        DeepComparer.assertAreEqual(expected, actual);
//    }
//
//    // fails with message
//    // $root.getReference; actual is null, expected is npakudin.Examples$CycleReference@32ef2c60.
//    // at each test run number after '@' can be different
//    @Test
//    public void cycleReferencesErrorMessage() {
//        CycleReference expected = new CycleReference(1, new CycleReference(0, null));
//        CycleReference actual = new CycleReference(1, null);
//
//        DeepComparer.assertAreEqual(expected, actual);
//    }
//
//    // OK
//    @Test
//    public void cycleReferencesVeryDeepDifference() {
//        CycleReference expected = null;
//        CycleReference actual = null;
//
//        for (int i=0; i<100; i++) {
//            expected = new CycleReference(i, expected);
//            actual = new CycleReference(i, actual);
//        }
//
//        DeepComparer.assertAreEqual(expected, actual);
//    }
//
//
//
//    // fails with message
//    // $root; Size of expected: 3; size of actual: 2.
//    @Test
//    public void iterablesAreDifferent() {
//        DeepComparer.assertAreEqual(
//                new ArrayList<String>(Arrays.asList("foo", "bar", "baz")),
//                new LinkedList<String>(Arrays.asList("foo", "bar")));
//    }
//
//    //OK
//    @Test
//    public void iterablesOfDifferentTypesAreEqual() {
//        DeepComparer.assertAreEqual(
//                new ArrayList<String>(Arrays.asList("foo", "bar", "baz")),
//                new LinkedList<String>(Arrays.asList("foo", "bar", "baz")));
//    }
//
//    // fails with message
//    // $root; Expected type: class java.util.ArrayList; actual type: class java.util.LinkedList.
//    @Test
//    public void iterablesOfDifferentTypesAreDifferent() {
//        DeepComparer.assertAreEqual(
//                new ArrayList<String>(Arrays.asList("foo", "bar", "baz")),
//                new LinkedList<String>(Arrays.asList("foo", "bar", "baz")),
//                true);
//    }
//
//
//
//
//    class Node {
//        private Node parent;
//        private List<Node> children;
//
//        public Node() {
//        }
//        public Node(Node parent, List<Node> children) {
//            this.parent = parent;
//            this.children = children;
//        }
//        public Node getParent() {
//            return parent;
//        }
//        public void setParent(Node parent) {
//            this.parent = parent;
//        }
//        public List<Node> getChildren() {
//            return children;
//        }
//        public void setChildren(List<Node> children) {
//            this.children = children;
//        }
//    }
//
//    //OK
//    @Test
//    public void differentNodesAreEqualWithExcludingSomePaths() {
//        Node root1 = new Node(null, Arrays.asList(new Node(), new Node()));
//        Node root2 = new Node(null, Arrays.asList(new Node(), new Node()));
//        root2.children.get(0).setParent(root2);
//        root2.children.get(1).setParent(root2);
//
//        DeepComparer.assertAreEqual(root2, root1,
//                "$root.getChildren.[0]",
//                "$root.getChildren.[1]");
//    }
//
//    //OK
//    @Test
//    public void differentNodesAreEqualWithExcludingSomeRegexPaths() {
//        Node root1 = new Node(null, Arrays.asList(new Node(), new Node()));
//        Node root2 = new Node(null, Arrays.asList(new Node(), new Node()));
//        root2.children.get(0).setParent(root2);
//        root2.children.get(1).setParent(root2);
//
//        DeepComparer.assertAreEqual(root2, root1, "$root.getChildren.[*].getParent");
//    }
//
//
//    class OverriddenEquals {
//        private int internalId;
//        private int publicId;
//
//        public OverriddenEquals(int internalId, int publicId) {
//            this.internalId = internalId;
//            this.publicId = publicId;
//        }
//
//        public int getInternalId() {
//            return internalId;
//        }
//        public int getPublicId() {
//            return publicId;
//        }
//
//        // automatically generated by IDEA
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            OverriddenEquals that = (OverriddenEquals) o;
//
//            if (publicId != that.publicId) return false;
//
//            return true;
//        }
//        @Override
//        public int hashCode() {
//            return publicId;
//        }
//    }
//
//    // fails with message
//    // $root; Expected value: "npakudin.Examples$OverriddenEquals@1"; actual value: "npakudin.Examples$OverriddenEquals@2".
//    @Test
//    public void overriddenEqualsAreDifferent() {
//        DeepComparer.assertAreEqual(new OverriddenEquals(1, 1), new OverriddenEquals(2, 2));
//    }
//
//    //OK
//    @Test
//    public void overriddenEqualsAreEqual() {
//        DeepComparer.assertAreEqual(new OverriddenEquals(1, 1), new OverriddenEquals(2, 1));
//    }
//}
