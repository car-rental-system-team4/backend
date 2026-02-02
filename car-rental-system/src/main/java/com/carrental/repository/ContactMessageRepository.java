package com.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carrental.entity.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Integer> {
}
