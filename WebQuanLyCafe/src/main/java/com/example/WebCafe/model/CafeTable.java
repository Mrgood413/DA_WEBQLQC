package com.example.WebCafe.model;

import com.example.WebCafe.model.enums.TableStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cafe_tables")
@Getter
@Setter
@NoArgsConstructor
public class CafeTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "table_number", nullable = false, unique = true)
	private Integer tableNumber;

	@Enumerated(EnumType.STRING)
	private TableStatus status = TableStatus.EMPTY;

	@OneToMany(mappedBy = "table")
	private List<CafeOrder> orders = new ArrayList<>();
}
