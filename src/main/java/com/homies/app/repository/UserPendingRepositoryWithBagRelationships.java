package com.homies.app.repository;

import com.homies.app.domain.UserPending;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserPendingRepositoryWithBagRelationships {
    Optional<UserPending> fetchBagRelationships(Optional<UserPending> userPending);

    List<UserPending> fetchBagRelationships(List<UserPending> userPendings);

    Page<UserPending> fetchBagRelationships(Page<UserPending> userPendings);
}
