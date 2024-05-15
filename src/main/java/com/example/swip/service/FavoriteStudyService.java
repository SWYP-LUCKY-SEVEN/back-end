package com.example.swip.service;

import com.example.swip.dto.study.StudyFilterResponse;
import com.example.swip.entity.FavoriteStudy;
import com.example.swip.entity.Study;
import com.example.swip.entity.User;
import com.example.swip.entity.compositeKey.UserStudyId;
import com.example.swip.repository.FavoriteStudyRepository;
import com.example.swip.repository.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteStudyService {
    private final UserService userService;
    private final StudyService studyService;
    private final FavoriteStudyRepository favoriteStudyRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    public List<StudyFilterResponse> getFavoriteStudyList(Long userId) {
        List<Study> list = userRepositoryCustom.favoriteStudyList(userId);
        return studyService.studyListToStudyFilterResponse(list);
    }

    @Transactional
    public boolean postFavoriteStudy(Long userId, Long studyId) {
        User user = userService.findUserById(userId);
        Study study = studyService.findStudyById(studyId);
        if(user == null || study == null)
            return false;
        FavoriteStudy favoriteStudy = FavoriteStudy.builder()
                .id(new UserStudyId(userId, studyId))
                .user(user)
                .study(study)
                .build();
        favoriteStudyRepository.save(favoriteStudy);
        return true;
    }

    @Transactional
    public boolean deleteFavoriteStudy(Long userId, Long studyId) {
        User user = userService.findUserById(userId);
        Study study = studyService.findStudyById(studyId);
        if(user == null || study == null)
            return false;
        FavoriteStudy favoriteStudy = favoriteStudyRepository.findByUserIdAndStudyId(userId, studyId);
        if(favoriteStudy == null)
            return false;
        favoriteStudyRepository.delete(favoriteStudy);
        return true;
    }
}
