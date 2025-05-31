package com.example.hotelroomapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.*;
import androidx.recyclerview.widget.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RoomAdapter adapter;
    private AppDatabase db;
    private List<RoomEntity> roomList;
    private LinearLayout filterForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().post(this::showUserGuidePopup);

        db = AppDatabase.getInstance(this);

        RecyclerView recyclerView = findViewById(R.id.roomRecyclerView);
        Button addButton = findViewById(R.id.addButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomList = db.roomDao().getAllRooms();
        adapter = new RoomAdapter(roomList,
                this::showDeleteDialog,
                room -> {
                    Intent intent = new Intent(MainActivity.this, EditRoomActivity.class);
                    intent.putExtra("roomId", room.getNumber());
                    startActivity(intent);
                }
        );
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRoomActivity.class);
            startActivity(intent);
        });

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch availableOnlySwitch = findViewById(R.id.availableOnlySwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch petsSwitch = findViewById(R.id.petsSwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch smokingSwitch = findViewById(R.id.smokingSwitch);
        EditText minPriceInput = findViewById(R.id.minPriceInput);
        EditText maxPriceInput = findViewById(R.id.maxPriceInput);
        Button applyFilterButton = findViewById(R.id.applyFilterButton);

        applyFilterButton.setOnClickListener(v -> {
            double minPrice = 0;
            double maxPrice = Double.MAX_VALUE;

            try {
                if (!minPriceInput.getText().toString().isEmpty()) {
                    minPrice = Double.parseDouble(minPriceInput.getText().toString());
                }
                if (!maxPriceInput.getText().toString().isEmpty()) {
                    maxPrice = Double.parseDouble(maxPriceInput.getText().toString());
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price input", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean availableOnly = availableOnlySwitch.isChecked();
            boolean petsAllowed = petsSwitch.isChecked();
            boolean smokingAllowed = smokingSwitch.isChecked();

            List<RoomEntity> filteredRooms = db.roomDao().filterRooms(
                    minPrice,
                    maxPrice,
                    availableOnly,
                    petsAllowed,
                    smokingAllowed
            );
            adapter.setRooms(filteredRooms);
        });

        TextView filterToggle = findViewById(R.id.filterToggle);
        filterForm = findViewById(R.id.filterForm);
        Button clearFiltersButton = findViewById(R.id.clearFiltersButton);

        filterToggle.setOnClickListener(v -> {
            if (filterForm.getVisibility() == View.GONE) {
                filterForm.setVisibility(View.VISIBLE);
                clearFiltersButton.setVisibility(View.VISIBLE); // ✅ show
            } else {
                filterForm.setVisibility(View.GONE);
                clearFiltersButton.setVisibility(View.GONE);    // ✅ hide
            }
        });

        clearFiltersButton.setOnClickListener(v -> {
            minPriceInput.setText("");
            maxPriceInput.setText("");

            availableOnlySwitch.setChecked(false);
            petsSwitch.setChecked(false);
            smokingSwitch.setChecked(false);

            refreshRoomList();

            filterForm.setVisibility(View.GONE);
            TextView emptyMessage = findViewById(R.id.emptyMessage);
            emptyMessage.setVisibility(View.GONE);
        });

        setupFilterLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRoomList();
        TextView emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setVisibility(View.GONE);
    }

    private void refreshRoomList() {
        roomList = db.roomDao().getAllRooms();
        adapter.setRooms(roomList);
    }

    private void showDeleteDialog(RoomEntity room) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to delete room " + room.getNumber() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.roomDao().delete(room);
                    refreshRoomList();
                    Toast.makeText(this, "Room deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUserGuidePopup() {
        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(this).inflate(R.layout.popup_user_guide, null);

        PopupWindow popup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        MediaPlayer mp = MediaPlayer.create(this, R.raw.notification);
        mp.start();

        popupView.findViewById(R.id.closeButton).setOnClickListener(v -> popup.dismiss());

        popup.showAtLocation(findViewById(R.id.rootLayout), Gravity.CENTER, 0, 0);

        new Handler().postDelayed(popup::dismiss, 4000);
    }

    private void setupFilterLogic() {
        EditText minPriceInput = findViewById(R.id.minPriceInput);
        EditText maxPriceInput = findViewById(R.id.maxPriceInput);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch availableOnlySwitch = findViewById(R.id.availableOnlySwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch petsSwitch = findViewById(R.id.petsSwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch smokingSwitch = findViewById(R.id.smokingSwitch);
        Button applyFilterButton = findViewById(R.id.applyFilterButton);

        applyFilterButton.setOnClickListener(v -> {
            String minStr = minPriceInput.getText().toString().trim();
            String maxStr = maxPriceInput.getText().toString().trim();

            Double minPrice = null, maxPrice = null;

            if (!minStr.isEmpty()) {
                try {
                    minPrice = Double.parseDouble(minStr);
                    if (minPrice < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Min price must be a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!maxStr.isEmpty()) {
                try {
                    maxPrice = Double.parseDouble(maxStr);
                    if (maxPrice < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Max price must be a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean availableOnly = availableOnlySwitch.isChecked();
            boolean pets = petsSwitch.isChecked();
            boolean smoking = smokingSwitch.isChecked();

            List<RoomEntity> allRooms = db.roomDao().getAllRooms();
            List<RoomEntity> filteredRooms = new ArrayList<>();

            for (RoomEntity room : allRooms) {
                if (minPrice != null && room.getPrice() < minPrice) continue;
                if (maxPrice != null && room.getPrice() > maxPrice) continue;
                if (availableOnly && !room.isAvailable()) continue;
                if (pets && !room.isAllowsPets()) continue;
                if (smoking && !room.isSmokingAllowed()) continue;
                filteredRooms.add(room);
            }

            TextView emptyMessage = findViewById(R.id.emptyMessage);

            filterForm.setVisibility(View.GONE);
            adapter.setRooms(filteredRooms);
            emptyMessage.setVisibility(filteredRooms.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}

