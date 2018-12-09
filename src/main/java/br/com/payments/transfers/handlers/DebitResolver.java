package br.com.payments.transfers.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.payments.transfers.commands.Debit;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Component
@AllArgsConstructor
@Log
public class DebitResolver {

	private static final String DEBIT = "/withdraw";
	private static final String WALLET = "/wallet/";
	private static final String API  = "http://localhost:10000";

	public boolean tryDebit(Debit debit) {
		
		WithdrawRequest req = new WithdrawRequest();
		req.setAmount(debit.getAmount());
		Double debited = new RestTemplate().postForObject(mountURL(debit), req, Double.class);
		log.info("Amount debited: " + debited + " from: {" +debit.getFrom() + "}");
		
		return debited.equals(debit.getAmount());
	}

	private String mountURL(Debit debit) {
		return API + WALLET + debit.getFrom() + DEBIT;
	}

}
