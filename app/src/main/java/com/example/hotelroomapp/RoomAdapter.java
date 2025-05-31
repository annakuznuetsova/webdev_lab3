package com.example.hotelroomapp;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<RoomEntity> rooms;
    private final OnRoomLongClickListener longClickListener;

    private final OnRoomClickListener clickListener;

    public RoomAdapter(List<RoomEntity> roomList, OnRoomLongClickListener longClickListener, OnRoomClickListener clickListener) {
        this.rooms = roomList;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }


    public void setRooms(List<RoomEntity> newRooms) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return rooms.size();
            }

            @Override
            public int getNewListSize() {
                return newRooms.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return rooms.get(oldItemPosition).getNumber() == newRooms.get(newItemPosition).getNumber();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return rooms.get(oldItemPosition).equals(newRooms.get(newItemPosition));
            }
        });

        this.rooms = newRooms;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomEntity room = rooms.get(position);
        holder.roomNumber.setText(
                holder.itemView.getContext().getString(R.string.room_number, room.number)
        );
        holder.roomType.setText(
                holder.itemView.getContext().getString(R.string.room_type, room.type)
        );
        holder.roomPrice.setText(
                holder.itemView.getContext().getString(R.string.room_price, room.price)
        );
        holder.roomAvailability.setText(
                holder.itemView.getContext().getString(
                        room.available ? R.string.room_available : R.string.room_booked
                )
        );

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onRoomLongClick(room);
            return true;
        });

        holder.itemView.setOnClickListener(v -> clickListener.onRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return rooms != null ? rooms.size() : 0;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNumber, roomType, roomPrice, roomAvailability;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.roomNumberText);
            roomType = itemView.findViewById(R.id.roomTypeText);
            roomPrice = itemView.findViewById(R.id.roomPriceText);
            roomAvailability = itemView.findViewById(R.id.roomAvailabilityText);
        }
    }

    public interface OnRoomLongClickListener {
        void onRoomLongClick(RoomEntity room);
    }

    public interface OnRoomClickListener {
        void onRoomClick(RoomEntity room);
    }
}
