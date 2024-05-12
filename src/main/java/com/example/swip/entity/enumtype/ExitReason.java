package com.example.swip.entity.enumtype;

import java.util.HashMap;
import java.util.Map;

public class ExitReason {
    public enum Element {
        Abuse, Promotion, Objective, NonParticipation, Leave
    }
    private static final Map<String, ExitReason.Element> map;

    static {
        map = new HashMap<>();
        map.put("과도한 비방이나 욕설", Element.Abuse);
        map.put("홍보성 메시지", Element.Promotion);
        map.put("목적 외 스터디 사용", Element.Objective);
        map.put("스터디 미참여", Element.NonParticipation);
        map.put("이탈", Element.Leave);
    }
    public static Element toExitResaon(String exit_reason){
        return map.get(exit_reason);
    }
}