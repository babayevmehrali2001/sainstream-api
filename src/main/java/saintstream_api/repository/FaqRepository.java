package saintstream_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import saintstream_api.model.Faq;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findAllByOrderByOrderNumberAsc();
}
