package br.com.payments.transfers.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import br.com.payments.transfers.events.CreditCanceled;
import br.com.payments.transfers.events.CreditCompleted;
import br.com.payments.transfers.events.CreditExcluded;
import br.com.payments.transfers.events.DebitCanceled;
import br.com.payments.transfers.events.DebitCompleted;
import br.com.payments.transfers.events.TransferCanceled;
import br.com.payments.transfers.events.TransferCompleted;
import br.com.payments.transfers.events.TransferStarted;
import br.com.payments.transfers.query.entity.TransferEntity;
import br.com.payments.transfers.query.entity.TransferEventEntity;
import br.com.payments.transfers.query.entity.TransferStatus;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransferQueryEventhandler {

	private final TransferQueryRepository transferRepository;
	private final TransferEventRepository eventRepository;
	
	@EventHandler
	public void on(TransferStarted tr) {
		
		TransferEntity transferEntity = new TransferEntity(tr.getTransferId(),tr.getFrom(),tr.getTo()
				,tr.getAmount(),tr.getAt(),TransferStatus.INICIADO.name());
		eventRepository.save(TransferEventEntity.from(transferEntity));
		transferRepository.save(transferEntity);
	}
	
	
	@EventHandler
	public void on(CreditCompleted credited) {
		updateStatus(credited.getTransferId(), TransferStatus.CREDITADO);
				
	}
	
	@EventHandler
	public void on(CreditCanceled canceled) {
		updateStatus(canceled.getTransferId(), TransferStatus.CREDITO_CANCELADO);
	}

	@EventHandler
	public void on(CreditExcluded excluded) {
		updateStatus(excluded.getTransferId(), TransferStatus.CREDITO_ESTORNADO);
	}
	
	@EventHandler
	public void on(DebitCompleted debited) {
		updateStatus(debited.getTransferId(), TransferStatus.DEBITADO);
	}

	@EventHandler
	public void on(DebitCanceled canceled) {
		updateStatus(canceled.getTransferId(), TransferStatus.DEBITO_CANCELADO);
	}

	@EventHandler
	public void on(TransferCompleted completed) {
		updateStatus(completed.getTransferId(), TransferStatus.REALIZADO);
	}
	
	@EventHandler
	public void on(TransferCanceled completed) {
		updateStatus(completed.getTransferId(), TransferStatus.CANCELADO);
	}
	
	private void updateStatus(String id, TransferStatus status) {
		transferRepository.findById(id)
		.ifPresent(t->{
			t.setStatus(status.name());
			eventRepository.save(TransferEventEntity.from(t));
			transferRepository.save(t);
		});
	}
	
}
