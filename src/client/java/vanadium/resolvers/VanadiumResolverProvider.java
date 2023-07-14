package vanadium.resolvers;

@FunctionalInterface
public interface VanadiumResolverProvider<T> {
    VanadiumResolver create(T key);
}
