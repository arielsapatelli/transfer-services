package br.com.payments.transfers.aggregate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.payments.transfers.commands.CancelDebit;
import br.com.payments.transfers.commands.CompleteTransfer;
import br.com.payments.transfers.commands.Credit;
import br.com.payments.transfers.commands.Debit;
import br.com.payments.transfers.events.CreditCanceled;
import br.com.payments.transfers.events.CreditCompleted;
import br.com.payments.transfers.events.DebitCanceled;
import br.com.payments.transfers.events.DebitCompleted;
import br.com.payments.transfers.events.TransferCanceled;
import br.com.payments.transfers.events.TransferCompleted;
import br.com.payments.transfers.events.TransferStarted;
import lombok.Data;
import lombok.extern.java.Log;

@Saga
@Data
@Log
public class TransferSaga implements Serializable {

	private static final long serialVersionUID = 5059563016500267647L;
	transient CommandGateway commandGateway;

	private String id;
	private LocalDateTime at;
	private double amount;
	private String to;
	private String from;

	private boolean debited;
	private boolean triedDebit;
	private boolean credited;
	private boolean triedCredit;
	private boolean completed;
	@Autowired
	public void setCommandGateway(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@StartSaga
	@SagaEventHandler(associationProperty = "transferId")
	public void handle(TransferStarted startedEvent) {
		this.id = startedEvent.getTransferId();
		this.amount = startedEvent.getAmount();
		this.to = startedEvent.getTo();
		this.from = startedEvent.getFrom();
		this.at = startedEvent.getAt();

		log.info(""+ startedEvent);
		log.info("Debiting from {" + this.from + " }... ");
		CompletableFuture<Object> send = commandGateway.send(Debit.builder().amount(this.amount).from(this.from).transferId(this.id).build());
		
		if(send.isCompletedExceptionally()) {
			commandGateway.send(CancelDebit.builder().amount(this.amount).from(this.from).transferId(this.id).build());

		}

		log.info("Crediting to {" + this.from + " }... ");
		commandGateway.send(Credit.builder().amount(this.amount).to(this.to).transferId(this.id).build());
	}

	
	@EndSaga
	@SagaEventHandler(associationProperty = "transferId")
	public void handler(TransferCompleted completed) {
		this.completed = Boolean.TRUE;
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "transferId")
	public void handler(TransferCanceled completed) {
		this.completed = Boolean.FALSE;
	}
	@SagaEventHandler(associationProperty = "transferId")
	public void handler(DebitCompleted debited) {
		this.debited = Boolean.TRUE;
		this.triedDebit = Boolean.TRUE;
		finishTransfer(this.credited, this.triedCredit);

	}

	@SagaEventHandler(associationProperty = "transferId")
	public void handler(DebitCanceled canceled) {
		this.debited = Boolean.FALSE;
		this.triedDebit = Boolean.TRUE;
		finishTransfer(this.credited, this.triedCredit);
	}

	@SagaEventHandler(associationProperty = "transferId")
	public void handler(CreditCanceled canceled) {
		this.credited = Boolean.FALSE;
		this.triedCredit = Boolean.TRUE;
		finishTransfer(this.debited, this.triedDebit);

	}

	@SagaEventHandler(associationProperty = "transferId")
	public void handler(CreditCompleted credited) {
		this.credited = Boolean.TRUE;
		this.triedCredit = Boolean.TRUE;
		finishTransfer(this.debited, this.triedDebit);
	}

	private void finishTransfer(boolean done, boolean tried) {
		if (done || tried) {
			commandGateway.send(CompleteTransfer.builder().transferId(this.id).amount(this.amount).from(this.from)
					.to(this.to)
					.credited(this.credited)
					.debited(this.debited)
					.at(this.at).build());
		}
	}

}
