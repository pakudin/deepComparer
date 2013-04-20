package npakudin.common;

public interface Predicate<T> {
    boolean apply(T item);
}