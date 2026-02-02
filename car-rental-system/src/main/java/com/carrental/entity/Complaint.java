package com.carrental.entity;

import java.time.LocalDateTime;

import com.carrental.enums.ComplaintStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "complaints")
public class Complaint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@Column(nullable = false)
	private String subject;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "booking_id")
	private Integer bookingId; // Optional: link to a specific booking

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('PENDING','RESOLVED','CLOSED')")
	private ComplaintStatus status = ComplaintStatus.PENDING;

	@Column(name = "admin_response", columnDefinition = "TEXT")
	private String adminResponse; // Admin's response/resolution

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (status == null) {
			status = ComplaintStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
		if (status == ComplaintStatus.RESOLVED && resolvedAt == null) {
			resolvedAt = LocalDateTime.now();
		}
	}

}
