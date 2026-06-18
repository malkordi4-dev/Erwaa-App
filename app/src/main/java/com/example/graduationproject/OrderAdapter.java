package com.example.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> orders;

    public OrderAdapter(Context context, List<OrderModel> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void updateList(List<OrderModel> newList) {
        this.orders = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_record, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);

        // اسم المزود أو نوع الخدمة
        holder.tvCustomerName.setText(order.getOrderType().equals("monthly") ? "اشتراك شهري" : "طلب مياه عذب");
        
        // رقم الطلب والوقت
        String shortId = order.getId() != null && order.getId().length() > 8 ? order.getId().substring(0, 8) : "---";
        holder.tvOrderMeta.setText("طلب #" + shortId + " • " + order.getScheduledTime());
        
        // السعر والكمية
        holder.tvOrderPrice.setText(String.format("%.2f ₪", order.getTotalPrice()));
        holder.tvWaterAmount.setText(order.getQuantity() + " " + order.getUnit());
        
        // حالة الطلب
        String status = order.getStatus();
        holder.tvStatusText.setText(getStatusArabic(status));
        holder.tvStatusText.setTextColor(getStatusColor(status));

        // معالجة التاريخ العلوي (Section Date)
        if (order.getCreatedAt() != null) {
            holder.tvSectionDate.setText(order.getCreatedAt().split("T")[0]); 
        }

        holder.btnGoToDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, Order_Details_Activity.class);
            intent.putExtra("order_id", order.getId());
            intent.putExtra("status", order.getStatus());
            intent.putExtra("price", order.getTotalPrice());
            intent.putExtra("quantity", order.getQuantity());
            intent.putExtra("unit", order.getUnit());
            intent.putExtra("address", order.getAddressDetails());
            context.startActivity(intent);
        });
    }

    private String getStatusArabic(String status) {
        if (status == null) return "قيد المعالجة";
        switch (status) {
            case "pending": return "قيد الانتظار";
            case "accepted": return "تم القبول";
            case "on_way": return "في الطريق";
            case "delivered": return "تم التوصيل";
            case "cancelled": return "ملغي";
            default: return "نشط";
        }
    }

    private int getStatusColor(String status) {
        if (status == null) return Color.parseColor("#F59E0B");
        switch (status) {
            case "pending": return Color.parseColor("#F59E0B");
            case "accepted": return Color.parseColor("#3B82F6");
            case "on_way": return Color.parseColor("#10B981");
            case "delivered": return Color.parseColor("#15803D");
            case "cancelled": return Color.parseColor("#EF4444");
            default: return Color.GRAY;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvOrderMeta, tvOrderPrice, tvStatusText, tvWaterAmount, tvSectionDate;
        View btnGoToDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderMeta = itemView.findViewById(R.id.tvOrderMeta);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvStatusText = itemView.findViewById(R.id.tvStatusText);
            tvWaterAmount = itemView.findViewById(R.id.tvWaterAmount);
            tvSectionDate = itemView.findViewById(R.id.tvSectionDate);
            btnGoToDetails = itemView.findViewById(R.id.btnGoToDetails);
        }
    }
}
