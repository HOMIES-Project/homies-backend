package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.service.*;
import com.homies.app.web.rest.TaskResource;
import com.homies.app.web.rest.errors.General.IncorrectParameters;
import com.homies.app.web.rest.errors.User.UserDoesNotExistInGroup;
import com.homies.app.web.rest.vm.AddUserToTaskVM;
import com.homies.app.web.rest.vm.UpdateProductVM;
import com.homies.app.web.rest.vm.UpdateTaskVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ManageProductAuxService {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);
    @Autowired
    private ProductsService productsService;
    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDataQueryService userDataQueryService;
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private GroupService groupService;

    public ManageProductAuxService(ProductsService productsService,
                                   ShoppingListService shoppingListService,
                                   UserService userService,
                                   UserDataQueryService userDataQueryService,
                                   UserDataService userDataService,
                                   GroupService groupService) {
        this.productsService = productsService;
        this.shoppingListService = shoppingListService;
        this.userService = userService;
        this.userDataQueryService = userDataQueryService;
        this.userDataService = userDataService;
        this.groupService = groupService;
    }

    private Optional<Products> products;

    private Optional<ShoppingList> shoppingList;

    private Optional<Group> group;

    private Optional<UserData> userData;


    public Optional<Products> updateProduct(UpdateProductVM updateProductVM){
        if(managerUpdateOfTheProduct(updateProductVM).isPresent()){
            products = productsService.findOne(updateProductVM.getIdProduct());
            shoppingList = shoppingListService.findOne(updateProductVM.getIdGroup());
            group = groupService.findOne(updateProductVM.getIdGroup());
            AtomicBoolean userExist = new AtomicBoolean(false);

            if(group.get().getUserData() != null){
                group.get().getUserData().forEach(userData1 -> {
                    if(userData1.getId().equals(userData.get().getId())){
                        userExist.set(true);
                    }
                });
                if(userExist.get()){
                    if(!updateProductVM.getName().equals(products.get().getName())){
                        products.get().setName(updateProductVM.getName());
                    }

                    if(!products.get().getUnits().equals(updateProductVM.getUnits())){
                        products.get().setUnits(updateProductVM.getUnits());
                    }

                    if(!updateProductVM.getTypeUnit().equals(products.get().getTypeUnit())){
                        products.get().setTypeUnit(updateProductVM.getTypeUnit());
                    }

                    productsService.save(products.get());

                }else{
                    throw new UserDoesNotExistInGroup();
                }
            }
            return productsService.findOne(updateProductVM.getIdProduct());
        }
        throw new IncorrectParameters();
    }

    private Optional<Products> managerUpdateOfTheProduct(UpdateProductVM updateProductVM){

        products = productsService.findOne(updateProductVM.getIdProduct());
        shoppingList = shoppingListService.findOne(updateProductVM.getIdGroup());
        Optional<User> user = userService.getUser(updateProductVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || products.isEmpty() || shoppingList.isEmpty()){
            return Optional.empty();
        }
        return products;
    }

}
