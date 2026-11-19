package saintstream_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saintstream_api.dto.WatchlistContentResponse;
import saintstream_api.dto.WatchlistItemResponse;
import saintstream_api.dto.WatchlistRequest;
import saintstream_api.exception.ApiException;
import saintstream_api.model.WatchlistItem;
import saintstream_api.repository.WatchlistItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistItemRepository watchlistItemRepository;

    public WatchlistItemResponse add(Long userId, WatchlistRequest request) {
        Long contentId = request == null ? null : request.getContentId();

        if (contentId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "contentId is required and must be a number");
        }

        if (watchlistItemRepository.countContent(contentId) == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Content not found");
        }

        if (watchlistItemRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Already in watchlist");
        }

        try {
            WatchlistItem item = new WatchlistItem();
            item.setUserId(userId);
            item.setContentId(contentId);
            item = watchlistItemRepository.save(item);
            return new WatchlistItemResponse(item.getId(), item.getContentId(), item.getAddedAt());
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.CONFLICT, "Already in watchlist");
        }
    }

    @Transactional
    public void remove(Long userId, Long contentId) {
        long deleted = watchlistItemRepository.deleteByUserIdAndContentId(userId, contentId);
        if (deleted == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Not in watchlist");
        }
    }

    public List<WatchlistContentResponse> getMyList(Long userId) {
        return watchlistItemRepository.findMyList(userId).stream()
                .map(row -> new WatchlistContentResponse(
                        row.getId(),
                        row.getTitle(),
                        row.getPosterUrl(),
                        row.getType(),
                        row.getRating(),
                        row.getAgeRating(),
                        row.getGenres() == null ? List.of() : List.of(row.getGenres().split(",")),
                        row.getAddedAt()))
                .toList();
    }
}