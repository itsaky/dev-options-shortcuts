-keep class androidx.work.impl.WorkDatabase { *; }
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class androidx.work.** { <init>(...); }
-keep class * extends androidx.work.InputMerger { <init>(); }
-keep class * extends androidx.work.ListenableWorker { <init>(android.content.Context, androidx.work.WorkerParameters); }

-allowaccessmodification
-repackageclasses

-dontwarn kotlinx.coroutines.debug.**

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkNotNullParameter(...);
    static void checkNotNullExpressionValue(...);
    static void checkExpressionValueIsNotNull(...);
    static void checkNotNull(...);
    static void checkParameterIsNotNull(...);
    static void checkReturnedValueIsNotNull(...);
}

-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
