package com.lab3

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

//імпорти для навігації:
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TourismApp()
        }
    }
}

// ✅ Модель даних
data class Place(
    val id: Int,
    val name: String,
    val description: String
)

// ✅ Розширені описи міст
val places = listOf(
    Place(
        1,
        "Львів",
        "Львів — місто кави, шоколаду й легенд. Старе Місто входить до спадщини ЮНЕСКО. " +
                "Тут кожна вуличка дихає історією Австро-Угорщини, а Ратуша відкриває панораму на червоні дахи та шпилі. " +
                "Львів відомий своїми атмосферними ресторанами, театрами та неймовірними кав’ярнями."
    ),

    Place(
        2,
        "Київ",
        "Столиця України — поєднання сучасності та тисячолітньої історії. " +
                "Софія Київська, Києво-Печерська лавра, Золоті ворота — свідки епохи Русі. " +
                "На Хрещатику кипить життя, а Оболонська набережна чудово підходить для прогулянок. " +
                "Київ — місто мостів, парків і величної архітектури."
    ),

    Place(
        3,
        "Одеса",
        "Одеса — перлина біля моря, місто з унікальним гумором і південним колоритом. " +
                "Тут знаходиться знаменита Дерибасівська, Оперний театр, Потьомкінські сходи. " +
                "У літку — тепле море, пляжі Ланжерону, морська кухня та набережна з неймовірними заходами сонця."
    ),

    Place(
        4,
        "Карпати",
        "Карпати — серце української природи. " +
                "Гірські хребти, водоспади, чисте повітря, традиції гуцулів. " +
                "Буковель взимку — рай для лижників, а влітку — для туристів та любителів походів. " +
                "Карпати дарують відчуття спокою та неймовірної гармонії."
    ),

    Place(
        5,
        "Чернівці",
        "Чернівці — місто, яке називають «маленьким Віднем». " +
                "Університет ім. Федьковича входить до списку ЮНЕСКО та вражає своєю архітектурою. " +
                "Вузькі європейські вулички, центральна площа, театри та затишна атмосфера роблять місто ідеальним для прогулянок."
    )
)

@Composable
fun TourismApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {

        // ✅ Екран списку
        composable("list") {
            PlacesListScreen(navController)
        }

        // ✅ Екран деталей
        composable(
            route = "details/{placeId}",
            arguments = listOf(navArgument("placeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("placeId") ?: 0
            val place = places.first { it.id == id }
            PlaceDetailsScreen(place, navController)
        }
    }
}

@Composable
fun PlacesListScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Популярні туристичні місця",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(places) { place ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("details/${place.id}")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(place.name, fontWeight = FontWeight.Bold)
                        Text(place.description, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceDetailsScreen(place: Place, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Назад до списку")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            place.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(place.description, style = MaterialTheme.typography.bodyLarge)
    }
}