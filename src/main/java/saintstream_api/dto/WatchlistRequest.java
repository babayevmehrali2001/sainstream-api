package saintstream_api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class WatchlistRequest {

    private Long contentId;
}