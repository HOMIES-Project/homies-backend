package com.homies.app.service.impl;

import com.homies.app.domain.Group;
import com.homies.app.repository.GroupRepository;
import com.homies.app.repository.TaskListRepository;
import com.homies.app.service.GroupService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Group}.
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupRepository groupRepository;

    private final TaskListRepository taskListRepository;

    public GroupServiceImpl(GroupRepository groupRepository, TaskListRepository taskListRepository) {
        this.groupRepository = groupRepository;
        this.taskListRepository = taskListRepository;
    }

    @Override
    public Group save(Group group) {
        log.debug("Request to save Group : {}", group);
        Long taskListId = group.getTaskList().getId();
        taskListRepository.findById(taskListId).ifPresent(group::taskList);
        return groupRepository.saveAndFlush(group);
    }

    @Override
    public Optional<Group> partialUpdate(Group group) {
        log.debug("Request to partially update Group : {}", group);

        return groupRepository
            .findById(group.getId())
            .map(existingGroup -> {
                if (group.getGroupKey() != null) {
                    existingGroup.setGroupKey(group.getGroupKey());
                }
                if (group.getGroupName() != null) {
                    existingGroup.setGroupName(group.getGroupName());
                }
                if (group.getGroupRelationName() != null) {
                    existingGroup.setGroupRelationName(group.getGroupRelationName());
                }
                if (group.getAddGroupDate() != null) {
                    existingGroup.setAddGroupDate(group.getAddGroupDate());
                }

                return existingGroup;
            })
            .map(groupRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Group> findAll(Pageable pageable) {
        log.debug("Request to get all Groups");
        return groupRepository.findAll(pageable);
    }

    public Page<Group> findAllWithEagerRelationships(Pageable pageable) {
        return groupRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     *  Get all the groups where SpendingList is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Group> findAllWhereSpendingListIsNull() {
        log.debug("Request to get all groups where SpendingList is null");
        return StreamSupport
            .stream(groupRepository.findAll().spliterator(), false)
            .filter(group -> group.getSpendingList() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the groups where ShoppingList is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Group> findAllWhereShoppingListIsNull() {
        log.debug("Request to get all groups where ShoppingList is null");
        return StreamSupport
            .stream(groupRepository.findAll().spliterator(), false)
            .filter(group -> group.getShoppingList() == null)
            .collect(Collectors.toList());
    }

    /**
     *  Get all the groups where SettingsList is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Group> findAllWhereSettingsListIsNull() {
        log.debug("Request to get all groups where SettingsList is null");
        return StreamSupport
            .stream(groupRepository.findAll().spliterator(), false)
            .filter(group -> group.getSettingsList() == null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findOne(Long id) {
        log.debug("Request to get Group : {}", id);
        return groupRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Optional<Group> findOne(String groupName) {
        return groupRepository.findByGroupName(groupName);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Group : {}", id);
        groupRepository.deleteById(id);
    }
}
