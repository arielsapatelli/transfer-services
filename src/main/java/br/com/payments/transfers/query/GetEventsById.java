package br.com.payments.transfers.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetEventsById {

	private String idTransfer;
}
