package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.service.*;
import com.homies.app.web.rest.GroupResource;
import com.homies.app.web.rest.errors.Group.GroupNotExistException;
import com.homies.app.web.rest.errors.ShoppingList.ShoppingListDoesNotExist;
import com.homies.app.web.rest.errors.User.UserDoesNotExist;
import com.homies.app.web.rest.vm.AddProductVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class CreateProductAuxService {
    @Autowired
    private final UserDataService userDataService;
    @Autowired
    private final GroupService groupService;
    @Autowired
    private final ProductsService productsService;
    @Autowired
    private final ShoppingListService shoppingListService;

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    public CreateProductAuxService(UserDataService userDataService,
                                   GroupService groupService,
                                   ProductsService productsService,
                                   ShoppingListService shoppingListService) {
        this.userDataService = userDataService;
        this.groupService = groupService;
        this.productsService = productsService;
        this.shoppingListService = shoppingListService;
    }

    public Products createNewProduct(AddProductVM addProductVM) {
        if (groupExist(addProductVM.getIdGroup()) == null)
            throw new GroupNotExistException();

        if (userExist(addProductVM.getIdUserData()) == null)
            throw new UserDoesNotExist();

        if (shoppingListExist(addProductVM.getIdGroup()) == null)
            throw new ShoppingListDoesNotExist();


        log.warn("creating product");
        Products newproducts = new Products();
        newproducts.setName(addProductVM.getNameProduct());
        newproducts.setUnits(addProductVM.getUnits());

        //Product add ShoppingList
        ShoppingList shoppingList = shoppingListExist(addProductVM.getIdGroup());
        newproducts.setShoppingList(shoppingList);
        shoppingListService.save(shoppingList);

        //New task created
        log.warn("Created Product: " + newproducts);
        productsService.save(newproducts);

        return newproducts;
    }

    @Transactional(readOnly = true)
    private UserData userExist(Long id) {
        Optional<UserData> user = userDataService.findOne(id);
        log.warn(user.toString());
        return user.orElse(null);
    }

    @Transactional(readOnly = true)
    private Group groupExist(Long id) {
        Optional<Group> group = groupService.findOne(id);
        log.warn("" + id);
        return group.orElse(null);
    }

    @Transactional(readOnly = true)
    private ShoppingList shoppingListExist(Long id) {
        Optional<ShoppingList> shoppingList = shoppingListService.findOne(id);
        log.warn(shoppingList.toString());
        return shoppingList.orElse(null);
    }
}
