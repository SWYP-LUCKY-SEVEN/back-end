package com.example.swip.service;

import com.example.swip.entity.AdditionalInfo;
import com.example.swip.entity.Study;
import com.example.swip.repository.AdditionalInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdditionalInfoService {
    private final AdditionalInfoRepository additionalInfoRepository;

    //추가 정보 저장
    @Transactional
    public void saveAddInfo(List<String> additional_info_list, Study study){
        for (String add_info : additional_info_list) {

            AdditionalInfo additionalInfo = AdditionalInfo.builder()
                    .name(add_info)
                    .study(study)
                    .build();

            additionalInfoRepository.save(additionalInfo);
        }
    }
}
