package com.example.graduationproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<NotificationModel> notifications;

    public NotificationAdapter(Context context, List<NotificationModel> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle() != null ? notification.getTitle() : "إشعار جديد");
        holder.tvMessage.setText(notification.getMessage() != null ? notification.getMessage() : "");

        if (notification.getCreated_at() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            holder.tvTime.setText(sdf.format(notification.getCreated_at()));
        } else {
            holder.tvTime.setText("الآن");
        }

        holder.unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        // Customize icon and colors based on type
        String type = notification.getType();
        holder.layoutActions.setVisibility(View.GONE); // Default

        if ("new_order".equals(type)) {
            holder.ivIcon.setImageResource(R.drawable.drop);
            holder.ivIcon.setColorFilter(Color.parseColor("#0069B4"));
            holder.iconContainer.setCardBackgroundColor(Color.parseColor("#67E8F9"));
            
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnPrimary.setText("تتبع السائق 📍");
            holder.btnSecondary.setText("تفاصيل الطلب");
            
            holder.btnPrimary.setOnClickListener(v -> {
                if (context instanceof UserNotificationActivity) {
                    ((UserNotificationActivity) context).onTrackDriverClicked(v);
                }
            });
            holder.btnSecondary.setOnClickListener(v -> {
                if (context instanceof UserNotificationActivity) {
                    ((UserNotificationActivity) context).onViewOrderDetailsClicked(v);
                }
            });
        } else if ("payment".equals(type)) {
            holder.ivIcon.setImageResource(R.drawable.payment);
            holder.ivIcon.setColorFilter(Color.parseColor("#475569"));
            holder.iconContainer.setCardBackgroundColor(Color.parseColor("#E2E8F0"));
        } else if ("rating".equals(type)) {
            holder.ivIcon.setImageResource(R.drawable.star);
            holder.ivIcon.setColorFilter(Color.parseColor("#0069B4"));
            holder.iconContainer.setCardBackgroundColor(Color.parseColor("#E2E8F0"));
        } else {
            holder.ivIcon.setImageResource(R.drawable.notification);
            holder.ivIcon.setColorFilter(Color.parseColor("#475569"));
            holder.iconContainer.setCardBackgroundColor(Color.parseColor("#E2E8F0"));
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivIcon;
        View unreadIndicator, layoutActions;
        MaterialCardView iconContainer;
        MaterialButton btnPrimary, btnSecondary;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnPrimary = itemView.findViewById(R.id.btnPrimary);
            btnSecondary = itemView.findViewById(R.id.btnSecondary);
        }
    }
}
