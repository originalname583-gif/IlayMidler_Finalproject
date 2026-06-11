package com.alma.ilaymidler_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.adapters.CourtAdapter;
import com.alma.ilaymidler_finalproject.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UserPage extends BaseMenuActivity {

    private Spinner spinnerCity;
    // Spinner שבו המשתמש בוחר עיר.

    private RecyclerView rvCourts;
    // רשימה שמציגה את המגרשים בעיר שנבחרה.

    private TextView tvEmpty;
    // טקסט שמוצג אם אין מגרשים או אם צריך לבחור עיר.

    private ProgressBar progressBar;
    // סימן טעינה בזמן שמביאים מגרשים מ-Firebase.

    private final List<Court> allCourts = new ArrayList<>();
    // רשימה ששומרת את כל המגרשים שהגיעו מ-Firebase.

    private CourtAdapter courtAdapter;
    // Adapter שמציג את המגרשים ברשימה.

    private DatabaseService databaseService;
    // השירות שאחראי על פעולות מול Firebase.

    private boolean loadingInProgress = false;
    // מונע טעינה כפולה של מגרשים באותו זמן.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // מפעיל את onCreate של המחלקה האב.

        setContentView(R.layout.activity_user_page);
        // טוען את העיצוב של מסך המשתמש.

        setupToolbar(R.id.topToolbar, "User Page");
        // מגדיר Toolbar עם הכותרת User Page.

        spinnerCity = findViewById(R.id.spinnerCity);
        // מחבר את Spinner הערים מה-XML לקוד.

        rvCourts = findViewById(R.id.rvCourts);
        // מחבר את RecyclerView מה-XML לקוד.

        tvEmpty = findViewById(R.id.tvEmpty);
        // מחבר את טקסט ההודעה מה-XML לקוד.

        progressBar = findViewById(R.id.progressBar);
        // מחבר את ProgressBar מה-XML לקוד.

        databaseService = DatabaseService.getInstance();
        // מקבל את DatabaseService כדי לעבוד מול Firebase.

        rvCourts.setLayoutManager(new LinearLayoutManager(this));
        // מגדיר שהמגרשים יוצגו כרשימה אנכית.

        courtAdapter = new CourtAdapter(new ArrayList<>(), court -> {
            // יוצר Adapter ומגדיר מה קורה כשלוחצים על מגרש.

            if (court == null || court.getId().isEmpty()) {
                Toast.makeText(UserPage.this, "Court not found", Toast.LENGTH_SHORT).show();
                return;
                // אם המגרש לא תקין, לא עוברים למסך הבא.
            }

            Intent intent = new Intent(UserPage.this, CourtDetailsActivity.class);
            // יוצר מעבר למסך פרטי מגרש.

            intent.putExtra("COURT_ID", court.getId());
            // שולח למסך הבא את מזהה המגרש.

            startActivity(intent);
            // פותח את מסך פרטי המגרש.
        });

        rvCourts.setAdapter(courtAdapter);
        // מחבר את ה-Adapter לרשימה.

        setupSpinner();
        // מכין את רשימת הערים.

        checkLoginAndLoadCourts();
        // בודק שהמשתמש מחובר ואז טוען מגרשים.
    }

    private void checkLoginAndLoadCourts() {
        // הפונקציה בודקת אם המשתמש מחובר לפני טעינת המגרשים.

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // מקבל את המשתמש שמחובר כרגע.

        if (firebaseUser == null) {
            Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            // אם אין משתמש מחובר, מציג הודעה.

            startActivity(new Intent(UserPage.this, Login.class));
            // מעביר למסך התחברות.

            finish();
            // סוגר את מסך המשתמש.

            return;
            // עוצר את הפונקציה.
        }

        loadCourts();
        // אם המשתמש מחובר, טוען את המגרשים.
    }

    private void setupSpinner() {
        // הפונקציה מכינה את Spinner הערים.

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.arlocation,
                android.R.layout.simple_spinner_item
        );
        // יוצר Adapter לערים מתוך strings.xml.

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // מגדיר איך הרשימה תיראה כאשר היא נפתחת.

        spinnerCity.setAdapter(adapter);
        // מחבר את רשימת הערים ל-Spinner.

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // מאזין לבחירת עיר.

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // מופעל כאשר המשתמש בוחר עיר.

                String selectedCity = parent.getItemAtPosition(position) != null
                        ? parent.getItemAtPosition(position).toString()
                        : "";
                // לוקח את העיר שנבחרה בצורה בטוחה.

                filterCourtsByCity(selectedCity);
                // מסנן את המגרשים לפי העיר שנבחרה.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // מופעל אם לא נבחר כלום.

                rvCourts.setVisibility(View.GONE);
                // מסתיר את רשימת המגרשים.

                tvEmpty.setVisibility(View.VISIBLE);
                // מציג הודעה.

                tvEmpty.setText("Choose a city to view courts");
                // מבקש מהמשתמש לבחור עיר.
            }
        });
    }

    private void loadCourts() {
        // הפונקציה טוענת את כל המגרשים מ-Firebase.

        if (loadingInProgress) {
            return;
            // אם כבר מתבצעת טעינה, לא מתחילים טעינה נוספת.
        }

        loadingInProgress = true;
        // מסמן שהתחילה טעינה.

        progressBar.setVisibility(View.VISIBLE);
        // מציג טעינה.

        tvEmpty.setVisibility(View.GONE);
        // מסתיר הודעות בזמן טעינה.

        rvCourts.setVisibility(View.GONE);
        // מסתיר את הרשימה בזמן טעינה.

        databaseService.getCourtsList(new DatabaseService.DatabaseCallback<List<Court>>() {
            // מביא את כל המגרשים מ-Firebase.

            @Override
            public void onCompleted(List<Court> courts) {
                loadingInProgress = false;
                // מסמן שהטעינה הסתיימה.

                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                allCourts.clear();
                // מנקה את הרשימה הישנה.

                if (courts != null) {
                    allCourts.addAll(courts);
                    // מוסיף את כל המגרשים שהגיעו מ-Firebase.
                }

                if (allCourts.isEmpty()) {
                    rvCourts.setVisibility(View.GONE);
                    // מסתיר את הרשימה.

                    tvEmpty.setVisibility(View.VISIBLE);
                    // מציג הודעה.

                    tvEmpty.setText("No courts were added yet.");
                    // אומר שאין עדיין מגרשים במערכת.

                    courtAdapter.updateList(new ArrayList<>());
                    // מנקה את הרשימה במסך.

                    return;
                    // עוצר את הפונקציה.
                }

                Object selectedItem = spinnerCity.getSelectedItem();
                // לוקח את העיר הנבחרת כרגע.

                String selectedCity = selectedItem != null ? selectedItem.toString() : "";
                // ממיר את העיר לטקסט בצורה בטוחה.

                filterCourtsByCity(selectedCity);
                // מציג רק את המגרשים בעיר שנבחרה.
            }

            @Override
            public void onFailed(Exception e) {
                loadingInProgress = false;
                // מסמן שהטעינה הסתיימה גם אם נכשלה.

                progressBar.setVisibility(View.GONE);
                // מסתיר טעינה.

                rvCourts.setVisibility(View.GONE);
                // מסתיר את הרשימה.

                tvEmpty.setVisibility(View.VISIBLE);
                // מציג הודעת שגיאה.

                tvEmpty.setText("Something went wrong while loading courts.");
                // שם טקסט שגיאה.
            }
        });
    }

    private void filterCourtsByCity(String city) {
        // הפונקציה מסננת את רשימת המגרשים לפי העיר שנבחרה.

        List<Court> filtered = new ArrayList<>();
        // רשימה חדשה שתכיל רק את המגרשים בעיר שנבחרה.

        if (city == null || city.trim().isEmpty() || city.equalsIgnoreCase("Choose city")) {
            courtAdapter.updateList(filtered);
            // מנקה את הרשימה.

            rvCourts.setVisibility(View.GONE);
            // מסתיר את המגרשים.

            tvEmpty.setVisibility(View.VISIBLE);
            // מציג הודעה.

            tvEmpty.setText("Choose a city to view courts");
            // מבקש לבחור עיר.

            return;
            // עוצר את הפונקציה.
        }

        for (Court court : allCourts) {
            // עובר על כל המגרשים שהגיעו מ-Firebase.

            if (court == null) {
                continue;
                // אם מגרש לא תקין, מדלגים עליו.
            }

            if (court.getCity().trim().equalsIgnoreCase(city.trim())) {
                filtered.add(court);
                // אם העיר של המגרש שווה לעיר שנבחרה, מוסיפים לרשימה.
            }
        }

        courtAdapter.updateList(filtered);
        // מעדכן את הרשימה במסך עם המגרשים המסוננים.

        if (filtered.isEmpty()) {
            rvCourts.setVisibility(View.GONE);
            // אם אין מגרשים בעיר הזאת, מסתירים את הרשימה.

            tvEmpty.setVisibility(View.VISIBLE);
            // מציגים הודעה.

            tvEmpty.setText("No courts are available in this city.");
            // מראים שאין מגרשים בעיר שנבחרה.

        } else {
            rvCourts.setVisibility(View.VISIBLE);
            // אם יש מגרשים, מציגים את הרשימה.

            tvEmpty.setVisibility(View.GONE);
            // מסתירים את הודעת הריק.
        }
    }
}