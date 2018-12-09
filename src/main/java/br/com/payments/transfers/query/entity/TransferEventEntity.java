package br.com.payments.transfers.query.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "transfers_events")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class TransferEventEntity {

	@Id
	@GeneratedValue
	private Long id;
	private String idTransfer;	
	@Column(name="from_id")
	private String from;
	@Column(name="to_id")
	private String to;
	private double amount;
	private LocalDateTime at;
	private String status;
	
	public static TransferEventEntity from(TransferEntity t) {
		return new TransferEventEntity(null,t.getId(),t.getFrom()
				,t.getTo(),t.getAmount(),t.getAt(),t.getStatus());
	}
}
