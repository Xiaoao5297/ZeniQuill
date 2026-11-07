# Add project specific ProGuard rules here.
# sora-editor / tm4e are used via reflection in some paths; keep them safe.
-keep class io.github.rosemoe.sora.** { *; }
-keep class org.eclipse.tm4e.** { *; }
-dontwarn org.eclipse.tm4e.**

# SnakeYAML (tm4e theme/grammar parsing) accesses via reflection.
-dontwarn org.snakeyaml.engine.**
-keep class org.snakeyaml.engine.** { *; }

# joni / jcodings (TextMate regex engine) load encoding tables reflectively.
-keep class org.joni.** { *; }
-keep class org.jcodings.** { *; }
