package online.armanportfolio.sms.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A generic paginated envelope returned by list endpoints, so the frontend
 * always has the metadata it needs to render pagination controls without
 * a second round-trip.
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
