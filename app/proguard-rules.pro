# Add project specific ProGuard rules here.
# sora-editor / tm4e are used via reflection in some paths; keep them safe.
-keep class io.github.rosemoe.sora.** { *; }
-keep class org.eclipse.tm4e.** { *; }
-dontwarn org.eclipse.tm4e.**
