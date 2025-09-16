package co.com.pragma.model.pageable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DomainPageable {

    private final int pageNumber;
    private final int pageSize;

}
