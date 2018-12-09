package br.com.payments.transfers.exception;

public class InsufficientCashException extends Exception {

	public InsufficientCashException(String messsage) {
		super(messsage);
	}

	private static final long serialVersionUID = -1834436408533303738L;

}
