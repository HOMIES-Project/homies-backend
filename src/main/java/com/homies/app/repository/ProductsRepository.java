package com.homies.app.repository;

import com.homies.app.domain.Products;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Products entity.
 */
@Repository
public interface ProductsRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {
    default Optional<Products> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Products> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Products> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct products from Products products left join fetch products.shoppingList",
        countQuery = "select count(distinct products) from Products products"
    )
    Page<Products> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct products from Products products left join fetch products.shoppingList")
    List<Products> findAllWithToOneRelationships();

    @Query("select products from Products products left join fetch products.shoppingList where products.id =:id")
    Optional<Products> findOneWithToOneRelationships(@Param("id") Long id);
}
