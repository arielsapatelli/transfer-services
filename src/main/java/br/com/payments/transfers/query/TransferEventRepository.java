package br.com.payments.transfers.query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.payments.transfers.query.entity.TransferEventEntity;

@Repository
public interface TransferEventRepository extends JpaRepository<TransferEventEntity, Long> {

	List<TransferEventEntity> findAllByIdTransfer(String idTransfer);
}

