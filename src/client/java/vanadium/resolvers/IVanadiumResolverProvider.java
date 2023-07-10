package vanadium.resolvers;

@FunctionalInterface
public interface IVanadiumResolverProvider<T> {
    IVanadiumResolver create(T key);
}
