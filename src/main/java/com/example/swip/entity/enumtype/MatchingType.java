package com.example.swip.entity.enumtype;

import java.util.HashMap;
import java.util.Map;

public class MatchingType {
    public enum Element {
        Quick, Approval  //빠른매칭, 승인
    }
    private static final Map<String, Element> map;
    static {
        map = new HashMap<>();
        map.put("approval", Element.Approval);
        map.put("quick", Element.Quick);
    }
    public static Element toMatchingType(String matching_type){
        return map.get(matching_type);
    }
}
