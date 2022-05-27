package com.homies.app.service.AuxiliarServices;

import com.homies.app.domain.*;
import com.homies.app.security.SecurityUtils;
import com.homies.app.service.*;
import com.homies.app.web.rest.TaskResource;
import com.homies.app.web.rest.errors.General.IncorrectParameters;
import com.homies.app.web.rest.errors.Products.ProductDoesNotExist;
import com.homies.app.web.rest.errors.User.UserDoesNotExistInGroup;
import com.homies.app.web.rest.vm.UpdateProductVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private Optional<Products> product;

    private Optional<ShoppingList> shoppingList;

    private Optional<Group> group;

    private Optional<UserData> userData;


    public Optional<Products> updateProduct(UpdateProductVM updateProductVM) {
        if (managerUpdateOfTheProduct(updateProductVM).isPresent()) {
            product = productsService.findOne(updateProductVM.getIdProduct());
            shoppingList = shoppingListService.findOne(updateProductVM.getIdGroup());
            group = groupService.findOne(updateProductVM.getIdGroup());
            AtomicBoolean userExist = new AtomicBoolean(false);

            if (group.get().getUserData() != null) {
                group.get().getUserData().forEach(userData1 -> {
                    if (userData1.getId().equals(userData.get().getId())) {
                        userExist.set(true);
                    }
                });
                if (userExist.get()) {
                    if (!updateProductVM.getName().equals(product.get().getName())) {
                        product.get().setName(updateProductVM.getName());
                    }

                    if (!product.get().getUnits().equals(updateProductVM.getUnits())) {
                        product.get().setUnits(updateProductVM.getUnits());
                    }

                    if (!updateProductVM.getTypeUnit().equals(product.get().getTypeUnit())) {
                        product.get().setTypeUnit(updateProductVM.getTypeUnit());
                    }

                    productsService.save(product.get());

                } else {
                    throw new UserDoesNotExistInGroup();
                }
            }
            return productsService.findOne(updateProductVM.getIdProduct());
        }
        throw new IncorrectParameters();
    }

    public Optional<Products> updateProductCancel(UpdateProductVM updateProductVM) {
        if (managerUpdateOfTheProduct(updateProductVM).isPresent()) {
            product = productsService.findOne(updateProductVM.getIdProduct());
            group = groupService.findOne(updateProductVM.getIdGroup());
            Optional<User> user = userService.getUser(updateProductVM.getLogin());
            userData = userDataService.findOne(user.get().getId());

            AtomicBoolean userExist = new AtomicBoolean(false);
            if (group.get().getUserData() != null) {
                group.get().getUserData().forEach(userData1 -> {
                    if (userData1.getId().equals(userData.get().getId())) {
                        userExist.set(true);
                    }
                });
                if (userExist.get()) {
                    product.get().setPurchased(updateProductVM.isPurchased());
                    productsService.save(product.get());
                } else {
                    throw new UserDoesNotExistInGroup();
                }
            }
            return productsService.findOne(updateProductVM.getIdProduct());
        }
        throw new IncorrectParameters();
    }

    public boolean deleteProducts(Long id) {
        //If user login exist in the group
        try {
            String login = SecurityUtils.getCurrentUserLogin().get();
            userData = userDataQueryService.getByUser_Login(login);
            group = groupService.findOne(productsService.findOne(id).get()
                    .getShoppingList()
                    .getId());

            if (group.get().getUserData().contains(userData.get())) {
                shoppingList = shoppingListService.findOne(productsService.findOne(id).get().getShoppingList().getId());
                shoppingList.get().removeProducts(productsService.findOne(id).get());
                shoppingListService.save(shoppingList.get());

                product = productsService.findOne(id);
                product.get().setShoppingList(null);
                productsService.save(product.get());

                productsService.delete(id);
                return true;
            } else {
                throw new UserDoesNotExistInGroup();
            }
        } catch (Exception e) {
            throw new ProductDoesNotExist();
        }

    }

    private Optional<Products> managerUpdateOfTheProduct(UpdateProductVM updateProductVM) {

        product = productsService.findOne(updateProductVM.getIdProduct());
        shoppingList = shoppingListService.findOne(updateProductVM.getIdGroup());
        Optional<User> user = userService.getUser(updateProductVM.getLogin());
        userData = userDataService.findOne(user.get().getId());

        if (userData.isEmpty() || product.isEmpty() || shoppingList.isEmpty()) {
            return Optional.empty();
        }
        return product;
    }

}
