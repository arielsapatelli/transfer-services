package br.com.payments.transfers.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.payments.transfers.commands.Credit;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Component
@AllArgsConstructor
@Log
public class CreditResolver {

	private static final String CREDIT = "/deposit";
	private static final String WALLET = "/wallet/";
	private static final String API  = "http://localhost:10000";
	
	public boolean tryCredit(Credit credit) {
		DepositRequest req = new DepositRequest();
		req.setAmount(credit.getAmount());
		Double credited = new RestTemplate().postForObject(mountURL(credit), req, Double.class);
		log.info("Amount credited: " + credited + " from: {" +credit.getTo() + "}");
		return credited.equals(credit.getAmount());
	}

	private String mountURL(Credit credit) {
		return API + WALLET + credit.getTo()+ CREDIT;
	}

}
