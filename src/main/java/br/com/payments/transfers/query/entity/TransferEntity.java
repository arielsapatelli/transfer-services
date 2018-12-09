package br.com.payments.transfers.query.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "transfers")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class TransferEntity {

	@Id
	private String id;
	@Column(name="from_id")
	private String from;
	@Column(name="to_id")
	private String to;
	private double amount;
	private LocalDateTime at;
	private String status;
	
	
	
}
