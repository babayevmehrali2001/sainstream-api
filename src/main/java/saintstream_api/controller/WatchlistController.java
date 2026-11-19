package saintstream_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import saintstream_api.dto.ApiResponse;
import saintstream_api.dto.WatchlistContentResponse;
import saintstream_api.dto.WatchlistItemResponse;
import saintstream_api.dto.WatchlistRequest;
import saintstream_api.service.WatchlistService;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WatchlistItemResponse> add(@AuthenticationPrincipal Long userId,
                                                  @RequestBody(required = false) WatchlistRequest request) {
        return new ApiResponse<>(true, "Added to watchlist", watchlistService.add(userId, request));
    }

    @DeleteMapping("/{contentId}")
    public ApiResponse<Void> remove(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long contentId) {
        watchlistService.remove(userId, contentId);
        return new ApiResponse<>(true, "Removed from watchlist", null);
    }

    @GetMapping
    public ApiResponse<List<WatchlistContentResponse>> getMyList(@AuthenticationPrincipal Long userId) {
        return new ApiResponse<>(true, "OK", watchlistService.getMyList(userId));
    }
}