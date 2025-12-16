package com.example.labs_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApp()
        }
    }
}

@Composable
fun ToDoApp() {
    var taskText by remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(16.dp)) {

        // поле вводу
        OutlinedTextField(
            value = taskText,
            onValueChange = { taskText = it },
            label = { Text("Введіть завдання") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // кнопка додати
        Button(
            onClick = {
                if (taskText.isNotBlank()) {
                    tasks.add(taskText)
                    taskText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Додати завдання")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Список справ:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(10.dp))

        // вивід + кнопка видалення
        tasks.forEachIndexed { index, task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${index + 1}. $task")

                Button(
                    onClick = { tasks.removeAt(index) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Видалити")
                }
            }
        }
    }
}