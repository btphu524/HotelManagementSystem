package com.example.hotelmanagementsystem.form2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagementsystem.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<HotelService> services;
    private OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    public ServiceAdapter(List<HotelService> services, OnSelectionChangedListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HotelService service = services.get(position);
        holder.tvName.setText(service.serviceName);
        holder.tvPrice.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(service.cost));
        holder.checkBox.setChecked(service.isSelected());
        holder.checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
            service.setSelected(isChecked);
            listener.onSelectionChanged();
        });
    }

    public List<Integer> getSelectedServiceIds() {
        List<Integer> ids = new ArrayList<>();
        for (HotelService s : services) if (s.isSelected()) ids.add(s.serviceId);
        return ids;
    }

    @Override public int getItemCount() { return services.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        CheckBox checkBox;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvServiceName);
            tvPrice = v.findViewById(R.id.tvServicePrice);
            checkBox = v.findViewById(R.id.cbSelectService);
        }
    }
}
