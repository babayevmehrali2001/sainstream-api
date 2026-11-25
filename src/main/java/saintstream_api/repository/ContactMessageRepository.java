package saintstream_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saintstream_api.model.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

}
