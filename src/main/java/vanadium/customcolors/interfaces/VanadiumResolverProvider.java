package vanadium.customcolors.interfaces;

@FunctionalInterface
public interface VanadiumResolverProvider<T> {
    VanadiumResolver create(T key);
}
