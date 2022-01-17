package com.app.mapyourway

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mapyourway.models.UserMap
import com.app.mapyourway.viewmodels.UserMapViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.io.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
        const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
        const val FILENAME = "UserMaps.data"
    }

    private lateinit var rvMaps: RecyclerView
    private lateinit var fabCreateMap: FloatingActionButton
    private var userMaps: MutableList<UserMap> = emptyList<UserMap>().toMutableList()
    private lateinit var mapAdapter: MapsAdapter
    private lateinit var viewModel: UserMapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        viewModel = ViewModelProvider(this).get(UserMapViewModel::class.java)

//        val userMapsFromFile = deserializeUserMaps(this).toMutableList()
//        if(userMapsFromFile.isEmpty()) {
//            userMaps.addAll(generateSampleData())
//        }
//        userMaps.addAll(userMapsFromFile)
        viewModel.allUserMaps.observe(this, Observer { items ->
            userMaps.clear()
            items?.let {
                items.forEach { map ->
                    val um = UserMap()
                    um.title = map.userMap.title
                    um.userMapId = map.userMap.userMapId
                    um.places.addAll(map.places)
                    userMaps.add(um)
                }
            }
            mapAdapter.notifyDataSetChanged()
        })

        rvMaps = findViewById(R.id.rvMaps)
        //Set layout manager on rv
        rvMaps.layoutManager = LinearLayoutManager(this)
        //Set adapter on the recycler view
        mapAdapter = MapsAdapter(this, userMaps, object : MapsAdapter.OnClickListener {
            //When user taps on a view in RV, navigate to new activity
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick: $position")
                val intent = Intent(this@MainActivity, DisplayMapActivity::class.java)
                intent.putExtra(EXTRA_USER_MAP, userMaps[position])
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        rvMaps.adapter = mapAdapter

        val swap = ItemTouchHelper(itemSwipe)
        swap.attachToRecyclerView(rvMaps)

        fabCreateMap = findViewById(R.id.fabCreateMap)
        fabCreateMap.setOnClickListener {
            Log.i(TAG, "Tap on fab")
            showAlertDialog()
        }
    }

    private fun showDeleteDialog(viewHolder: RecyclerView.ViewHolder) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Confirm") { _, _ ->
            val position = viewHolder.adapterPosition
            val itemToDelete = userMaps[position]
            viewModel.delete(itemToDelete)
            userMaps.removeAt(position)
            mapAdapter.notifyItemRemoved(position)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            val position = viewHolder.adapterPosition
            mapAdapter.notifyItemChanged(position)
        }
        builder.show()
    }

    private fun launchIntentToCreateMapActivity(title: String) {
        val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
        intent.putExtra(EXTRA_MAP_TITLE, title)
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
                userMaps.add(userMap)
                mapAdapter.notifyItemInserted(userMaps.size - 1)

                //serializeUserMaps(this, userMaps)
                Log.i(TAG, "onActivityresult with new map title ${userMap.title}")
            }
        }

    private fun showAlertDialog() {
        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Map title")
            .setView(mapFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null).show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = mapFormView.findViewById<EditText>(R.id.editText).text.toString()
            if (title.trim().isEmpty()) {
                Toast.makeText(
                    this,
                    "Place must have non-empty title",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            //Navigate to create map activity
            launchIntentToCreateMapActivity(title)
            dialog.dismiss()
        }
    }

    private fun getDataFile(context: Context): File {
        Log.i(TAG, "Getting file from directory ${context.filesDir}")
        return File(context.filesDir, FILENAME)
    }

    private fun serializeUserMaps(context: Context, userMaps: List<UserMap>) {
        Log.i(TAG, "serializeUserMaps")
        return ObjectOutputStream(FileOutputStream(getDataFile(context))).use {
            it.writeObject(userMaps)
        }
    }

    private fun deserializeUserMaps(context: Context): List<UserMap> {
        Log.i(TAG, "deserializeUserMaps")
        val dataFile = getDataFile(context)
        //ddataFile.delete()
        if (!dataFile.exists()) {
            Log.i(TAG, "Data file doesn't exist.")
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as List<UserMap> }
    }

    val itemSwipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            showDeleteDialog(viewHolder)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addSwipeRightActionIcon(R.drawable.ic_delete)
                .addSwipeRightBackgroundColor(R.color.delete_item)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }


}