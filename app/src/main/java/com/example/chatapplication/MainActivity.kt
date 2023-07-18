package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var filterList: ArrayList<User>
    private lateinit var tempList: ArrayList<User>

    private lateinit var dotIcon: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var searchBox: SearchView
    private lateinit var backButton: ImageView

    private lateinit var maintoolbar: LinearLayout
    private lateinit var searchBar: LinearLayout

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        filterList = ArrayList()
        tempList = ArrayList()
        userAdapter = UserAdapter(this,userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = userAdapter

        dotIcon = findViewById(R.id.dots)
        searchIcon = findViewById(R.id.search_icon)


        maintoolbar = findViewById(R.id.toolBarLinearLayout)
        searchBar = findViewById(R.id.toolBarLinearLayout2)

        searchBox = findViewById(R.id.userSearch)
        backButton = findViewById(R.id.back_button)

        searchBox.setOnCloseListener {
            Toast.makeText(this@MainActivity, "Close", Toast.LENGTH_SHORT).show()
            false
        }


        searchIcon.setOnClickListener {
            toggleViews()
        }

        backButton.setOnClickListener{
            searchBox.setQuery("",false)
            searchBox.clearFocus()
            toggleViews()
        }

        dotIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, dotIcon)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.logout -> {
                        mAuth.signOut()
                        val intent = Intent(this,Login::class.java)
                        finish()
                        startActivity(intent)
                        true
                    }
                    // Add more conditions for other menu items if needed
                    else -> false
                }
            }

            popupMenu.show()
        }

        searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    toggleViews()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterUser(newText)
                }
                return true
            }

        })



        mDb.child("users").addValueEventListener(object :ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                tempList.clear()
                for (postSnapShot in snapshot.children) {
                    val currentUser = postSnapShot.getValue(User::class.java)
                    if ( mAuth.currentUser?.uid != currentUser?.uid ) {
                        userList.add(currentUser!!)
                    }
                }
                tempList.addAll(userList)
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun toggleViews() {
        if (maintoolbar.visibility == View.VISIBLE) {
            maintoolbar.visibility = View.GONE
            searchBar.visibility = View.VISIBLE

        } else {
            maintoolbar.visibility = View.VISIBLE
            searchBar.visibility = View.GONE
        }
    }

    private fun filterUser(text : String) {
        filterList.clear()
        for ( user in tempList ) {
            if (user.name?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT)) == true ) {
                filterList.add(user)
            }
        }
        userAdapter.updateList(filterList)
    }
}
