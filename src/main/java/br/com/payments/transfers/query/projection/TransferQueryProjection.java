package br.com.payments.transfers.query.projection;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.payments.transfers.query.GetEventsById;
import br.com.payments.transfers.query.GetTransfersByFromId;
import br.com.payments.transfers.query.entity.TransferEntity;
import br.com.payments.transfers.query.entity.TransferEventEntity;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value="/transfers")
@AllArgsConstructor
public class TransferQueryProjection {

	private final QueryGateway queryGateway;
	
	@GetMapping
	public CompletableFuture<List<TransferEntity>> getByFrom(@RequestParam ("from") String fromId) {
		return queryGateway.query(GetTransfersByFromId.builder().fromId(fromId).build(),ResponseTypes.multipleInstancesOf(TransferEntity.class));
	}
	
	@GetMapping(value="/{id}/events")
	public CompletableFuture<List<TransferEventEntity>> getEventsById(@PathVariable("id") String idTransfer) {
		return queryGateway.query(GetEventsById.builder().idTransfer(idTransfer).build(),ResponseTypes.multipleInstancesOf(TransferEventEntity.class));
	}
}
