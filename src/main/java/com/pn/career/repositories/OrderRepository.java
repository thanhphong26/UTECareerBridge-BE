package com.pn.career.repositories;

import com.pn.career.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer>{
}
