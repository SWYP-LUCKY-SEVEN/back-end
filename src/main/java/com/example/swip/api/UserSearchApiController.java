package com.example.swip.api;

import com.example.swip.config.UserPrincipal;
import com.example.swip.dto.search.deletedSearchResponse;
import com.example.swip.dto.search.popularSearchReponse;
import com.example.swip.dto.search.recentSearchResponse;
import com.example.swip.entity.Search;
import com.example.swip.entity.UserSearch;
import com.example.swip.service.SearchService;
import com.example.swip.service.UserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserSearchApiController {

    private final UserSearchService userSearchService;
    private final SearchService searchService;

    @Operation(summary = "최근 검색어 조회 API")
    @GetMapping("/userSearch/recent")
    public Result getRecentSearch(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        List<UserSearch> recentSearchByUserId = userSearchService.findRecentSearchByUserId(principal.getUserId());
        List<recentSearchResponse> response = recentSearchByUserId.stream()
                .map(userSearch -> new recentSearchResponse(
                        userSearch.getSearch().getId(),
                        userSearch.getSearch().getKeyword()
                ))
                .collect(Collectors.toList());

        return new Result(response, response.size());

    }

    @Operation(summary = "인기 검색어 조회 API")
    @GetMapping("/userSearch/popular")
    public Result getPopularSearch() {
        List<Search> top2ByCount = searchService.findTop2ByCount();
        List<popularSearchReponse> reponses = top2ByCount.stream()
                .map(search -> new popularSearchReponse(
                        search.getId(),
                        search.getKeyword(),
                        search.getCount()
                ))
                .collect(Collectors.toList());

        return new Result(reponses, reponses.size());
    }

    @Operation(summary = "최근 검색어 삭제 API")
    @DeleteMapping("/userSearch/recent")
    public deletedSearchResponse deleteRecentSearch(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        long deletedCount = userSearchService.deleteRecentSearch(principal.getUserId());
        if(deletedCount!=0){
            return new deletedSearchResponse(true, deletedCount);
        }
        else {
            return new deletedSearchResponse(false, deletedCount);
        }
    }
    @Operation(summary = "최근 검색어 단일 삭제 API")
    @DeleteMapping("/userSearch/recent/{keyword_id}")
    public deletedSearchResponse deleteRecentSearch(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("keyword_id") Long keywordId
    ){
        userSearchService.deleteRecentSearch(principal.getUserId(), keywordId);
        return new deletedSearchResponse(true, 1);
    }

    // List 값을 Result로 한 번 감싸서 return하기 위한 class
    @Data
    @AllArgsConstructor
    public static class Result<T>{
        private T data;
        private int totalCount;
    }
}
