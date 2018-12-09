package br.com.payments.transfers.commands;

import java.time.LocalDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcludeCredit {

	@TargetAggregateIdentifier
	private final String transferId;
	private final String to;
	private final LocalDateTime at;
	private final double amount;
	
}
