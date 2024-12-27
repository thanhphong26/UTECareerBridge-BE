package com.pn.career.repositories;

import com.pn.career.models.Cart;
import com.pn.career.models.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>{
    Optional<Cart> findByEmployer(Employer employer);
}
