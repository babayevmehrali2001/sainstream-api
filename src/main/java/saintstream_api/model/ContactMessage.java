package saintstream_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@NoArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneCountryCode;
    private String phoneNumber;
    private String message;

    @Column(name = "is_read")
    private boolean read = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
