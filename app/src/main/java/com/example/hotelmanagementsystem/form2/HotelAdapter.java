package com.example.hotelmanagementsystem.form2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    private final List<Hotel> hotels;
    private final OnHotelClickListener listener;
    private int selectedPosition = -1; // vị trí đang chọn

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public HotelAdapter(List<Hotel> hotels, OnHotelClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.textView.setText(hotel.toString());

        // SỬA CHÍNH XÁC THEO YÊU CẦU CỦA GOOGLE
        int adapterPosition = holder.getAdapterPosition();
        boolean isSelected = (adapterPosition == selectedPosition);

        if (isSelected) {
            holder.itemView.setBackgroundColor(0xFFCCE5FF); // xanh nhạt
        } else {
            holder.itemView.setBackgroundColor(0x00000000); // trong suốt
        }

        // Xử lý click – dùng getAdapterPosition() để an toàn tuyệt đối
        holder.itemView.setOnClickListener(v -> {
            int clickedPos = holder.getAdapterPosition();
            if (clickedPos != RecyclerView.NO_POSITION) { // tránh lỗi khi item bị xóa
                selectedPosition = clickedPos;
                notifyDataSetChanged(); // cập nhật lại toàn bộ để đổi màu
                listener.onHotelClick(hotels.get(clickedPos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotels != null ? hotels.size() : 0;
    }

    // Optional: Nếu bạn muốn reset chọn từ bên ngoài
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}