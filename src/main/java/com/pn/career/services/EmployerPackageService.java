package com.pn.career.services;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.OverUsageException;
import com.pn.career.models.EmployerPackage;
import com.pn.career.models.EmployerPackageId;
import com.pn.career.models.Order;
import com.pn.career.models.OrderDetail;
import com.pn.career.models.Package;
import com.pn.career.repositories.EmployerPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
                    .amount(orderDetail.getJobPackage().getAmount())
                    .expiredAt(calculateExpirationDate(orderDetail.getJobPackage()))
                    .build();
            employerPackageRepository.save(employerPackage);
        }
    }
    @Override
    public void updateEmployerPackage(Integer employerId, Integer packageId) {
        EmployerPackageId employerPackageId = new EmployerPackageId(employerId, packageId);
        EmployerPackage employerPackage = employerPackageRepository.findById(employerPackageId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy gói tương ứng"));
        if(employerPackage.getAmount()<0){
            throw new OverUsageException("Gói đã hết lượt sử dụng");
        }
        employerPackage.setAmount(employerPackage.getAmount()-1);
        employerPackageRepository.save(employerPackage);
    }

    @Override
    public List<EmployerPackage> getAllEmployerPackages(Integer employerId) {
        return employerPackageRepository.findAllByEmployer_UserId(employerId);
    }

    @Override
    public EmployerPackage validateExpiredPackage(Integer employerId, Integer packageId) {
        EmployerPackageId employerPackageId= new EmployerPackageId(employerId, packageId);
        EmployerPackage employerPackage = employerPackageRepository.findById(employerPackageId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy gói tương ứng"));
        if(employerPackage.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new DataNotFoundException("Gói đã hết hạn sử dụng");
        }
        return employerPackage;
    }

    @Override
    public List<EmployerPackage> getAllByEmployerWithNonExpiredPackageAndAmount(Integer employerId) {
        return employerPackageRepository.findAllByEmployer_UserIdAndExpiredAtAfterAndAmountGreaterThan(employerId, LocalDateTime.now(), 0);
    }


    private LocalDateTime calculateExpirationDate(Package jobPackage) {
        // Giả sử rằng mỗi gói có một trường duration (số ngày có hiệu lực)
        return LocalDateTime.now().plusWeeks(jobPackage.getDuration());
    }
}
