package com.pn.career.services;

import com.pn.career.models.EmployerPackage;
import com.pn.career.models.EmployerPackageId;
import com.pn.career.models.Order;
import com.pn.career.models.OrderDetail;
import com.pn.career.models.Package;
import com.pn.career.repositories.EmployerPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployerPackageService implements IEmployerPackageService{
    private final EmployerPackageRepository employerPackageRepository;
    @Override
    public void createEmployerPackage(Order order) {
        for(OrderDetail orderDetail : order.getOrderDetails()){
            EmployerPackage employerPackage = EmployerPackage.builder()
                    .id(new EmployerPackageId(order.getEmployer().getUserId(), orderDetail.getJobPackage().getPackageId()))
                    .employer(order.getEmployer())
                    .jobPackage(orderDetail.getJobPackage())
                    .amount(orderDetail.getAmount())
                    .expiredAt(calculateExpirationDate(orderDetail.getJobPackage()))
                    .build();
            employerPackageRepository.save(employerPackage);
        }
    }
    private LocalDateTime calculateExpirationDate(Package jobPackage) {
        // Giả sử rằng mỗi gói có một trường duration (số ngày có hiệu lực)
        return LocalDateTime.now().plusWeeks(jobPackage.getDuration());
    }
}
