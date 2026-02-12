package kz.che.xm.crypto.adapter;

import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.repo.CryptoRepository;
import kz.che.xm.crypto.search.CryptoSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kz.che.xm.crypto.specification.CryptoSpecification.search;
import static kz.che.xm.crypto.specification.SortUtil.convert;

/**
 * Repository adapter (DAO layer facade) for accessing cryptocurrency rate data.
 * <p>
 * This adapter hides Spring Data / JPA specifics from upper layers and provides
 * a single entry point to perform search requests with filtering and sorting.
 * <p>
 * Filtering is delegated to {@link kz.che.xm.crypto.specification.CryptoSpecification},
 * and sorting is converted to Spring Data sort via {@link kz.che.xm.crypto.specification.SortUtil}.
 */
@Service
@RequiredArgsConstructor
public class CryptoRepoAdapter {

    /**
     * Spring Data repository used to execute queries against the database.
     */
    private final CryptoRepository repository;

    /**
     * Returns all rate entities matching the provided search filters and sort configuration.
     *
     * @param search search parameters (date range, currency, sorter)
     * @return list of matched {@link CryptoRateEntity} entities (may be empty)
     */
    @Transactional(readOnly = true)
    public List<CryptoRateEntity> getAllBySearch(CryptoSearch search) {
        return repository.findAll(search(search), convert(search.getSort()));
    }
}
