package chromatiq.resolvers;

@FunctionalInterface
public interface ChromatiqResolverProvider<T> {
    ChromatiqResolver create(T key);
}
