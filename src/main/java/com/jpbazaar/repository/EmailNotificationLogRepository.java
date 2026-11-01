package com.jpbazaar.repository;

import com.jpbazaar.entity.EmailNotificationLog;
import com.jpbazaar.entity.NotificationStatus;
import com.jpbazaar.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailNotificationLogRepository extends JpaRepository<EmailNotificationLog, Long> {

    boolean existsByOrderIdAndNotificationTypeAndStatus(Long orderId, NotificationType notificationType, NotificationStatus status);

    boolean existsByRecipientEmailAndNotificationTypeAndStatus(String recipientEmail, NotificationType notificationType, NotificationStatus status);

    List<EmailNotificationLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<EmailNotificationLog> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);
}
