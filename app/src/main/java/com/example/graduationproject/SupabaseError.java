package com.example.graduationproject;

import com.google.gson.annotations.SerializedName;

public class SupabaseError {
    @SerializedName("msg")
    private String message;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("message")
    private String messageAlt;

    public String getDisplayMessage() {
        String rawError = "";
        if (message != null) rawError = message;
        else if (errorDescription != null) rawError = errorDescription;
        else if (messageAlt != null) rawError = messageAlt;

        if (rawError.isEmpty()) return "حدث خطأ غير متوقع";

        // تحويل رسائل الخطأ الشائعة من Supabase إلى العربية
        String lowerError = rawError.toLowerCase();

        if (lowerError.contains("user already registered")) {
            return "هذا البريد الإلكتروني مسجل بالفعل";
        } else if (lowerError.contains("password should be at least")) {
            return "يجب أن تكون كلمة المرور 6 أحرف على الأقل";
        } else if (lowerError.contains("invalid login credentials")) {
            return "البريد الإلكتروني أو كلمة المرور غير صحيحة";
        } else if (lowerError.contains("email not confirmed")) {
            return "يرجى تأكيد البريد الإلكتروني أولاً";
        } else if (lowerError.contains("network") || lowerError.contains("timeout")) {
            return "خطأ في الاتصال بالشبكة، تأكد من الإنترنت";
        } else if (lowerError.contains("signup is disabled")) {
            return "التسجيل معطل حالياً من قبل الإدارة";
        } else if (lowerError.contains("rate limit")) {
            return "لقد قمت بمحاولات كثيرة، انتظر قليلاً";
        }

        // إذا لم يكن من الأخطاء المعروفة، اطبع الرسالة الأصلية أو رسالة عامة
        return "خطأ: " + rawError;
    }
}
