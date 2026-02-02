package com.carrental.entity;

import java.time.LocalDateTime;

import com.carrental.enums.ReviewStatus;
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
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id", nullable = false)
	@JsonIgnore
	private Vehicle vehicle;

	@Column(nullable = false)
	private Integer rating; // 1-5 stars

	@Column(columnDefinition = "TEXT")
	private String comment;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "ENUM('PENDING','APPROVED','REJECTED')")
	private ReviewStatus status = ReviewStatus.PENDING;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (status == null) {
			status = ReviewStatus.PENDING;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
