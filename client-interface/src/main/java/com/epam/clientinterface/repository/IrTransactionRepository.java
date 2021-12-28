package com.epam.clientinterface.repository;

import com.epam.clientinterface.entity.IrTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrTransactionRepository extends JpaRepository<IrTransaction, Long> {
    Optional<IrTransaction> findFirstByAccountIdOrderByDateDesc(long accountId);
}
