package com.example.ravintola1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.TopAppBar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "restaurantList") {
        composable("restaurantList") {
            RestaurantListScreen(navController)
        }
        composable("restaurantDetail/{name}/{address}/{rating}/{cuisine}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?:""
            val address = backStackEntry.arguments?.getString("address") ?: ""
            val rating = backStackEntry.arguments?.getString("rating")?.toDouble() ?: 0.0
            val cuisine = backStackEntry.arguments?.getString("cuisine") ?: ""
            RestaurantDetailScreen(name, address, rating, cuisine, navController)
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantListScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    val filteredRestaurant = remember ( searchText ) {
        restaurantList.filter { restaurant ->
            restaurant.name.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Find a Restaurant",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search Restaurants") },
                modifier = Modifier
                    .fillMaxWidth()
            )
            // RestaurantList
            RestaurantList(filteredRestaurant, navController)

        }
    }
}

@Composable
fun RestaurantList(restaurants: List<Restaurant>, navController: NavController) {
    LocalContext.current
    LazyColumn (
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ){
        items(restaurants) { restaurant ->
            RestaurantListItem(restaurant) {
                navController.navigate("restaurantDetail/${restaurant.name}/${restaurant.address}/${restaurant.rating}/${restaurant.cuisine}")
                // Ravintola list itemiä klikattu. Tämä on callback.
                // Navigoi jonnekin, esim. toiseen näkymään, jolle annetaan ravintola parametriksi
            }
        }
    }
}

@Composable
fun RestaurantListItem(restaurant: Restaurant, restaurantClicked: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                restaurantClicked()
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = restaurant.name, fontWeight = FontWeight.Bold)
            Text(text = restaurant.address, color = Color.Gray)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(name: String, address: String, rating: Double, cuisine: String, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply inner padding from Scaffold
                .padding(horizontal = 32.dp, vertical = 64.dp)
        ) {
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Address: $address", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Rating: $rating", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Cuisine: $cuisine", fontSize = 18.sp)
        }
    }
}

// Tässä staattinen ravintoladata. Web/HTTP/JSON katsotaan ensi kerralla.
// Data class for Restaurant
data class Restaurant(
    val name: String,
    val address: String,
    val rating: Double,
    val cuisine: String
)


// Määritetään ravintolalista globaalisti
val restaurantList = listOf(
    Restaurant("The Gourmet Kitchen", "123 Food St.", 4.5, "Italian"),
    Restaurant("Sushi World", "456 Sushi Ave.", 4.8, "Japanese"),
    Restaurant("Taco Paradise", "789 Taco Blvd.", 4.2, "Mexican"),
    Restaurant("Burger Heaven", "321 Burger Ln.", 4.0, "American"),
    Restaurant("Pasta House", "147 Noodle St.", 4.3, "Italian"),
    Restaurant("Spicy Curry Palace", "852 Spice Rd.", 4.7, "Indian"),
    Restaurant("Le Petit Bistro", "963 French St.", 4.6, "French"),
    Restaurant("Wok 'n' Roll", "258 Noodle Ave.", 4.1, "Chinese"),
    Restaurant("Pizza Planet", "159 Slice Blvd.", 3.9, "Italian"),
    Restaurant("The BBQ Shack", "753 Grill Ln.", 4.4, "American"),
    Restaurant("Ramen Kingdom", "357 Ramen St.", 4.9, "Japanese"),
    Restaurant("Café Mocha", "123 Coffee Rd.", 4.0, "Cafe"),
    Restaurant("Viva la Vegan", "456 Green St.", 4.6, "Vegan"),
    Restaurant("El Toro Loco", "789 Fiesta Ave.", 4.3, "Mexican"),
    Restaurant("Dim Sum Delight", "159 Dumpling Ln.", 4.5, "Chinese"),
    Restaurant("The Greek Taverna", "258 Olive St.", 4.7, "Greek"),
    Restaurant("Kebab Palace", "963 Spice St.", 4.3, "Middle Eastern"),
    Restaurant("The Hot Pot Spot", "654 Boil Ave.", 4.2, "Chinese"),
    Restaurant("Falafel Corner", "321 Vegan Blvd.", 4.0, "Middle Eastern"),
    Restaurant("Seaside Sushi", "753 Ocean Ave.", 4.8, "Japanese"),
    Restaurant("The Taco Stand", "987 Fiesta St.", 3.8, "Mexican"),
    Restaurant("Steakhouse Supreme", "654 Meat Ln.", 4.9, "American"),
    Restaurant("Pho Haven", "258 Soup Ave.", 4.4, "Vietnamese"),
    Restaurant("The Sushi Spot", "951 Fish Blvd.", 4.2, "Japanese"),
    Restaurant("The Vegan Joint", "753 Plant Ave.", 4.7, "Vegan")
)

