package com.example.swip.service;

import com.example.swip.entity.Search;
import com.example.swip.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    public Boolean KeywordIsExist(String queryString) {
        return searchRepository.existsByKeyword(queryString);
    }

    @Transactional
    public Long saveKeyword(String queryString) {
        Search search = Search.builder()
                .keyword(queryString)
                .build();
        Search savedSearch = searchRepository.save(search);
        return savedSearch.getId();
    }

    public Search findByKeyword(String queryString) {
        return searchRepository.findByKeyword(queryString);
    }

}
