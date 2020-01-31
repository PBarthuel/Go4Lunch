package paul.barthuel.go4lunch;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import paul.barthuel.go4lunch.ui.list_view.ListViewFragment;
import paul.barthuel.go4lunch.ui.map_view.LocalisationFragment;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesFragment;

public class SearchRestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        if(savedInstanceState == null) {
            displayedfragment(0);
        }

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_localisation:
                        displayedfragment(0);
                        break;
                    case R.id.navigation_list_view:
                        displayedfragment(1);
                        break;
                    case R.id.navigation_workmates:
                        displayedfragment(2);
                        break;
                }
                return true;
            }
        });
    }

    private void displayedfragment(int fragmentNumber) {
        Fragment fragment;
        switch (fragmentNumber) {
            case 0:
                fragment = LocalisationFragment.newInstance();
                break;
            case 1:
                fragment = ListViewFragment.newInstance();
                break;
            case 2:
                fragment = WorkmatesFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("Incorrect fragment number : " + fragmentNumber);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
