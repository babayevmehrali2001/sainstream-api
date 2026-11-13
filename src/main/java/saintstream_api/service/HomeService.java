package saintstream_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import saintstream_api.dto.*;
import saintstream_api.exception.BadRequestException;
import saintstream_api.repository.HomeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;

    public List<PlatformResponse> getPlatforms() {

        return homeRepository.findActivePlatforms();
    }

    public List<HeroResponse> getHero() {
        return homeRepository.findHero();
    }

    public List<FeaturedResponse> getFeatured(){
        return homeRepository.findFeatured();
    }


    private static final List<String> ALLOWED_SECTIONS = List.of(
            "just-release", "popular-week", "movies", "series",
            "korean-series", "awards", "fast", "live");

    public SectionResponse getSection(String section, Integer limit) {
        // 1) section yoxlaması
        if (section == null || !ALLOWED_SECTIONS.contains(section)) {
            throw new BadRequestException(
                    "Invalid section. Allowed values: " + String.join(", ", ALLOWED_SECTIONS));
        }

        // 2) limit yoxlaması (göndərilməyibsə, default 10)
        int finalLimit = (limit == null) ? 10 : limit;
        if (finalLimit < 1 || finalLimit > 20) {
            throw new BadRequestException("Limit must be between 1 and 20");
        }

        // 3) bölməyə görə uyğun sorğunu seç
        List<SectionItemResponse> items = switch (section) {
            case "just-release" -> homeRepository.findSectionFromContents(
                    "1 = 1", "c.release_date DESC, c.id", List.of(), finalLimit);
            case "movies" -> homeRepository.findSectionFromContents(
                    "c.type = ?", "c.rating DESC, c.id", List.of("movie"), finalLimit);
            case "series" -> homeRepository.findSectionFromContents(
                    "c.type = ?", "c.rating DESC, c.id", List.of("series"), finalLimit);
            case "korean-series" -> homeRepository.findSectionFromContents(
                    "c.type = ? AND c.origin_country = ?", "c.rating DESC, c.id",
                    List.of("series", "KR"), finalLimit);
            default -> homeRepository.findSectionFromCollection(section, finalLimit);   // popular-week, awards, fast, live
        };

        return new SectionResponse(section, items);
    }
}