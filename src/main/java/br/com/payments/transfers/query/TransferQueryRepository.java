package br.com.payments.transfers.query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.payments.transfers.query.entity.TransferEntity;

@Repository
public interface TransferQueryRepository extends JpaRepository<TransferEntity, String> {

	List<TransferEntity> findAllByFrom(String from);
}

