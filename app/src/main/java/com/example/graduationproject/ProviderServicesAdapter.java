package com.example.graduationproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class ProviderServicesAdapter extends RecyclerView.Adapter<ProviderServicesAdapter.ServiceViewHolder> {

    private List<ServiceModel> services;

    public ProviderServicesAdapter(List<ServiceModel> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel service = services.get(position);
        holder.tvServiceName.setText(service.getNameAr());
        holder.tvServiceDesc.setText(service.getDescriptionAr());
        
        // هنا يمكنك ربط السعر إذا كان موجوداً في الـ Model، حالياً نضع قيم افتراضية
        holder.tvServicePrice.setText("50 NIS"); 
        
        // يمكنك تغيير الأيقونة بناءً على نوع الخدمة
        if (service.getNameAr().contains("صهريج")) {
            holder.imgServiceIcon.setImageResource(R.drawable.drop);
        }
    }

    @Override
    public int getItemCount() {
        return services != null ? services.size() : 0;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServicePrice, tvServiceDesc;
        ImageView imgServiceIcon;
        SwitchMaterial switchStatus;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceDesc = itemView.findViewById(R.id.tvServiceDesc);
            imgServiceIcon = itemView.findViewById(R.id.imgServiceIcon);
            switchStatus = itemView.findViewById(R.id.switchServiceStatus);
        }
    }
}
