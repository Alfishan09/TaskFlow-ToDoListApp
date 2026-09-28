package com.example.todolistapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val AppBackground = Color(0xFFF5F6FA)
private val PrimaryBlue = Color(0xFF4F46E5)
private val DarkText = Color(0xFF1E293B)
private val MutedText = Color(0xFF64748B)
private val CardBackground = Color.White

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: String,
    val priority: String,
    val completed: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = PrimaryBlue,
                    background = AppBackground,
                    surface = CardBackground,
                    onBackground = DarkText,
                    onSurface = DarkText
                )
            ) {
                TodoApp(this)
            }
        }
    }
}

@Composable
fun TodoApp(context: Context) {

    val prefs = remember {
        context.getSharedPreferences(
            "todo_data",
            Context.MODE_PRIVATE
        )
    }

    fun loadTasks(): List<Task> {
        return try {
            val array = JSONArray(
                prefs.getString("tasks", "[]")
            )

            List(array.length()) { i ->
                val obj = array.getJSONObject(i)

                Task(
                    id = obj.getLong("id"),
                    title = obj.getString("title"),
                    description = obj.optString("description", ""),
                    dueDate = obj.optString("dueDate", ""),
                    priority = obj.optString("priority", "Medium"),
                    completed = obj.optBoolean("completed", false)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    var tasks by remember { mutableStateOf(loadTasks()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var selectedFilter by remember { mutableStateOf("All") }

    fun saveTasks(updated: List<Task>) {
        tasks = updated

        val array = JSONArray()

        updated.forEach { task ->
            array.put(
                JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("dueDate", task.dueDate)
                    put("priority", task.priority)
                    put("completed", task.completed)
                }
            )
        }

        prefs.edit()
            .putString("tasks", array.toString())
            .apply()
    }

    val completedCount = tasks.count { it.completed }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) {
        completedCount.toFloat() / totalCount
    } else {
        0f
    }

    val filteredTasks = when (selectedFilter) {
        "Pending" -> tasks.filter { !it.completed }
        "Completed" -> tasks.filter { it.completed }
        else -> tasks
    }

    Scaffold(
        containerColor = AppBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingTask = null
                    showDialog = true
                },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "+  Add Task",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Text(
                text = "TASKFLOW",
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "My Tasks",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Stay organized. Get things done.",
                fontSize = 14.sp,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEEF0FF)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Progress",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "$completedCount of $totalCount tasks completed",
                                fontSize = 13.sp,
                                color = MutedText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .background(
                                    Color.White,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = PrimaryBlue,
                        trackColor = Color(0xFFD8DCFA),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Task Section
            Text(
                text = "Manage Tasks",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filters
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "Completed").forEach { filter ->

                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = MutedText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == filter,
                            borderColor = Color(0xFFE2E8F0),
                            selectedBorderColor = PrimaryBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            if (filteredTasks.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (tasks.isEmpty()) {
                                "No tasks yet"
                            } else {
                                "Nothing to show here"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (tasks.isEmpty()) {
                                "Add your first task and start organizing your day."
                            } else {
                                "Your tasks will appear here."
                            },
                            fontSize = 13.sp,
                            color = MutedText
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(
                        items = filteredTasks,
                        key = { it.id }
                    ) { task ->

                        TaskCard(
                            task = task,
                            onCheckedChange = { checked ->
                                saveTasks(
                                    tasks.map {
                                        if (it.id == task.id) {
                                            it.copy(completed = checked)
                                        } else {
                                            it
                                        }
                                    }
                                )
                            },
                            onEdit = {
                                editingTask = task
                                showDialog = true
                            },
                            onDelete = {
                                saveTasks(
                                    tasks.filterNot {
                                        it.id == task.id
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showDialog) {
        TaskDialog(
            task = editingTask,
            onDismiss = {
                showDialog = false
                editingTask = null
            },
            onSave = { title, description, dueDate, priority ->

                val updated = if (editingTask == null) {

                    tasks + Task(
                        id = System.currentTimeMillis(),
                        title = title,
                        description = description,
                        dueDate = dueDate,
                        priority = priority
                    )

                } else {

                    tasks.map { oldTask ->
                        if (oldTask.id == editingTask!!.id) {
                            oldTask.copy(
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                priority = priority
                            )
                        } else {
                            oldTask
                        }
                    }
                }

                saveTasks(updated)
                showDialog = false
                editingTask = null
            }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFDC2626)
        "Medium" -> Color(0xFFD97706)
        else -> Color(0xFF059669)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = task.completed,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.completed) {
                            MutedText
                        } else {
                            DarkText
                        },
                        textDecoration = if (task.completed) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = task.description,
                            fontSize = 13.sp,
                            color = MutedText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Priority Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = priorityColor.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = "${task.priority} Priority",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = priorityColor
                    )
                }

                if (task.dueDate.isNotBlank()) {
                    Text(
                        text = task.dueDate,
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                color = Color(0xFFF1F5F9)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(onClick = onEdit) {
                    Text(
                        text = "Edit",
                        color = PrimaryBlue
                    )
                }

                TextButton(onClick = onDelete) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {

    var title by remember(task) {
        mutableStateOf(task?.title ?: "")
    }

    var description by remember(task) {
        mutableStateOf(task?.description ?: "")
    }

    var dueDate by remember(task) {
        mutableStateOf(task?.dueDate ?: "")
    }

    var priority by remember(task) {
        mutableStateOf(task?.priority ?: "Medium")
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,

        title = {
            Text(
                text = if (task == null) {
                    "Create New Task"
                } else {
                    "Edit Task"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("What needs to be done?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Add a few details...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    placeholder = { Text("DD-MM-YYYY") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Priority: $priority")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Low", "Medium", "High").forEach { option ->

                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            title.trim(),
                            description.trim(),
                            dueDate.trim(),
                            priority
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text("Save Task")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}