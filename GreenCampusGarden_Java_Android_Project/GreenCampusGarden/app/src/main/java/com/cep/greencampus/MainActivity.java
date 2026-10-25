package com.cep.greencampus;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root;
    private JSONArray plants;
    private final int green = Color.rgb(30, 110, 65);

    @Override public void onCreate(Bundle state) { super.onCreate(state); load(); dashboard(); }
    private void load() {
        try { plants = new JSONArray(getPreferences(0).getString("plants", "")); }
        catch (Exception e) { plants = new JSONArray(); }
        if (plants.length() == 0 && !getPreferences(0).getBoolean("initialized", false)) {
            addPlant("Tulsi", "Ocimum tenuiflorum", "Garden Zone A", "Volunteer 1");
            addPlant("Neem", "Azadirachta indica", "Garden Zone B", "Volunteer 2");
            getPreferences(0).edit().putBoolean("initialized", true).apply();
        }
    }
    private void save() { getPreferences(0).edit().putString("plants", plants.toString()).apply(); }
    private void addPlant(String name, String species, String location, String volunteer) {
        try {
            JSONObject p = new JSONObject();
            p.put("id", "GCG-" + String.format(Locale.US, "%03d", plants.length() + 1));
            p.put("name", name); p.put("species", species); p.put("location", location);
            p.put("volunteer", volunteer); p.put("lastWatered", "Not recorded");
            plants.put(p); save();
        } catch (Exception ignored) {}
    }
    private TextView text(String s, int size, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.rgb(33, 53, 42));
        if (bold) t.setTypeface(null, Typeface.BOLD); t.setPadding(0, 12, 0, 12); return t;
    }
    private void base(String heading) {
        ScrollView scroll = new ScrollView(this); root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 28, 30, 36); root.setBackgroundColor(Color.rgb(244, 250, 245));
        scroll.addView(root); setContentView(scroll); root.addView(text("🌿 " + heading, 25, true));
    }
    private void button(String title, Runnable click) {
        Button b = new Button(this); b.setText(title); b.setAllCaps(false); b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.topMargin = 10;
        root.addView(b, lp); b.setOnClickListener(v -> click.run());
    }
    private void dashboard() {
        base("Green Campus Garden"); root.addView(text("SE SPPU • Community Engagement Project", 15, false));
        root.addView(text("Total registered plants: " + plants.length(), 21, true));
        int watered = 0; for (int i=0;i<plants.length();i++) if (!plants.optJSONObject(i).optString("lastWatered").equals("Not recorded")) watered++;
        root.addView(text("Plants with watering records: " + watered, 17, false));
        button("🌱 View All Plants", this::plantList);
        button("➕ Register New Plant", this::register);
        button("🔎 Find Plant by QR / ID", this::findPlant);
        root.addView(text("Tip: Print each plant's ID as a QR code. Scanning it with a QR reader reveals the ID, which can be entered in the app.", 14, false));
        root.addView(text("Records are stored locally on this device. This version does not synchronize between phones.", 13, false));
    }
    private void plantList() {
        base("Plant Register"); button("← Dashboard", this::dashboard);
        if (plants.length() == 0) root.addView(text("No plants registered yet.", 16, false));
        for (int i=0;i<plants.length();i++) {
            JSONObject p = plants.optJSONObject(i); if (p == null) continue;
            root.addView(text("🌿 " + p.optString("name") + " • " + p.optString("id"), 19, true));
            final int index = i; button("View Details", () -> detail(index));
        }
    }
    private void detail(int index) {
        JSONObject p = plants.optJSONObject(index); if (p == null) return;
        base(p.optString("name")); button("← All Plants", this::plantList);
        root.addView(text("Plant ID: " + p.optString("id"), 18, true));
        root.addView(text("Scientific name: " + p.optString("species"), 16, false));
        root.addView(text("Location: " + p.optString("location"), 16, false));
        root.addView(text("Adopted by: " + p.optString("volunteer"), 16, false));
        root.addView(text("Last watered: " + p.optString("lastWatered"), 16, false));
        button("💧 Mark as Watered", () -> {
            try { p.put("lastWatered", new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date())); save(); detail(index); }
            catch (Exception ignored) {}
        });
    }
    private EditText field(String hint) {
        EditText e = new EditText(this); e.setHint(hint); e.setSingleLine(true);
        e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        root.addView(e); return e;
    }
    private void register() {
        base("Register a Plant"); button("← Dashboard", this::dashboard);
        EditText name = field("Plant name (required)"); EditText species = field("Scientific name");
        EditText location = field("Garden location"); EditText volunteer = field("Student / volunteer name");
        button("Save Plant", () -> {
            if (name.getText().toString().trim().isEmpty()) { name.setError("Enter plant name"); return; }
            addPlant(name.getText().toString().trim(), species.getText().toString().trim(), location.getText().toString().trim(), volunteer.getText().toString().trim());
            Toast.makeText(this, "Plant saved successfully", Toast.LENGTH_SHORT).show(); plantList();
        });
    }
    private void findPlant() {
        base("Find Plant by ID"); button("← Dashboard", this::dashboard);
        root.addView(text("Enter the ID shown after scanning a plant's QR code (example: GCG-001).", 15, false));
        EditText id = field("Plant ID");
        button("Search Plant", () -> {
            for (int i=0;i<plants.length();i++) {
                JSONObject p = plants.optJSONObject(i);
                if (p != null && p.optString("id").equalsIgnoreCase(id.getText().toString().trim())) { detail(i); return; }
            }
            new AlertDialog.Builder(this).setMessage("No plant found with this ID.").setPositiveButton("OK", null).show();
        });
    }
}
