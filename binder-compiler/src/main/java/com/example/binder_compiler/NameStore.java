package com.example.binder_compiler;

import com.example.binder_annotations.BindingSuffix;

public final class NameStore {

    private NameStore() {
        // not to be instantiated in public
    }

    public static String getGeneratedClassName (String className) {
        return className + BindingSuffix.BINDING_SUFFIX;
    }

    public static class Package {
        public static final String ANDROID_VIEW = "android.view";
    }

    public static class Methods {

        public static final String ANDROID_VIEW_ON_CLICK = "onClick";


        public  static  final String BIND_VIEW = "bindView";
        public  static  final String BIND_ON_CLICK = "bindOnClick";
    }

    public static class Class {
        // Android
        public static final String ANDROID_VIEW = "View";
        public static final String ANDROID_VIEW_ON_CLICK_LISTENER = "OnClickListener";
    }

    public static class Field {
        public  static  final String ACTIVITY = "activity";

        public  static  final String VIEW = "view";

    }
}
