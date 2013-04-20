package npakudin;

import npakudin.common.Action2;

public class Main {

    public static void main(String[] args) {
	    int[] a = new int[] { 1, 2, 3 };
        int[] b = new int[] { 1, 2, 3 };

        DeepComparer.setAssertAction(new Action2<Boolean, String>() {
            @Override
            public void apply(Boolean aBoolean, String s) {
                System.out.println(String.format("%s, - %s", aBoolean, s));
            }
        });

        DeepComparer.AssertAreEqual(a, b);
    }
}
