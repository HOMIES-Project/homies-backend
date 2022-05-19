package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.repository.UserDataRepository;
import com.homies.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Service
@Transactional
public class CreateUserDataForUserAuxService {

    private final Logger log = LoggerFactory.getLogger(CreateUserDataForUserAuxService.class);
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserDataRepository userDataRepository;

    public CreateUserDataForUserAuxService(UserRepository userRepository, UserDataRepository userDataRepository) {
        this.userRepository = userRepository;
        this.userDataRepository = userDataRepository;
    }

    public boolean create(User user) {
        log.warn("User: " + user.getLogin() + ", Id: " + user.getId() + ", User: " + user);

        UserData newUserData = new UserData();
        newUserData.setId(null);
        newUserData.setPhoto(null);
        newUserData.setPhotoContentType(null);
        newUserData.setPhone(null);
        newUserData.setPremium(false);
        newUserData.setBirthDate(null);
        newUserData.setAddDate(LocalDate.now());
        newUserData.setUser(user);


        HashSet groupSet = new HashSet<Group>();
        newUserData.setGroups(groupSet);

        HashSet ProductsSet = new HashSet<Products>();
        newUserData.setProductCreateds(ProductsSet);

        HashSet TaskSet = new HashSet<Task>();
        newUserData.setTaskAsigneds(TaskSet);


        userDataRepository.save(newUserData);
        log.warn("newUserData is save: " + newUserData.getUser());
        return true;

    }


    public boolean createUserData(Long id) {
        Optional<User> user = userRepository.findById(id);
        log.warn("User: " + user.get().getLogin() + ", Id: " + user.get().getId() + ", User: " + user.get());
        if (user.isPresent()) {
            UserData newUserData = new UserData();
            newUserData.setId(null);
            newUserData.setPhoto(null);
            newUserData.setPhotoContentType(null);
            newUserData.setPhone(null);
            newUserData.setPremium(false);
            newUserData.setBirthDate(null);
            newUserData.setAddDate(LocalDate.now());
            newUserData.setUser(user.get());
            userDataRepository.save(newUserData);
            log.warn("newUserData is save: " + newUserData.getId());
            return true;
        }
        return false;
    }

    public boolean safeNewUserData(UserData userData) {
        Optional<UserData> user = userDataRepository.findById(userData.getId());

        if (user.isPresent()) {
            user.get().setPhoto(userData.getPhoto());
            user.get().setPhotoContentType(userData.getPhotoContentType());
            user.get().setPhone(userData.getPhone());
            user.get().setPremium(userData.getPremium());
            user.get().setBirthDate(LocalDate.parse(userData.getBirthDate().toString()));
            userDataRepository.saveAndFlush(user.get());
            return true;
        }

        return false;
    }

}
