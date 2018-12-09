package br.com.payments.transfers.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTransfersByFromId {

	private String fromId;
}
