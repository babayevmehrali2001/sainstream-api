package saintstream_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import saintstream_api.dto.ApiResponse;
import saintstream_api.dto.support.ContactRequest;
import saintstream_api.dto.support.ContactResponse;
import saintstream_api.dto.support.FaqResponse;
import saintstream_api.service.SupportService;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    // POST /api/support/contact → 201
    @PostMapping("/contact")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContactResponse> sendContactMessage(
            @RequestBody(required = false) ContactRequest request) {
        return new ApiResponse<>(true, "Your message has been sent successfully",
                supportService.sendContactMessage(request));
    }

    // GET /api/support/faqs → 200
    @GetMapping("/faqs")
    public ApiResponse<List<FaqResponse>> getFaqs() {
        return new ApiResponse<>(true, "OK", supportService.getFaqs());
    }
}
