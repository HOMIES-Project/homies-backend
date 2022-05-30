package com.homies.app.repository;

import com.homies.app.domain.UserData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserDataRepositoryWithBagRelationships {
    Optional<UserData> fetchBagRelationships(Optional<UserData> userData);

    List<UserData> fetchBagRelationships(List<UserData> userData);

    Page<UserData> fetchBagRelationships(Page<UserData> userData);
}
