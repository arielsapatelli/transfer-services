package br.com.payments.transfers.controller;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.payments.transfers.commands.StartTransfer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController()
@RequestMapping("transfers")
public class TransferCommandController {

	private final CommandGateway commandGateway;

	@PostMapping
	public CompletableFuture<String> makeTransfer(@RequestBody TransferStartRequest req) {
		return commandGateway.send(StartTransfer.builder()
				.transferId(UUID.randomUUID().toString())
				.from(req.getFrom())
				.to(req.getTo())
				.amount(req.getAmount())
				.at(LocalDateTime.now())
				.build());
	}

}
