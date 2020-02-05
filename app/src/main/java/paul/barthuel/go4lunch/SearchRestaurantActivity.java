package paul.barthuel.go4lunch;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

        Toolbar toolbar = findViewById(R.id.search_restaurant_toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.search_restaurant_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            displayedFragment(0);
        }

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_localisation:
                        displayedFragment(0);
                        break;
                    case R.id.navigation_list_view:
                        displayedFragment(1);
                        break;
                    case R.id.navigation_workmates:
                        displayedFragment(2);
                        break;
                }
                return true;
            }
        });
    }

    private void displayedFragment(int fragmentNumber) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_activity_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.search_restaurant_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
