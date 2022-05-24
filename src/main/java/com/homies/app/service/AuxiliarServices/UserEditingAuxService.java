package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.UserData;
import com.homies.app.service.UserDataService;
import com.homies.app.service.UserService;
import com.homies.app.service.dto.UserDataUpdateDTO;
import com.homies.app.service.dto.UserUpdateDTO;
import com.homies.app.web.rest.vm.UserEditingVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEditingAuxService {
    private final Logger log = LoggerFactory.getLogger(UserEditingAuxService.class);
    @Autowired
    private final UserService userService;
    @Autowired
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
        if ( userEditingVM.getLogin() != null ) user.setLogin(userEditingVM.getLogin());
        //if ( userEditingVM.getPassword() != null ) user.setPassword(userEditingVM.getPassword());
        if ( userEditingVM.getFirstName() != null ) user.setFirstName(userEditingVM.getFirstName());
        if ( userEditingVM.getLastName() != null ) user.setLastName(userEditingVM.getLastName());
        if ( userEditingVM.getEmail() != null ) user.setEmail(userEditingVM.getEmail());
        if ( userEditingVM.getLangKey() != null ) user.setLangKey(userEditingVM.getLangKey());

        userData.setId(id);
        if (userEditingVM.getPhoto() != null) userData.setPhoto(userEditingVM.getPhoto());
        if (userEditingVM.getPhotoContentType() != null) userData.setPhotoContentType(userEditingVM.getPhotoContentType());
        if (userEditingVM.getPhone() != null) userData.setPhone(userEditingVM.getPhone());
        if (userEditingVM.getBirthDate() != null) userData.setBirthDate(userEditingVM.getBirthDate());

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
            if (userData.getPhoto() != null) newUserData.get().setPhoto(userData.getPhoto());
            if (userData.getPhotoContentType() != null) newUserData.get().setPhotoContentType(userData.getPhotoContentType());
            if (userData.getPhone() != null) newUserData.get().setPhone(userData.getPhone());
            if (userData.getBirthDate() != null) newUserData.get().setBirthDate(userData.getBirthDate());
            userDataService.save(newUserData.get());
            log.warn("@@@ Homies::Changed Information for UserData: {}", newUserData.toString());
            return true;
        }
        return false;
    }

}
