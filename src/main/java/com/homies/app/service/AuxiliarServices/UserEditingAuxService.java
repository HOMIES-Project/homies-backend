package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.UserData;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.service.dto.UserDataUpdateDTO;
import com.homies.app.service.dto.UserUpdateDTO;
import com.homies.app.web.rest.vm.UserEditingVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEditingAuxService {

    private final Logger log = LoggerFactory.getLogger(UserEditingAuxService.class);

    private final UserService userService;

    private final UserDataService userDataService;

    public UserEditingAuxService(UserService userService,
                                 UserDataService userDataService) {
        this.userService = userService;
        this.userDataService = userDataService;
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
        userData.setBirthDate(userEditingVM.getBirthDate());

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
