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

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> rooms;
    private OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    public RoomAdapter(List<Room> rooms, OnSelectionChangedListener listener) {
        this.rooms = rooms;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.tvRoomInfo.setText("Phòng " + room.roomNumber + " - " + room.typeName);

        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        if (room.discountRate > 0) {
            holder.tvPrice.setText(fmt.format(room.originalPrice) + " → " + fmt.format(room.getFinalPrice()) + " (-" + (int)room.discountRate + "%)");
        } else {
            holder.tvPrice.setText(fmt.format(room.getFinalPrice()));
        }

        // BỎ listener cũ đi, thay bằng cái này (tránh lỗi layout)
        holder.checkBox.setOnCheckedChangeListener(null);  // quan trọng!
        holder.checkBox.setChecked(room.isSelected());
        holder.checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
            room.setSelected(isChecked);
            if (listener != null) listener.onSelectionChanged();
        });
    }

    public List<Integer> getSelectedRoomIds() {
        List<Integer> ids = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isSelected()) ids.add(r.roomId);
        }
        return ids;
    }

    @Override public int getItemCount() { return rooms.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomInfo, tvPrice;
        CheckBox checkBox;
        ViewHolder(View v) {
            super(v);
            tvRoomInfo = v.findViewById(R.id.tvRoomInfo);
            tvPrice = v.findViewById(R.id.tvPrice);
            checkBox = v.findViewById(R.id.cbSelectRoom);
        }
    }
}