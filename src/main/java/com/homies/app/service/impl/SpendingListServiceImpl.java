package com.homies.app.service.impl;

import com.homies.app.domain.SpendingList;
import com.homies.app.repository.SpendingListRepository;
import com.homies.app.service.SpendingListService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SpendingList}.
 */
@Service
@Transactional
public class SpendingListServiceImpl implements SpendingListService {

    private final Logger log = LoggerFactory.getLogger(SpendingListServiceImpl.class);

    private final SpendingListRepository spendingListRepository;

    public SpendingListServiceImpl(SpendingListRepository spendingListRepository) {
        this.spendingListRepository = spendingListRepository;
    }

    @Override
    public SpendingList save(SpendingList spendingList) {
        log.debug("Request to save SpendingList : {}", spendingList);
        return spendingListRepository.save(spendingList);
    }

    @Override
    public Optional<SpendingList> partialUpdate(SpendingList spendingList) {
        log.debug("Request to partially update SpendingList : {}", spendingList);

        return spendingListRepository
            .findById(spendingList.getId())
            .map(existingSpendingList -> {
                if (spendingList.getTotal() != null) {
                    existingSpendingList.setTotal(spendingList.getTotal());
                }
                if (spendingList.getNameSpendList() != null) {
                    existingSpendingList.setNameSpendList(spendingList.getNameSpendList());
                }

                return existingSpendingList;
            })
            .map(spendingListRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpendingList> findAll(Pageable pageable) {
        log.debug("Request to get all SpendingLists");
        return spendingListRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SpendingList> findOne(Long id) {
        log.debug("Request to get SpendingList : {}", id);
        return spendingListRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SpendingList : {}", id);
        spendingListRepository.deleteById(id);
    }
}
