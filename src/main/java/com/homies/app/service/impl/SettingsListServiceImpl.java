package com.homies.app.service.impl;

import com.homies.app.domain.SettingsList;
import com.homies.app.repository.GroupRepository;
import com.homies.app.repository.SettingsListRepository;
import com.homies.app.service.SettingsListService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SettingsList}.
 */
@Service
@Transactional
public class SettingsListServiceImpl implements SettingsListService {

    private final Logger log = LoggerFactory.getLogger(SettingsListServiceImpl.class);

    private final SettingsListRepository settingsListRepository;

    private final GroupRepository groupRepository;

    public SettingsListServiceImpl(SettingsListRepository settingsListRepository, GroupRepository groupRepository) {
        this.settingsListRepository = settingsListRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public SettingsList save(SettingsList settingsList) {
        log.debug("Request to save SettingsList : {}", settingsList);
        Long groupId = settingsList.getGroup().getId();
        groupRepository.findById(groupId).ifPresent(settingsList::group);
        return settingsListRepository.save(settingsList);
    }

    @Override
    public Optional<SettingsList> partialUpdate(SettingsList settingsList) {
        log.debug("Request to partially update SettingsList : {}", settingsList);

        return settingsListRepository
            .findById(settingsList.getId())
            .map(existingSettingsList -> {
                if (settingsList.getSettingOne() != null) {
                    existingSettingsList.setSettingOne(settingsList.getSettingOne());
                }
                if (settingsList.getSettingTwo() != null) {
                    existingSettingsList.setSettingTwo(settingsList.getSettingTwo());
                }
                if (settingsList.getSettingThree() != null) {
                    existingSettingsList.setSettingThree(settingsList.getSettingThree());
                }
                if (settingsList.getSettingFour() != null) {
                    existingSettingsList.setSettingFour(settingsList.getSettingFour());
                }
                if (settingsList.getSettingFive() != null) {
                    existingSettingsList.setSettingFive(settingsList.getSettingFive());
                }
                if (settingsList.getSettingSix() != null) {
                    existingSettingsList.setSettingSix(settingsList.getSettingSix());
                }
                if (settingsList.getSettingSeven() != null) {
                    existingSettingsList.setSettingSeven(settingsList.getSettingSeven());
                }

                return existingSettingsList;
            })
            .map(settingsListRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SettingsList> findAll(Pageable pageable) {
        log.debug("Request to get all SettingsLists");
        return settingsListRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SettingsList> findOne(Long id) {
        log.debug("Request to get SettingsList : {}", id);
        return settingsListRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SettingsList : {}", id);
        settingsListRepository.deleteById(id);
    }
}
