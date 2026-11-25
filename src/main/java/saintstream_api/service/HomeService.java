package saintstream_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saintstream_api.dto.*;
import saintstream_api.exception.BadRequestException;
import saintstream_api.repository.HomeRepository;
import saintstream_api.repository.HomeRepository.CollectionSectionRow;
import saintstream_api.repository.HomeRepository.SectionRow;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

    private static final List<String> ALLOWED_SECTIONS = List.of(
            "just-release", "popular-week", "movies", "series",
            "korean-series", "awards", "fast", "live");

    public List<PlatformResponse> getPlatforms() {
        return homeRepository.findActivePlatforms().stream()
                .map(p -> new PlatformResponse(p.getId(), p.getName(), p.getLogoUrl()))
                .toList();
    }

    public List<HeroResponse> getHero() {
        return homeRepository.findHero().stream()
                .map(h -> new HeroResponse(
                        h.getId(), h.getTitle(), h.getType(),
                        h.getReleaseYear(), h.getDurationMinutes(), h.getSeasonCount(),
                        toList(h.getGenres()),
                        h.getDescription(), h.getBackdropUrl(), h.getTrailerUrl()))
                .toList();
    }

    public List<FeaturedResponse> getFeatured() {
        return homeRepository.findFeatured().stream()
                .map(f -> new FeaturedResponse(
                        f.getId(), f.getTitle(), f.getTagline(), f.getRating(),
                        f.getDurationMinutes(), f.getReleaseYear(),
                        toList(f.getGenres()),
                        f.getDescription(), f.getPosterUrl(), f.getBackdropUrl()))
                .toList();
    }

    public SectionResponse getSection(String section, Integer limit) {
        if (section == null || !ALLOWED_SECTIONS.contains(section)) {
            throw new BadRequestException(
                    "Invalid section. Allowed values: " + String.join(", ", ALLOWED_SECTIONS));
        }

        int finalLimit = (limit == null) ? 10 : limit;
        if (finalLimit < 1 || finalLimit > 20) {
            throw new BadRequestException("Limit must be between 1 and 20");
        }

        List<SectionItemResponse> items = switch (section) {
            case "just-release" -> toItems(homeRepository.findLatest(finalLimit));
            case "movies" -> toItems(homeRepository.findTopRatedByType("movie", finalLimit));
            case "series" -> toItems(homeRepository.findTopRatedByType("series", finalLimit));
            case "korean-series" -> toItems(
                    homeRepository.findTopRatedByTypeAndCountry("series", "KR", finalLimit));
            default -> homeRepository.findSectionFromCollection(section, finalLimit).stream()
                    .map(row -> toCollectionItem(row, section))
                    .toList();
        };

        return new SectionResponse(section, items);
    }


    private List<SectionItemResponse> toItems(List<SectionRow> rows) {
        return rows.stream().map(this::toItem).toList();
    }

    private SectionItemResponse toItem(SectionRow r) {
        SectionItemResponse item = new SectionItemResponse();
        item.setId(r.getId());
        item.setTitle(r.getTitle());
        item.setPosterUrl(r.getPosterUrl());
        item.setType(r.getType());
        item.setRating(r.getRating());
        item.setAgeRating(r.getAgeRating());
        item.setReleaseYear(r.getReleaseYear());
        item.setSeasonCount(r.getSeasonCount());
        item.setGenres(toList(r.getGenres()));
        return item;
    }

    private SectionItemResponse toCollectionItem(CollectionSectionRow r, String section) {
        SectionItemResponse item = toItem(r);

        if (section.equals("popular-week")) {
            item.setRank(r.getOrderNumber());
        }

        if (section.equals("awards")) {
            item.setLabel(r.getLabel());
            item.setDescription(r.getDescription());
            item.setBackdropUrl(r.getBackdropUrl());
            item.setDurationMinutes(r.getDurationMinutes());
            if (r.getPlatformName() != null) {
                item.setPlatform(new PlatformInfo(r.getPlatformName(), r.getPlatformLogoUrl()));
            }
        }
        return item;
    }

    private List<String> toList(String genres) {
        if (genres == null) {
            return List.of();
        }
        return List.of(genres.split(","));
    }
}
