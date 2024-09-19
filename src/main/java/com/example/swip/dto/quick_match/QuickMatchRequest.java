package com.example.swip.dto.quick_match;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class QuickMatchRequest {
    private Long page = 0L;
    private Long size = 3L;
    @NotNull
    private Boolean save;
    @NotNull
    private String category;
    @NotNull
    private String duration;
    @NotNull
    private List<Long> mem_scope;
    @NotNull
    private List<String> tendency;
}
