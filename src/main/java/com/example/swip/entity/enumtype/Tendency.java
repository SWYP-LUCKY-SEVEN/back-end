package com.example.swip.entity.enumtype;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.Map;

public class Tendency {
    //Active: 활발한 대화와 동기부여 원해요
    //Feedback: 피드백을 주고 받고 싶어요
    //Focus: 조용히 집중하고 싶어요
    public enum Element {
        Active, Feedback, Focus
    }
    private static final Map<String,Element> map;
    static {
        map = new HashMap<>();
        map.put("active", Element.Active);
        map.put("feedback", Element.Feedback);
        map.put("focus", Element.Focus);
    }
    public static Element toTendency(String tendency){
        return map.get(tendency);
    }
    public static String toString(Element tendency){
        String result = null;
        for (String key : map.keySet()) {
            if(tendency.equals(map.get(key)))
                result = key;
        }
        return result;
    }
}