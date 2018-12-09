package br.com.payments.transfers.query;

import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import br.com.payments.transfers.query.entity.TransferEntity;
import br.com.payments.transfers.query.entity.TransferEventEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransferQueryHandler {

	private final TransferQueryRepository transferRepository;
	private final TransferEventRepository transferEventsRepository;

	@QueryHandler
	public List<TransferEntity> on(GetTransfersByFromId get){
		return transferRepository.findAllByFrom(get.getFromId());
	}
	
	@QueryHandler
	public List<TransferEventEntity> on(GetEventsById get){
		return transferEventsRepository.findAllByIdTransfer(get.getIdTransfer());
	}
	
}
