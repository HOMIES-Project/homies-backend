package com.homies.app.web.rest.auxiliary;

import com.homies.app.domain.User;
import com.homies.app.domain.UserData;
import com.homies.app.service.UserDataQueryService;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.service.dto.UserDataUpdateDTO;
import com.homies.app.service.dto.UserUpdateDTO;
import com.homies.app.web.rest.vm.UserEditingVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserEditingAux {

    private final Logger log = LoggerFactory.getLogger(UserEditingAux.class);

    private final UserService userService;

    private final UserDataQueryService userDataQueryService;

    private final UserDataService userDataService;

    //private final UserEditingVM userEditingVM;

    public UserEditingAux(UserService userService,
                          UserDataQueryService userDataQueryService,
                          UserDataService userDataService) {
        this.userService = userService;
        this.userDataQueryService = userDataQueryService;
        this.userDataService = userDataService;
        //this.userEditingVM = userEditingVM;
    }

    public UserData updateUser(UserEditingVM userEditingVM) {
        UserUpdateDTO user = new UserUpdateDTO();
        UserDataUpdateDTO userData = new UserDataUpdateDTO();
        Long id = userEditingVM.getId();

        user.setId(id);
        user.setLogin(userEditingVM.getLogin());
        user.setPassword(userEditingVM.getPassword());
        user.setFirstName(userEditingVM.getFirstName());
        user.setLastName(userEditingVM.getLastName());
        user.setEmail(userEditingVM.getEmail());
        user.setLangKey(userEditingVM.getLangKey());

        userData.setId(id);
        userData.setPhoto(userEditingVM.getPhoto());
        userData.setPhotoContentType(userEditingVM.getPhotoContentType());
        userData.setPhone(userEditingVM.getPhone());

        if (editUser(user) & editUserData(userData))
            return userDataService.findOne(userEditingVM.getId()).get();
        return null;
    }

    private Boolean editUser(UserUpdateDTO user) {
        return userService.updateUser(user);
    }

    private Boolean editUserData(UserDataUpdateDTO userData) {
        Optional<UserData> newUserData = userDataService.findOne(userData.getId());
        if (newUserData.isPresent()) {
            newUserData.get().setPhoto(userData.getPhoto());
            newUserData.get().setPhotoContentType(userData.getPhotoContentType());
            newUserData.get().setPhone(userData.getPhone());
            newUserData.get().setBirthDate(userData.getBirthDate());
            userDataService.save(newUserData.get());
            log.debug("Changed Information for UserData: {}", newUserData);
            return true;
        }
        return false;
    }

}
