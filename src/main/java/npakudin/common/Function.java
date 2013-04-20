package npakudin.common;

public interface Function<F, T> {
    T apply(F item);
}