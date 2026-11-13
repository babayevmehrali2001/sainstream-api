package saintstream_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saintstream_api.dto.*;
import saintstream_api.service.HomeService;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/platforms")
    public ApiResponse<List<PlatformResponse>> getPlatforms() {
        return new ApiResponse<>(true, "OK", homeService.getPlatforms());
    }

    @GetMapping("/hero")
    public ApiResponse<List<HeroResponse>> getHero() {
        return new ApiResponse<>(true, "OK", homeService.getHero());
    }

    @GetMapping("/featured")
    public ApiResponse<List<FeaturedResponse>> getFeatured(){
        return new ApiResponse<>(true,"OK",homeService.getFeatured());
    }

    @GetMapping("/sections")
    public ApiResponse<SectionResponse> getSections(
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Integer limit) {
        return new ApiResponse<>(true, "OK", homeService.getSection(section, limit));
    }
}