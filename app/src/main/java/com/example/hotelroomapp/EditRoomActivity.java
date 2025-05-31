package com.example.hotelroomapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class EditRoomActivity extends AppCompatActivity {
    private EditText numberEdit, typeEdit, priceEdit;
    private CheckBox availableCheck, petsCheck, smokingCheck;
    private AppDatabase db;
    private RoomEntity room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        db = AppDatabase.getInstance(this);

        numberEdit = findViewById(R.id.editNumber);
        typeEdit = findViewById(R.id.editType);
        priceEdit = findViewById(R.id.editPrice);
        availableCheck = findViewById(R.id.editAvailability);
        petsCheck = findViewById(R.id.editPets);
        smokingCheck = findViewById(R.id.editSmoking);
        Button updateButton = findViewById(R.id.updateButton);

        int roomId = getIntent().getIntExtra("roomId", -1);
        room = db.roomDao().findById(roomId);

        if (room != null) {
            numberEdit.setText(String.valueOf(room.getNumber()));
            typeEdit.setText(room.getType());
            priceEdit.setText(String.valueOf(room.getPrice()));
            availableCheck.setChecked(room.available);
            petsCheck.setChecked(room.allowsPets);
            smokingCheck.setChecked(room.smokingAllowed);
        }

        updateButton.setOnClickListener(v -> updateRoom());
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> finish());
    }

    private boolean isValidInput(String numberStr, String type, String priceStr) {
        if (!numberStr.matches("\\d+") || Integer.parseInt(numberStr) <= 0) {
            Toast.makeText(this, "Room number must be a positive integer", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!type.matches("[a-zA-Z ]+")) {
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

    private void updateRoom() {
        String numberStr = numberEdit.getText().toString();
        String type = typeEdit.getText().toString();
        String priceStr = priceEdit.getText().toString();

        if (!isValidInput(numberStr, type, priceStr)) return;

        room.setNumber(Integer.parseInt(numberStr));
        room.setType(type);
        room.setPrice(Double.parseDouble(priceStr));
        room.setAvailable(availableCheck.isChecked());
        room.setAllowsPets(petsCheck.isChecked());
        room.setSmokingAllowed(smokingCheck.isChecked());

        db.roomDao().update(room);
        Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
