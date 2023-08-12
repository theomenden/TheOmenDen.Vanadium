package vanadium.customcolors.interfaces;

public interface VanadiumResolverProvider<T> {
    VanadiumResolver create(T key);
}
