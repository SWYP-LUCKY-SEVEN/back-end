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
        map.put("quick", Element.Quick);
        map.put("approval", Element.Approval);
    }
    public static Element toMatchingType(String matching_type){
        return map.get(matching_type);
    }
    public static String toString(MatchingType.Element matchingType){
        String result = null;
        for (String key : map.keySet()) {
            if(matchingType.equals(map.get(key)))
                result = key;
        }
        return result;
    }
}
