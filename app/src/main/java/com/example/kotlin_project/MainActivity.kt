package com.example.kotlin_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_project.adapter.TodoAdapter
import com.example.kotlin_project.databinding.ActivityMainBinding
import com.example.kotlin_project.database.Todo
import com.example.kotlin_project.viewmodel.TodoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var todoAdapter: TodoAdapter

    lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        todoViewModel.todoList.observe(this) {
            Log.d(TAG, "onCreate: $it")
            todoAdapter.update(it)

        }

        todoAdapter = TodoAdapter(this)
        binding.TodoList.layoutManager = LinearLayoutManager(this)
        binding.TodoList.adapter = todoAdapter

        binding.AddList.setOnClickListener {
            val intent = Intent(this, EditTodoActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
        }

        todoAdapter.setItemClickListener(object: TodoAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                Toast.makeText(this@MainActivity, "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)

                    val intent = Intent(this@MainActivity, EditTodoActivity::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", todo)
                    }
                    requestActivity.launch(intent)
                }
            }
        })

        todoAdapter.setItemCheckBoxClickListener(object: TodoAdapter.ItemCheckBoxClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)
                    todo.isChecked = !todo.isChecked
                    todoViewModel.update(todo)
                    Log.d(TAG, "onClick: ${todo.isChecked}")
                }
            }
        })
    }

    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val todo = it.data?.getSerializableExtra("todo") as Todo

            when(it.data?.getIntExtra("flag", -1)) {
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        todoViewModel.insert(todo)
                    }
                    Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    Log.d(TAG, "update: ${todo.title}")
                    CoroutineScope(Dispatchers.IO).launch {
                        todoViewModel.update(todo)
                    }
                    Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.menu_item_delete -> {
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show()
                todoViewModel.todoList.value!!.forEach {
                    if (it.isChecked) {
                        todoViewModel.delete(it)
                    }
                }
                return true
            }
            R.id.menu_item_alarm ->{
                var builder = NotificationCompat.Builder(this, "start_day")
                    .setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
                    .setContentTitle("하루시작")
                    .setContentText("오늘 해야할 일들을 작성하세요")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel_id = "start_day"
                    val channel_name = "하루시작"
                    val descriptionText = "알림설정"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(channel_id, channel_name, importance).apply {
                        description = descriptionText
                    }

                    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                    notificationManager.notify(1002, builder.build())
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }
}