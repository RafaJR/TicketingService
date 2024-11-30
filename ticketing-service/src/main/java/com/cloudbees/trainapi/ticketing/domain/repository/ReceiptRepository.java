package com.cloudbees.trainapi.ticketing.domain.repository;

import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

}
