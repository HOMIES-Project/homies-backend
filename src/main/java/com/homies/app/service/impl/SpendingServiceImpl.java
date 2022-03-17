package com.homies.app.service.impl;

import com.homies.app.domain.Spending;
import com.homies.app.repository.SpendingRepository;
import com.homies.app.service.SpendingService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Spending}.
 */
@Service
@Transactional
public class SpendingServiceImpl implements SpendingService {

    private final Logger log = LoggerFactory.getLogger(SpendingServiceImpl.class);

    private final SpendingRepository spendingRepository;

    public SpendingServiceImpl(SpendingRepository spendingRepository) {
        this.spendingRepository = spendingRepository;
    }

    @Override
    public Spending save(Spending spending) {
        log.debug("Request to save Spending : {}", spending);
        return spendingRepository.save(spending);
    }

    @Override
    public Optional<Spending> partialUpdate(Spending spending) {
        log.debug("Request to partially update Spending : {}", spending);

        return spendingRepository
            .findById(spending.getId())
            .map(existingSpending -> {
                if (spending.getPayer() != null) {
                    existingSpending.setPayer(spending.getPayer());
                }
                if (spending.getNameCost() != null) {
                    existingSpending.setNameCost(spending.getNameCost());
                }
                if (spending.getCost() != null) {
                    existingSpending.setCost(spending.getCost());
                }
                if (spending.getPhoto() != null) {
                    existingSpending.setPhoto(spending.getPhoto());
                }
                if (spending.getPhotoContentType() != null) {
                    existingSpending.setPhotoContentType(spending.getPhotoContentType());
                }
                if (spending.getDescripcion() != null) {
                    existingSpending.setDescripcion(spending.getDescripcion());
                }
                if (spending.getPaid() != null) {
                    existingSpending.setPaid(spending.getPaid());
                }
                if (spending.getPending() != null) {
                    existingSpending.setPending(spending.getPending());
                }
                if (spending.getFinished() != null) {
                    existingSpending.setFinished(spending.getFinished());
                }

                return existingSpending;
            })
            .map(spendingRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Spending> findAll(Pageable pageable) {
        log.debug("Request to get all Spendings");
        return spendingRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Spending> findOne(Long id) {
        log.debug("Request to get Spending : {}", id);
        return spendingRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Spending : {}", id);
        spendingRepository.deleteById(id);
    }
}
