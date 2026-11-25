package saintstream_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saintstream_api.dto.support.ContactRequest;
import saintstream_api.dto.support.ContactResponse;
import saintstream_api.dto.support.FaqResponse;
import saintstream_api.exception.ApiException;
import saintstream_api.model.ContactMessage;
import saintstream_api.repository.ContactMessageRepository;
import saintstream_api.repository.FaqRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SupportService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");
    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");
    private static final int MIN_MESSAGE_LENGTH = 20;

    private final ContactMessageRepository contactMessageRepository;
    private final FaqRepository faqRepository;

    @Transactional
    public ContactResponse sendContactMessage(ContactRequest request) {
        if (request == null) {
            request = new ContactRequest();
        }

        String firstName = clean(request.getFirstName());
        String lastName = clean(request.getLastName());
        String email = clean(request.getEmail()).toLowerCase();
        String phoneCountryCode = clean(request.getPhoneCountryCode());
        String phoneNumber = clean(request.getPhoneNumber());
        String message = clean(request.getMessage());

        if (firstName.isEmpty()) {
            throw badRequest("First name is required");
        }
        if (lastName.isEmpty()) {
            throw badRequest("Last name is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw badRequest("Invalid email format");
        }
        if (phoneCountryCode.isEmpty()) {
            throw badRequest("Phone country code is required");
        }
        if (phoneNumber.isEmpty()) {
            throw badRequest("Phone number is required");
        }
        if (!DIGITS_ONLY.matcher(phoneNumber).matches()) {
            throw badRequest("Phone number must contain only digits");
        }
        if (message.isEmpty()) {
            throw badRequest("Message is required");
        }
        if (message.length() < MIN_MESSAGE_LENGTH) {
            throw badRequest("Message must be at least 20 characters");
        }

        ContactMessage contact = new ContactMessage();
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneCountryCode(phoneCountryCode);
        contact.setPhoneNumber(phoneNumber);
        contact.setMessage(message);

        ContactMessage saved = contactMessageRepository.save(contact);

        return new ContactResponse(saved.getId(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<FaqResponse> getFaqs() {
        return faqRepository.findAllByOrderByOrderNumberAsc().stream()
                .map(f -> new FaqResponse(f.getId(), f.getQuestion(), f.getAnswer(), f.getOrderNumber()))
                .toList();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }
}
