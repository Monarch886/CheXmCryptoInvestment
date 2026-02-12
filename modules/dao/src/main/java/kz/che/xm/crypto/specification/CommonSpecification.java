package kz.che.xm.crypto.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * Common helper methods for building Spring Data JPA {@link Specification Specifications}.
 * <p>
 * The methods in this class are intentionally generic and reusable across different entities.
 * They follow a "null-safe" approach: if a filter value is {@code null}, the method returns
 * a no-op predicate ({@code conjunction}) so it can be freely combined with other specifications.
 */
public class CommonSpecification<T> {

    /**
     * Builds a "between" specification for a comparable attribute.
     * <p>
     * Behavior:
     * <ul>
     *   <li>If both {@code from} and {@code to} are {@code null} - returns a no-op filter.</li>
     *   <li>If only {@code from} is {@code null} - uses {@code &lt;= to}.</li>
     *   <li>If only {@code to} is {@code null} - uses {@code &gt;= from}.</li>
     *   <li>If both are present - uses {@code between(from, to)}.</li>
     * </ul>
     *
     * @param attribute entity attribute name
     * @param from      lower bound (inclusive), may be {@code null}
     * @param to        upper bound (inclusive), may be {@code null}
     * @param <T>       entity type
     * @param <Y>       attribute type (must be comparable)
     * @return Spring Data JPA specification for the requested bounds, or a no-op specification
     */
    public static <T, Y extends Comparable<Y>> Specification<T> betweenFilter(String attribute, Y from, Y to) {
        if (from == null && to == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        if (from == null) {
            return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attribute), to);
        }
        if (to == null) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), from);
        }
        return (root, query, cb) -> cb.between(root.get(attribute), from, to);
    }

    /**
     * Builds an equality specification for an attribute.
     * <p>
     * If {@code value} is {@code null}, returns a no-op filter to simplify composition.
     *
     * @param attribute entity attribute name
     * @param value     expected value, may be {@code null}
     * @param <T>       entity type
     * @return equality specification or a no-op specification when value is {@code null}
     */
    public static <T> Specification<T> equalFilter(String attribute, Object value) {
        if (value == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get(attribute), value);
    }
}