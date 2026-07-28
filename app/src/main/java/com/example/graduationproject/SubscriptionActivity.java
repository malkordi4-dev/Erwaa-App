package com.example.graduationproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;

public class SubscriptionActivity extends AppCompatActivity {
    private ImageView btnBack;
    private CardView btnConfirmSubscription;
    private TextView tvTotalPriceBottom, tvTotalPriceCard;

    // باقات الاشتراك (CardViews)
    private CardView cardBasicPlan, cardStandardPlan, cardFamilyPlan;
    private int selectedPlanPrice = 45; // القيمة الافتراضية للباقة الأساسية

    // أيام التوصيل (قائمة لتخزين الأيام المحددة)
    private final ArrayList<String> selectedDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_subscription);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnConfirmSubscription = findViewById(R.id.btnConfirmSubscription);

        // سنحتاج لإضافة معرّفات (IDs) في الـ XML لهذه النصوص لتحديث السعر ديناميكياً
        // إذا لم تضفها بعد، يمكنك ربطها ميثودياً أو إضافتها لاحقاً
        // هنا فرضنا معرّفات افتراضية لتوضيح المنطق البرمجي
    }

    private void setupClickListeners() {
        // زر العودة للخلف
        btnBack.setOnClickListener(v -> finish());

        // زر تأكيد الاشتراك في أسفل الشاشة
        btnConfirmSubscription.setOnClickListener(v -> showSubscriptionSuccessDialog());
    }

    /**
     * ميثود للتحكم عند اختيار باقة محددة
     * مبرمجة لترتبط مع خاصية android:onClick في الـ XML
     */
    public void onPlanSelected(View view) {
        // يمكنك إعطاء الـ Cards معرفات مثل plan_basic, plan_standard, plan_family
        int id = view.getId();

        // محاكاة لتحديث الأسعار بناءً على الاختيار
        if (id == R.id.title1) { // الباقة الأساسية
            selectedPlanPrice = 45;
            Toast.makeText(this, "تم اختيار الباقة الأساسية", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.title2) { // الباقة القياسية
            selectedPlanPrice = 90;
            Toast.makeText(this, "تم اختيار الباقة القياسية", Toast.LENGTH_SHORT).show();
        } else { // الباقة العائلية
            selectedPlanPrice = 160;
            Toast.makeText(this, "تم اختيار الباقة العائلية", Toast.LENGTH_SHORT).show();
        }

        updateUiPrices();
    }

    /**
     * ميثود تفاعلية للتحكم باختيار أيام التوصيل المفضلة
     */
    public void onDaySelected(View view) {
        CardView dayCard = (CardView) view;
        // الحصول على النص داخل الكارد (السبت، الأحد، إلخ)
        TextView dayText = (TextView) dayCard.getChildAt(0);
        String day = dayText.getText().toString();

        if (selectedDays.contains(day)) {
            // إزالة اليوم من المفضلة وإعادة لون الخلفية للأبيض
            selectedDays.remove(day);
            dayCard.setCardBackgroundColor(0xFFFFFFFF);
            dayText.setTextColor(0xFF64748B);
        } else {
            // إضافة اليوم وتغيير الخلفية للأزرق الداكن
            selectedDays.add(day);
            dayCard.setCardBackgroundColor(0xFF0D63B3);
            dayText.setTextColor(0xFFFFFFFF);
        }
    }

    private void updateUiPrices() {
        // هنا يتم تحديث النصوص البرمجية للأسعار عند تغيير الباقة
        String priceText = selectedPlanPrice + ".00 ₪";
         tvTotalPriceBottom.setText(priceText);
         tvTotalPriceCard.setText(priceText);
    }

    private void showSubscriptionSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد العملية")
                .setMessage("هل ترغب في تأكيد الاشتراك بالخطة المختارة وجدولة المواعيد؟")
                .setPositiveButton("تأكيد ودفع", (dialog, which) -> {
                    Toast.makeText(this, "🎉 تم تفعيل اشتراكك الشهري بنجاح!", Toast.LENGTH_LONG).show();
                    // الانتقال إلى مركز تنبيهات المستفيد لمتابعة حالة الخصم والجدولة
                    Intent intent = new Intent(SubscriptionActivity.this, UserNotificationActivity.class);
                    // تصفير شاشات الخلفية لتبدأ الدورة من جديد
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("تعديل", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
