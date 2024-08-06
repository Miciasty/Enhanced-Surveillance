package nsk.enhanced.System;

import nsk.enhanced.EnhancedSurveillance;

public class ES {


    private static EnhancedSurveillance instance;
    public static EnhancedSurveillance getInstance() {
        return instance;
    }
    public static void setInstance(EnhancedSurveillance in) {
        instance = in;
    }

}