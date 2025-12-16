package com.lab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ---------- DATASTORE ----------
val ComponentActivity.dataStore by preferencesDataStore("study_tracker")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StudyApp(this)
            }
        }
    }
}

// ---------- DATA MODELS ----------
data class Subject(val name: String, val labs: List<String>)

val subjects = listOf(
    Subject("Програмування", listOf("Лабораторна Робота 1", "Лабораторна Робота 2", "Лабораторна Робота 3")),
    Subject("Бази Даних", listOf("Лабораторна Робота 1", "Лабораторна Робота 2")),
    Subject("Тестування ПЗ", listOf("Лабораторна Робота 1", "Лабораторна Робота 2", "Лабораторна Робота 3", "Лабораторна Робота 4"))
)

// ---------- MAIN APP ----------
@Composable
fun StudyApp(activity: ComponentActivity) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "subjects") {

        composable("subjects") {
            SubjectsScreen(navController)
        }

        composable(
            route = "labs/{subject}",
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType }
            )
        ) { backStack ->
            val subject = backStack.arguments?.getString("subject") ?: ""
            LabsScreen(subject, navController)
        }

        composable(
            route = "labDetails/{subject}/{lab}",
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("lab") { type = NavType.StringType }
            )
        ) { backStack ->
            val subject = backStack.arguments?.getString("subject") ?: ""
            val lab = backStack.arguments?.getString("lab") ?: ""
            LabDetailsScreen(subject, lab, activity, navController)
        }
    }
}

// ---------- SUBJECTS SCREEN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мої предмети") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(subjects) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate("labs/${subject.name}") },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(subject.name, fontWeight = FontWeight.Bold)
                        Text(
                            "Лабораторних: ${subject.labs.size}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ---------- LABS SCREEN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsScreen(subject: String, navController: NavController) {
    val labs = subjects.firstOrNull { it.name == subject }?.labs ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subject) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(labs) { lab ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("labDetails/$subject/$lab")
                        },
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(lab, fontWeight = FontWeight.Bold)
                        Text(
                            "Переглянути деталі",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ---------- LAB DETAILS SCREEN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabDetailsScreen(
    subject: String,
    lab: String,
    activity: ComponentActivity,
    navController: NavController
) {
    val dataStore = activity.dataStore
    val statusKey = stringPreferencesKey("${subject}_${lab}_status")
    val commentKey = stringPreferencesKey("${subject}_${lab}_comment")

    var status by remember { mutableStateOf("Не встановлено") }
    var comment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load saved data
    LaunchedEffect(Unit) {
        val prefs = dataStore.data.first()
        status = prefs[statusKey] ?: "Не встановлено"
        comment = prefs[commentKey] ?: ""
    }

    // Colors for buttons
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant
    val activeText = MaterialTheme.colorScheme.onPrimary
    val inactiveText = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$subject — $lab") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Статус:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // ---------- ВИКОНАНО ----------
                Button(
                    onClick = {
                        scope.launch {
                            dataStore.edit { it[statusKey] = "Виконано" }
                            status = "Виконано"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == "Виконано") activeColor else inactiveColor,
                        contentColor = if (status == "Виконано") activeText else inactiveText
                    )
                ) {
                    Text("✅ Виконано")
                }

                // ---------- В ПРОЦЕСІ ----------
                Button(
                    onClick = {
                        scope.launch {
                            dataStore.edit { it[statusKey] = "В процесі" }
                            status = "В процесі"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == "В процесі") activeColor else inactiveColor,
                        contentColor = if (status == "В процесі") activeText else inactiveText
                    )
                ) {
                    Text("🔄 В процесі")
                }

                // ---------- ВІДКЛАДЕНО ----------
                Button(
                    onClick = {
                        scope.launch {
                            dataStore.edit { it[statusKey] = "Відкладено" }
                            status = "Відкладено"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == "Відкладено") activeColor else inactiveColor,
                        contentColor = if (status == "Відкладено") activeText else inactiveText
                    )
                ) {
                    Text("⏸ Відкладено")
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Коментар:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = {
                    comment = it
                    scope.launch { dataStore.edit { prefs -> prefs[commentKey] = it } }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}