package paul.barthuel.go4lunch.ui.search_restaurant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import paul.barthuel.go4lunch.MainActivity
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser
import paul.barthuel.go4lunch.injections.ViewModelFactory
import paul.barthuel.go4lunch.ui.list_view.ListViewFragment
import paul.barthuel.go4lunch.ui.map_view.LocalisationFragment.Companion.newInstance
import paul.barthuel.go4lunch.ui.notification.NotificationActivity
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailActivity
import paul.barthuel.go4lunch.ui.workmates.WorkmatesFragment

class SearchRestaurantActivity : AppCompatActivity(), OnAutocompleteTextListener {
    private var mViewModel: SearchRestaurantViewModel? = null
    private var searchView: SearchView? = null

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_restaurant)
        mViewModel = ViewModelProvider(this, ViewModelFactory.instance).get(SearchRestaurantViewModel::class.java)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val autocompleteRecyclerView = findViewById<RecyclerView>(R.id.search_restaurant_autocomplete_rv)
        val autocompleteRestaurantAdapter = AutocompleteRestaurantAdapter(this)
        autocompleteRecyclerView.layoutManager = LinearLayoutManager(this)
        autocompleteRecyclerView.adapter = autocompleteRestaurantAdapter
        mViewModel!!.uiModelsMediatorLiveData.observe(this, Observer { list: List<String?> -> autocompleteRestaurantAdapter.submitList(list) })
        val toolbar = findViewById<Toolbar>(R.id.search_restaurant_toolbar)
        toolbar.title = "Map"
        val navigationView = findViewById<NavigationView>(R.id.search_restaurant_navigation_view_menu)
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            if (menuItem.itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,
                        MainActivity::class.java))
                finish()
            }
            if (menuItem.itemId == R.id.nav_settings) {
                startActivity(Intent(this,
                        NotificationActivity::class.java))
            }
            if (menuItem.itemId == R.id.nav_your_lunch) {
                mViewModel!!.currentTodayUserLiveData?.observe(
                        this, Observer { todayUser: TodayUser ->
                    startActivity(
                            RestaurantDetailActivity.navigate(
                                    this@SearchRestaurantActivity,
                                    todayUser.placeId,
                                    todayUser.restaurantName))
                })
            }
            false
        }
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.search_restaurant_drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            displayedFragment(0)
        }
        val drawerHeadView = navigationView.getHeaderView(0)
        val profilImageView = drawerHeadView.findViewById<ImageView>(R.id.head_drawer_profil_pic_iv)
        if (FirebaseAuth.getInstance().currentUser != null) {
            Glide.with(profilImageView)
                    .load(FirebaseAuth.getInstance().currentUser!!.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilImageView)
        }
        val userNameTextView = drawerHeadView.findViewById<TextView>(R.id.head_drawer_user_name_tv)
        val emailAddressTextView = drawerHeadView.findViewById<TextView>(R.id.head_drawer_email_tv)
        emailAddressTextView.text = FirebaseAuth.getInstance().currentUser!!.email
        userNameTextView.text = FirebaseAuth.getInstance().currentUser!!.displayName
        bottomNavView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.navigation_localisation -> {
                    displayedFragment(0)
                    toolbar.title = "Map"
                }
                R.id.navigation_list_view -> {
                    displayedFragment(1)
                    toolbar.title = "I'm Hungry!"
                }
                R.id.navigation_workmates -> {
                    displayedFragment(2)
                    toolbar.title = "Available workmates"
                }
            }
            true
        }
    }

    private fun displayedFragment(fragmentNumber: Int) {
        val fragment: Fragment = when (fragmentNumber) {
            0 -> newInstance()
            1 -> ListViewFragment.newInstance()
            2 -> WorkmatesFragment.newInstance()
            else -> throw IllegalStateException("Incorrect fragment number : $fragmentNumber")
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        searchView = menu.findItem(R.id.menu_activity_search).actionView as SearchView
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mViewModel!!.onSearchQueryChange(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.menu_activity_search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.search_restaurant_drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onAutocompleteTextSelected(selectedText: String?) {
        searchView!!.setQuery(selectedText, false)
        selectedText?.let { mViewModel!!.onAutocompleteSelected(it) }
    }
}