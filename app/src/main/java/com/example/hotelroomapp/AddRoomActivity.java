package com.example.hotelroomapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddRoomActivity extends AppCompatActivity {
    private EditText roomNumberInput, roomTypeInput, roomPriceInput;
    private CheckBox availabilityCheckbox, petsCheckbox, smokingCheckbox;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        db = AppDatabase.getInstance(this);

        roomNumberInput = findViewById(R.id.roomNumberInput);
        roomTypeInput = findViewById(R.id.roomTypeInput);
        roomPriceInput = findViewById(R.id.roomPriceInput);
        availabilityCheckbox = findViewById(R.id.availabilityCheckbox);
        petsCheckbox = findViewById(R.id.petsCheckbox);
        smokingCheckbox = findViewById(R.id.smokingCheckbox);
        Button saveRoomButton = findViewById(R.id.saveRoomButton);

        saveRoomButton.setOnClickListener(v -> saveRoom());
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> finish());
    }

    private boolean isValidInput(String numberStr, String type, String priceStr) {
        if (numberStr.trim().isEmpty()) {
            Toast.makeText(this, "Room number is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (type.trim().isEmpty()) {
            Toast.makeText(this, "Room type is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (priceStr.trim().isEmpty()) {
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!numberStr.matches("\\d+") || Integer.parseInt(numberStr) <= 0) {
            Toast.makeText(this, "Room number must be a positive integer", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!type.matches("^[a-zA-Z]+$")) {
            Toast.makeText(this, "Room type must contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                Toast.makeText(this, "Price must be non-negative", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveRoom() {
        String numberStr = roomNumberInput.getText().toString();
        String type = roomTypeInput.getText().toString();
        String priceStr = roomPriceInput.getText().toString();

        if (!isValidInput(numberStr, type, priceStr)) return;

        int number = Integer.parseInt(numberStr);

        if (db.roomDao().findByNumber(number) != null) {
            Toast.makeText(this, "Room number already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        boolean isAvailable = availabilityCheckbox.isChecked();
        boolean allowsPets = petsCheckbox.isChecked();
        boolean smokingAllowed = smokingCheckbox.isChecked();

        RoomEntity room = new RoomEntity(number, type, price, isAvailable, allowsPets, smokingAllowed);
        db.roomDao().insert(room);
        Toast.makeText(this, "Room saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
