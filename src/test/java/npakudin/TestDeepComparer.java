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
    public void nullAndStringAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual(null, "qqq"));
    }
    @Test
    public void stringAndNullAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual("", null));
    }
    @Test
    public void nullsAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(null, null));
    }


    @Test
    public void stringAndIntAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual("1", 1));
    }
    @Test
    public void stringsAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual("Hello 1", "Hello " + new Integer(1)));
    }
    @Test
    public void intsAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(1, 1));
    }
    @Test
    public void intsAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual(1, 2));
    }




    class PublicField {
        public int x;
        public PublicField(int x) {
            this.x = x;
        }
    }
    @Test
    public void publicFieldsAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(new PublicField(1), new PublicField(1)));
    }
    @Test
    public void publicFieldsAreDifferent() {
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
    public void publicPropertiesAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(new PublicProperty(1), new PublicProperty(1)));
    }
    @Test
    public void publicPropertiesAreDifferent() {
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
    public void listFieldsAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(
                new ListField(Arrays.asList( 1, 2, 3 )),
                new ListField(Arrays.asList( 1, 2, 3 ))));
    }
    @Test
    public void listFieldsAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual(
                new ListField(Arrays.asList( 1, 2, 3 )),
                new ListField(Arrays.asList( 1, 2, 4 ))));
    }




    class CycleReference {
        private int id;
        private CycleReference reference;
        public int getId() {
            return id;
        }
        public CycleReference getReference() {
            return reference;
        }
        public CycleReference(int id, CycleReference reference) {
            this.id = id;
            this.reference = reference;
        }
    }
    @Test
    public void cycleReferencesAreEqual() {
        CycleReference expected = new CycleReference(0, null);
        expected.reference = expected;

        CycleReference actual = new CycleReference(0, null);
        actual.reference = actual;

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferencesAreDifferent() {
        CycleReference expected = new CycleReference(0, null);
        expected.reference = expected;

        CycleReference actual = new CycleReference(0, new CycleReference(0, null));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferencesErrorMessage() {
        CycleReference expected = new CycleReference(1, new CycleReference(0, null));
        CycleReference actual = new CycleReference(1, null);

        Assert.assertEquals(String.format("$root.getReference; actual is null, expected is \"%s\".", expected.reference),
                DeepComparer.getNotEqualPath(expected, actual));
    }
    @Test
    public void cycleReferencesVeryDeepDifference() {
        CycleReference expected = null;
        CycleReference actual = null;

        for (int i=0; i<3; i++) {
            expected = new CycleReference(i, expected);
            actual = new CycleReference(i, actual);
        }

        Assert.assertEquals(null, DeepComparer.getNotEqualPath(expected, actual));
    }



    @Test
    public void iterablesAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual(
                new ArrayList<String>(Arrays.asList("foo", "bar", "baz")),
                new LinkedList<String>(Arrays.asList("foo", "bar"))));
    }
    @Test
    public void iterablesOfDifferentTypesAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(
                new ArrayList<String>(Arrays.asList("foo", "bar", "baz")),
                new LinkedList<String>(Arrays.asList("foo", "bar", "baz"))));
    }
    @Test
    public void iterablesOfDifferentTypesAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual(
                new ArrayList<String>(Arrays.asList("foo", "bar", "baz")),
                new LinkedList<String>(Arrays.asList("foo", "bar", "baz")),
                true));
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
    public void differentNodesAreEqualWithExcludingSomePaths() {
        Node root1 = new Node(null, Arrays.asList(new Node(), new Node()));
        Node root2 = new Node(null, Arrays.asList(new Node(), new Node()));
        root2.children.get(0).setParent(root2);
        root2.children.get(1).setParent(root2);

        Assert.assertTrue(DeepComparer.areEqual(root2, root1,
                "$root.getChildren.[0]",
                "$root.getChildren.[1]"));
    }
    @Test
    public void differentNodesAreEqualWithExcludingSomeRegexPaths() {
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
    public void overriddenEqualsAreDifferent() {
        Assert.assertFalse(DeepComparer.areEqual(new OverriddenEquals(1, 1), new OverriddenEquals(2, 2)));
    }
    @Test
    public void overriddenEqualsAreEqual() {
        Assert.assertTrue(DeepComparer.areEqual(new OverriddenEquals(1, 1), new OverriddenEquals(2, 1)));
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
    public void cycleCrossReferencesAreEqual() {
        CycleCrossReference expected = new CycleCrossReference();
        expected.setReference1(expected);

        CycleCrossReference actual = new CycleCrossReference();
        actual.setReference1(actual);

        expected.setReference2(actual);
        actual.setReference2(expected);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleCrossReferencesAreDifferent() {
        CycleCrossReference expected = new CycleCrossReference();
        expected.setReference1(expected);

        CycleCrossReference actual = new CycleCrossReference();
        actual.setReference1(actual);

        expected.setReference2(actual);
        actual.setReference2(new CycleCrossReference());

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }



    @Test
    public void cycleReferenceCollectionsAreEqual() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferenceCollectionsAreDifferent() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);
        actual.add(Arrays.asList(1, 2));

        Assert.assertFalse(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferenceAndCrossReferenceCollectionsAreEqual() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);

        expected.add(actual);
        actual.add(expected);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferenceAndReferenceCollectionsAreEqual() {
        List<Object> expected = new ArrayList<Object>();
        expected.add(expected);

        List<Object> actual = new ArrayList<Object>();
        actual.add(actual);

        expected.add(expected);
        actual.add(expected);

        Assert.assertTrue(DeepComparer.areEqual(expected, actual));
    }
    @Test
    public void cycleReferenceCollectionsAreDifferent2() {
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
}
