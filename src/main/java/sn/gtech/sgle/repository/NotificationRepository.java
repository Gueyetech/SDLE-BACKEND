package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Notification;
import sn.gtech.sgle.entity.enums.TypeNotificationEnum;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    List<Notification> findByDestinataireId(UUID destinataireId);
    
    List<Notification> findByDestinataireIdAndLue(UUID destinataireId, Boolean lue);
    
    List<Notification> findByDestinataireIdOrderByDateEnvoiDesc(UUID destinataireId);
    
    List<Notification> findByType(TypeNotificationEnum type);
    
    Long countByDestinataireIdAndLue(UUID destinataireId, Boolean lue);
}
