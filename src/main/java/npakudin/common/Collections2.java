package npakudin.common;

import java.util.ArrayList;
import java.util.Collection;

public class Collections2 {

    public static <T> boolean any(Collection<T> collection, Predicate<? super T> predicate) {
        for (T item : collection) {
            if (predicate.apply(item))
                return true;
        }
        return false;
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<T> predicate) {
        ArrayList<T> list = new ArrayList<T>();
        for(T t : collection){
            if(predicate.apply(t))
                list.add(t);
        }
        return list;
    }

    public static <F, T> Collection<T> transform(Collection<F> fromCollection,
                                                 Function<? super F, T> function) {
        ArrayList<T> list = new ArrayList<T>();
        for(F f : fromCollection){
            list.add(function.apply(f));
        }
        return list;
    }
}
