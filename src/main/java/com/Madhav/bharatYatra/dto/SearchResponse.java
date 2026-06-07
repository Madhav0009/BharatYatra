package com.Madhav.bharatYatra.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

//══ SEARCH ════════════════════════════════════════════
    
@Data @Builder
public class SearchResponse {  
 private List<PlaceSummaryDTO> places;
 private String query;
 private int totalResults;
}
