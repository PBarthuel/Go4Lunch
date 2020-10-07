package paul.barthuel.go4lunch.ui.search_restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import paul.barthuel.go4lunch.MainActivity;
import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.injections.ViewModelFactory;
import paul.barthuel.go4lunch.ui.list_view.ListViewFragment;
import paul.barthuel.go4lunch.ui.map_view.LocalisationFragment;
import paul.barthuel.go4lunch.ui.notification.NotificationActivity;
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailActivity;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesFragment;

public class SearchRestaurantActivity extends AppCompatActivity {

    private SearchRestaurantViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);

        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(SearchRestaurantViewModel.class);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);

        RecyclerView autocompleteRecyclerView = findViewById(R.id.search_restaurant_autocomplete_rv);

        AutocompleteRestaurantAdapter autocompleteRestaurantAdapter = new AutocompleteRestaurantAdapter();

        autocompleteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        autocompleteRecyclerView.setAdapter(autocompleteRestaurantAdapter);

        mViewModel.getPredictionMediatorLiveData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> restaurantsAutocompleteNames) {
                autocompleteRestaurantAdapter.submitList(restaurantsAutocompleteNames);
            }
        });

        Toolbar toolbar = findViewById(R.id.search_restaurant_toolbar);
        toolbar.setTitle("Map");
        NavigationView navigationView = findViewById(R.id.search_restaurant_navigation_view_menu);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,
                        MainActivity.class));
                finish();
            }
            if (menuItem.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this,
                        NotificationActivity.class));
            }
            if (menuItem.getItemId() == R.id.nav_your_lunch) {
                mViewModel.getCurrentTodayUserLiveData().observe(this, todayUser -> startActivity(RestaurantDetailActivity.navigate(SearchRestaurantActivity.this, todayUser.getPlaceId(), todayUser.getRestaurantName())));
            }
            return false;
        });

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.search_restaurant_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            displayedFragment(0);
        }

        View drawerHeadView = navigationView.getHeaderView(0);
        ImageView profilImageView = drawerHeadView.findViewById(R.id.head_drawer_profil_pic_iv);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Glide.with(profilImageView)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilImageView);
        }
        TextView userNameTextView = drawerHeadView.findViewById(R.id.head_drawer_user_name_tv);
        TextView emailAddressTextView = drawerHeadView.findViewById(R.id.head_drawer_email_tv);
        emailAddressTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userNameTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        bottomNavView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_localisation:
                    displayedFragment(0);
                    toolbar.setTitle("Map");
                    break;
                case R.id.navigation_list_view:
                    displayedFragment(1);
                    toolbar.setTitle("I'm Hungry!");
                    break;
                case R.id.navigation_workmates:
                    displayedFragment(2);
                    toolbar.setTitle("Available workmates");
                    break;
            }
            return true;
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
        //TODO faire la searchView ici
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_activity_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mViewModel.onSearchQueryChange(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
