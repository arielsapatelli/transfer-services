package br.com.payments.transfers.aggregate;

import java.time.LocalDateTime;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import br.com.payments.transfers.commands.CancelDebit;
import br.com.payments.transfers.commands.CompleteTransfer;
import br.com.payments.transfers.commands.Credit;
import br.com.payments.transfers.commands.Debit;
import br.com.payments.transfers.commands.ExcludeCredit;
import br.com.payments.transfers.commands.StartTransfer;
import br.com.payments.transfers.events.CreditCanceled;
import br.com.payments.transfers.events.CreditCompleted;
import br.com.payments.transfers.events.CreditExcluded;
import br.com.payments.transfers.events.DebitCanceled;
import br.com.payments.transfers.events.DebitCompleted;
import br.com.payments.transfers.events.TransferCanceled;
import br.com.payments.transfers.events.TransferCompleted;
import br.com.payments.transfers.events.TransferStarted;
import br.com.payments.transfers.handlers.CreditResolver;
import br.com.payments.transfers.handlers.DebitResolver;
import lombok.Data;
import lombok.NoArgsConstructor;

@Aggregate
@Data
@NoArgsConstructor
public class Transfer {

	@AggregateIdentifier
	private String transferId;
	private String from;
	private String to;
	private double amount;
	private LocalDateTime at;
	private LocalDateTime debitAt;
	private LocalDateTime creditAt;
	private LocalDateTime completeAt;

	@CommandHandler
	public Transfer(StartTransfer start) {
		Assert.isTrue(start.getAmount() > 0.0, "Amount must be greater then 0.0");
		AggregateLifecycle.apply(TransferStarted.builder()
				.transferId(start.getTransferId())
				.amount(start.getAmount())
				.from(start.getFrom())
				.to(start.getTo())
				.at(start.getAt()).build());
	}

	@CommandHandler
	public void on(Debit debit) {

		if (new DebitResolver().tryDebit(debit)) {

			AggregateLifecycle.apply(DebitCompleted.builder().amount(debit.getAmount()).from(debit.getFrom())
					.at(LocalDateTime.now()).transferId(debit.getTransferId()).build());
		} else {

			AggregateLifecycle.apply(DebitCanceled.builder().amount(debit.getAmount()).from(debit.getFrom())
					.at(LocalDateTime.now()).transferId(debit.getTransferId()).build());
		}
	}

	@CommandHandler
	public void on(Credit credit) {

		if (new CreditResolver().tryCredit(credit)) {

			AggregateLifecycle.apply(CreditCompleted.builder().amount(credit.getAmount()).to(credit.getTo())
					.at(LocalDateTime.now()).transferId(credit.getTransferId()).build());
		} else {

			AggregateLifecycle.apply(CreditCanceled.builder().amount(credit.getAmount()).to(credit.getTo())
					.at(LocalDateTime.now()).transferId(credit.getTransferId()).build());
		}
	}

	@CommandHandler
	public void on(CompleteTransfer completing, CommandGateway commandGateway) {
		//CreditResolver creditResolver = new CreditResolver();
		if (completing.isDebited() && completing.isCredited()) {
			AggregateLifecycle.apply(TransferCompleted.builder().amount(completing.getAmount()).to(completing.getTo())
					.at(LocalDateTime.now()).transferId(completing.getTransferId()).build());
		} else {

			undo(completing,commandGateway);
			AggregateLifecycle.apply(TransferCanceled.builder().amount(completing.getAmount()).to(completing.getTo())
					.at(LocalDateTime.now()).transferId(completing.getTransferId()).build());
		}
	}
	
	@CommandHandler
	public void on(ExcludeCredit exclude) {
		DebitResolver debitResolver = new DebitResolver();
		debitResolver.tryDebit(Debit.builder().transferId(exclude.getTransferId()).at(exclude.getAt())
				.from(exclude.getTo()).amount(exclude.getAmount()).build());
		
		AggregateLifecycle.apply(CreditExcluded.builder()
				.transferId(exclude.getTransferId())
				.amount(exclude.getAmount())
				.at(exclude.getAt())
				.to(exclude.getTo())
				.build());
	}

	@CommandHandler
	public void on(CancelDebit debit) {

		AggregateLifecycle.apply(DebitCanceled.builder().amount(debit.getAmount()).from(debit.getFrom())
				.at(LocalDateTime.now()).transferId(debit.getTransferId()).build());
	}

	
	@EventSourcingHandler
	public void on(CreditExcluded excluded) {
		this.creditAt =null;
	}

	
	@EventSourcingHandler
	public void on(TransferCanceled canceled) {
		this.completeAt = null;
	}

	@EventSourcingHandler
	public void on(TransferCompleted completed) {
		this.completeAt = LocalDateTime.now();
	}

	@EventSourcingHandler
	public void on(TransferStarted started) {
		this.transferId = started.getTransferId();
		this.from = started.getFrom();
		this.to = started.getTo();
		this.amount = started.getAmount();
		this.at = started.getAt();
	}

	@EventSourcingHandler
	public void on(DebitCompleted debited) {
		this.debitAt = debited.getAt();
	}

	@EventSourcingHandler
	public void on(CreditCompleted credited) {
		this.creditAt = credited.getAt();
	}

	@EventSourcingHandler
	public void on(DebitCanceled canceled) {
		this.debitAt = null;
	}

	@EventSourcingHandler
	public void on(CreditCanceled canceled) {
		this.creditAt = null;
	}

	private void undo(CompleteTransfer completing,CommandGateway commandGateway) {
		if (completing.isDebited()) {
			//creditResolver.tryCredit(Credit.builder().transferId(completing.getTransferId()).at(completing.getAt())
			//		.to(completing.getFrom()).amount(completing.getAmount()).build());
		}

		if (completing.isCredited()) {
			
			commandGateway.send(ExcludeCredit.builder()
					.transferId(completing.getTransferId())
					.amount(completing.getAmount())
					.to(completing.getTo())
					.at(completing.getAt())
					.build());
		}
	}

}
