package com.example.swip.service;

import com.example.swip.entity.Search;
import com.example.swip.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    public Boolean KeywordIsExist(String searchString) {
        return searchRepository.existsByKeyword(searchString);
    }

    @Transactional
    public Long saveKeyword(String searchString) {
        Search search = Search.builder()
                .keyword(searchString)
                .count(1L)
                .build();
        Search savedSearch = searchRepository.save(search);
        return savedSearch.getId();
    }

    public Search findByKeyword(String searchString) {
        return searchRepository.findByKeyword(searchString);
    }

    public List<Search> findTop6ByCount(){
        List<Search> top6ByCount = searchRepository.findTop6ByCount();
        return top6ByCount;
    }


}
