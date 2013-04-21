package npakudin;

import npakudin.common.Action2;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class TestDeepComparer {
    @BeforeClass
    public static void setUpClass() throws Exception {
        DeepComparer.setAssertAction(new Action2<Boolean, String>() {
            @Override
            public void apply(Boolean aBoolean, String s) {
                Assert.assertTrue(s, aBoolean);
            }
        });
    }

    @Test
    public void null1Test() {
        Assert.assertFalse(DeepComparer.areEqual(null, "qqq"));
    }
    @Test
    public void null2Test() {
        Assert.assertFalse(DeepComparer.areEqual("", null));
    }
    @Test
    public void bothNullTest() {
        Assert.assertTrue(DeepComparer.areEqual(null, null));
    }


    @Test
    public void stringAndIntTestFalse() {
        Assert.assertFalse(DeepComparer.areEqual("1", 1));
    }
    @Test
    public void stringTestTrue() {
        Assert.assertTrue(DeepComparer.areEqual("Hello 1", "Hello " + new Integer(1)));
    }
    @Test
    public void intTestTrue() {
        Assert.assertTrue(DeepComparer.areEqual(1, 1));
    }
    @Test
    public void intTestFalse() {
        Assert.assertFalse(DeepComparer.areEqual(1, 2));
    }




    class PublicField {
        public int x;
        public PublicField(int x) {
            this.x = x;
        }
    }
    @Test
    public void publicFieldTestTrue() {
        Assert.assertTrue(DeepComparer.areEqual(new PublicField(1), new PublicField(1)));
    }
    @Test
    public void publicFieldTestFalse() {
        Assert.assertFalse(DeepComparer.areEqual(new PublicField(1), new PublicField(2)));
    }



    class PublicProperty {
        private int x;
        public int getX() {
            return x;
        }
        public PublicProperty(int x) {
            this.x = x;
        }

    }
    @Test
    public void publicPropertyTestTrue() {
        Assert.assertTrue(DeepComparer.areEqual(new PublicProperty(1), new PublicProperty(1)));
    }
    @Test
    public void publicPropertyTestFalse() {
        Assert.assertFalse(DeepComparer.areEqual(new PublicProperty(1), new PublicProperty(2)));
    }



    class ListField {
        private List<Integer> x;
        public ListField(List<Integer> x) {
            this.x = x;
        }
        public List<Integer> getX() {
            return x;
        }
        public void setX(List<Integer> x) {
            this.x = x;
        }
    }
    @Test
    public void listFieldTestTrue() {
        Assert.assertTrue(DeepComparer.areEqual(new ListField(Arrays.asList( 1, 2, 3 ) ), new ListField(Arrays.asList( 1, 2, 3 ) )));
    }
    @Test
    public void listFieldTestFalse() {
        Assert.assertFalse(DeepComparer.areEqual(new ListField(Arrays.asList( 1, 2, 3 ) ), new ListField(Arrays.asList( 1, 2, 4 ) )));
    }




    class CycleReference {
        private CycleReference reference;
        public CycleReference getReference() {
            return reference;
        }
        public CycleReference(CycleReference reference) {
            this.reference = reference;
        }
    }
    @Test
    public void cycleReferenceTrue() {
        CycleReference expected = new CycleReference(null);
        expected.reference = expected;
        CycleReference actual = new CycleReference(null);
        actual.reference = actual;
        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferenceFalse() {
        CycleReference expected = new CycleReference(null);
        expected.reference = expected;

        CycleReference actual = new CycleReference(new CycleReference(null));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }



    @Test
    public void collectionIterableTrue() {
        Iterable<String> expected = new ArrayList<String>(Arrays.asList("foo", "bar", "baz"));
        Iterable<String> actual = new LinkedList<String>(Arrays.asList("foo", "bar", "baz"));

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void collectionIterableFalse() {
        Iterable<String> expected = new ArrayList<String>(Arrays.asList("foo", "bar", "baz"));
        Iterable<String> actual = new LinkedList<String>(Arrays.asList("foo", "bar"));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void collectionCheckIterableTypesStrictly() {
        Iterable<String> expected = new ArrayList<String>(Arrays.asList("foo", "bar", "baz"));
        Iterable<String> actual = new LinkedList<String>(Arrays.asList("foo", "bar", "baz"));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual, true));
    }




    class Node {
        private Node parent;
        private List<Node> children;

        public Node() {
        }
        public Node(Node parent, List<Node> children) {
            this.parent = parent;
            this.children = children;
        }
        public Node getParent() {
            return parent;
        }
        public void setParent(Node parent) {
            this.parent = parent;
        }
        public List<Node> getChildren() {
            return children;
        }
        public void setChildren(List<Node> children) {
            this.children = children;
        }
    }
    @Test
    public void testIgnoreListItem() {
        Node root1 = new Node(null, Arrays.asList(new Node(), new Node()));
        Node root2 = new Node(null, Arrays.asList(new Node(), new Node()));
        root2.children.get(0).setParent(root2);
        root2.children.get(1).setParent(root2);

        Assert.assertTrue(DeepComparer.areEqual(root2, root1,
                "$root.getChildren.[0]",
                "$root.getChildren.[1]"));
    }
    @Test
    public void testIgnoreGlobPath() {
        Node root1 = new Node(null, Arrays.asList(new Node(), new Node()));
        Node root2 = new Node(null, Arrays.asList(new Node(), new Node()));
        root2.children.get(0).setParent(root2);
        root2.children.get(1).setParent(root2);

        Assert.assertTrue(DeepComparer.areEqual(root2, root1, "$root.getChildren.[*].getParent"));
    }


    class OverriddenEquals {
        private int internalId;
        private int publicId;

        public OverriddenEquals(int internalId, int publicId) {
            this.internalId = internalId;
            this.publicId = publicId;
        }
        public int getInternalId() {
            return internalId;
        }
        public int getPublicId() {
            return publicId;
        }

        // automatically generated by IDEA
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OverriddenEquals that = (OverriddenEquals) o;

            if (publicId != that.publicId) return false;

            return true;
        }
        @Override
        public int hashCode() {
            return publicId;
        }
    }
    @Test
    public void testNotEqualOverriddenEquals() {
        OverriddenEquals expected = new OverriddenEquals(1, 1);
        OverriddenEquals actual = new OverriddenEquals(2, 2);

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void testEqualOverriddenEquals() {
        OverriddenEquals expected = new OverriddenEquals(1, 1);
        OverriddenEquals actual = new OverriddenEquals(2, 1);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }




    class NestedClassField {
        private ListField x;
        public ListField getX() {
            return x;
        }
        NestedClassField(ListField x) {
            this.x = x;
        }
    }
    @Test
    public void nestedClassFieldTestTrue() {
        NestedClassField expected = new NestedClassField(new ListField(Arrays.asList( 1, 2, 3 ) ));
        NestedClassField actual = new NestedClassField(new ListField(Arrays.asList( 1, 2, 3 ) ));
        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void nestedClassFieldTestFalse() {
        NestedClassField expected = new NestedClassField(new ListField(Arrays.asList( 1, 2, 3 ) ));
        NestedClassField actual = new NestedClassField(new ListField(Arrays.asList( 1, 2, 4 ) ));
        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }





    class CycleCrossReference {
        private CycleCrossReference reference1;
        private CycleCrossReference reference2;

        public CycleCrossReference getReference1() {
            return reference1;
        }
        public void setReference1(CycleCrossReference reference1) {
            this.reference1 = reference1;
        }
        public CycleCrossReference getReference2() {
            return reference2;
        }
        public void setReference2(CycleCrossReference reference2) {
            this.reference2 = reference2;
        }
    }
    @Test
    public void cycleCrossReferenceTrue() {
        CycleCrossReference expected = new CycleCrossReference();
        expected.setReference1(expected);

        CycleCrossReference actual = new CycleCrossReference();
        actual.setReference1(actual);

        expected.setReference2(actual);
        actual.setReference2(expected);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleCrossReferenceFalse() {
        CycleCrossReference expected = new CycleCrossReference();
        expected.setReference1(expected);

        CycleCrossReference actual = new CycleCrossReference();
        actual.setReference1(actual);

        expected.setReference2(actual);
        actual.setReference2(new CycleCrossReference());

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }



    @Test
    public void collectionCycleReferenceTrue() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void collectionCycleReferenceFalse() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);
        actual.add(Arrays.asList(1, 2));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void collectionCycleCrossReferenceTrue() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);

        expected.add(actual);
        actual.add(expected);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void collectionCycleCrossReferenceTrue2() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);

        expected.add(expected);
        actual.add(expected);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void collectionCycleCrossReferenceFalse() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);
        actual.add(expected);
        actual.add(expected);

        actual.add(Arrays.asList(actual, 2));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }




    class ComplexClass {
        private int id;
        private ComplexClass reference;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public ComplexClass getReference() {
            return reference;
        }
        public void setReference(ComplexClass reference) {
            this.reference = reference;
        }
        public ComplexClass(int id, ComplexClass reference) {
            this.id = id;
            this.reference = reference;
        }
        public ComplexClass() {
        }
    }
    @Test
    public void testNotEqualPath() {
        ComplexClass expected = new ComplexClass(1, new ComplexClass());
        ComplexClass actual = new ComplexClass(1, null);

        Assert.assertEquals(String.format("$root.getReference; actual is null, expected is \"%s\".", expected.reference),
                DeepComparer.getNotEqualPath(expected, actual));
    }
    @Test
    public void testEqualPath() {
        ComplexClass expected = new ComplexClass(1, new ComplexClass(2, new ComplexClass(3, null)));
        ComplexClass actual = new ComplexClass(1, new ComplexClass(2, new ComplexClass(3, null)));
        Assert.assertEquals(null, DeepComparer.getNotEqualPath(expected, actual));
    }

}
