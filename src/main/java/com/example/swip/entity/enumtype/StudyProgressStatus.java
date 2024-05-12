package com.example.swip.entity.enumtype;

import java.util.HashMap;
import java.util.Map;

public class StudyProgressStatus {
    public enum Element {
        BeforeStart, InProgress, Done
    }
    private static final Map<String, Element> map;
    static {
        map = new HashMap<>();
        map.put("before", Element.BeforeStart);
        map.put("progress", Element.InProgress);
        map.put("done", Element.Done);
    }
    public static Element toStudyProgressStatusType(String status){
        return map.get(status);
    }
}
