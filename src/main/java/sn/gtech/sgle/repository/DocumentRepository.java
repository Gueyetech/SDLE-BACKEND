package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Document;
import sn.gtech.sgle.entity.enums.TypeDocumentEnum;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    List<Document> findByProprietaireId(UUID proprietaireId);
    
    List<Document> findByType(TypeDocumentEnum type);
    
    List<Document> findByProprietaireIdAndType(UUID proprietaireId, TypeDocumentEnum type);
    
    List<Document> findByValide(Boolean valide);
    
    List<Document> findByAttributionId(UUID attributionId);
}
