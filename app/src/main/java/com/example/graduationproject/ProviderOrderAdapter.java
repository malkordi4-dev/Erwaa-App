package com.example.graduationproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProviderOrderAdapter extends RecyclerView.Adapter<ProviderOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> orders;
    private String currentTab; // "new", "in_progress", "completed"
    private FirebaseFirestore db;

    public ProviderOrderAdapter(Context context, List<OrderModel> orders, String currentTab) {
        this.context = context;
        this.orders = orders;
        this.currentTab = currentTab;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_provider_order_action, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);

        String displayId = order.getCustomerId() != null && order.getCustomerId().length() > 5 
                ? order.getCustomerId().substring(0, 5) 
                : "غير معروف";
        
        holder.tvClientName.setText("عميل رقم: " + displayId);
        holder.tvClientAddress.setText(order.getAddressDetails() != null ? order.getAddressDetails() : "غزة");
        holder.tvOrderQuantity.setText(order.getQuantity() + " " + (order.getUnit() != null ? order.getUnit() : "لتر"));
        holder.tvScheduledTime.setText(order.getScheduledTime() != null ? order.getScheduledTime() : "الآن");

        if ("new".equals(currentTab)) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setVisibility(View.GONE);
        } else if ("in_progress".equals(currentTab)) {
            holder.layoutActions.setVisibility(View.GONE);
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            
            String status = order.getStatus();
            if ("accepted".equals(status)) {
                holder.btnUpdateStatus.setText("بدء التوصيل (في الطريق)");
                holder.btnUpdateStatus.setBackgroundColor(Color.parseColor("#3B82F6"));
            } else if ("on_way".equals(status)) {
                holder.btnUpdateStatus.setText("تأكيد التوصيل (تم)");
                holder.btnUpdateStatus.setBackgroundColor(Color.parseColor("#10B981"));
            }
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            holder.btnUpdateStatus.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> updateStatus(order, "accepted", position));
        holder.btnReject.setOnClickListener(v -> updateStatus(order, "cancelled", position));
        
        holder.btnUpdateStatus.setOnClickListener(v -> {
            String nextStatus = "accepted".equals(order.getStatus()) ? "on_way" : "delivered";
            updateStatus(order, nextStatus, position);
        });
    }

    private void updateStatus(OrderModel order, String newStatus, int position) {
        if (order.getId() == null) {
            Toast.makeText(context, "خطأ: معرف الطلب غير موجود", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("orders").document(order.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "تم تحديث حالة الطلب", Toast.LENGTH_SHORT).show();
                    // Update local model status if it's an in-progress update that doesn't remove the item
                    order.setStatus(newStatus);
                    
                    // Logic to remove from current view if it no longer belongs to this tab
                    boolean shouldRemove = false;
                    if ("new".equals(currentTab) && !"pending".equals(newStatus)) shouldRemove = true;
                    if ("in_progress".equals(currentTab) && "delivered".equals(newStatus)) shouldRemove = true;
                    
                    if (shouldRemove) {
                        orders.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        notifyItemChanged(position);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "فشل التحديث في فيربيز", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvClientAddress, tvOrderQuantity, tvScheduledTime;
        MaterialButton btnAccept, btnReject, btnUpdateStatus;
        View layoutActions;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvClientAddress = itemView.findViewById(R.id.tvClientAddress);
            tvOrderQuantity = itemView.findViewById(R.id.tvOrderQuantity);
            tvScheduledTime = itemView.findViewById(R.id.tvScheduledTime);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}
