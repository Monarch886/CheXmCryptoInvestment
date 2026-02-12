package kz.che.xm.crypto.specification;

import kz.che.xm.crypto.common.Sorter;
import org.springframework.data.domain.Sort;

import java.util.List;

import static java.util.List.of;
import static java.util.Objects.nonNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.fromString;
import static org.springframework.data.domain.Sort.by;

public final class SortUtil {
    private static final List<String> DEFAULT_PROPERTY = of("id");
    private static final Sort.Direction DEFAULT_DIRECTION = ASC;

    private SortUtil() {
    }

    public static Sort convert(Sorter sort) {
        Sort.Direction direction = DEFAULT_DIRECTION;
        List<String> property = DEFAULT_PROPERTY;
        if (sort != null) {
            direction = getDirection(sort);
            property = getProperty(sort);
        }
        return by(direction, property.toArray(new String[0]));
    }

    private static Sort.Direction getDirection(Sorter sort) {
        if (nonNull(sort.getDirection())) {
            return fromString(sort.getDirection().name());
        }
        return DEFAULT_DIRECTION;
    }

    private static List<String> getProperty(Sorter sort) {
        if (nonNull(sort.getProperty()) && !sort.getProperty().isEmpty()) {
            return sort.getProperty();
        }
        return DEFAULT_PROPERTY;
    }
}
