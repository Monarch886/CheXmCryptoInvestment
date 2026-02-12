package kz.che.xm.crypto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sorter {

    /**
     * Sort direction.
     */
    private DirectionType direction;

    /**
     * List of property names to sort by, in priority order.
     */
    private List<String> property;

    /**
     * Direction for sorting.
     */
    public enum DirectionType {
        ASC,
        DESC
    }
}
