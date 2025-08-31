package com.example.recipeswapper.ui.screens.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.recipeswapper.R
import com.example.recipeswapper.data.models.CATEGORY_ORDER
import com.example.recipeswapper.data.models.Category
import com.example.recipeswapper.data.models.Theme
import com.example.recipeswapper.data.models.getCategoryIcon
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.theme.RecipeSwapperTheme

/*@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    val navController = rememberNavController()

    val CATEGORY_ORDER = listOf(
        "Antipasti",
        "Primi Piatti",
        "Secondi",
        "Contorni",
        "Dolci",
        "Lievitati"
    )

    val fakeCategories = listOf(
        Category(id = "6", name = "Lievitati", icon = "ic_lievitati"),
        Category(id = "2", name = "Primi Piatti", icon = "ic_primi_piatti"),
        Category(id = "4", name = "Dolci", icon = "ic_dolci"),
        Category(id = "3", name = "Secondi", icon = "ic_secondi"),
        Category(id = "5", name = "Contorni", icon = "ic_contorni"),
        Category(id = "1", name = "Antipasti", icon = "ic_antipasti")
    )

    val fakeState = CategoriesState(
        categories = fakeCategories
    )

    val fakeActions = object : CategoriesActions {
        override fun updateCategoriesDB() {
            TODO("Not yet implemented")
        }

    }

    RecipeSwapperTheme(Theme.Light) {
        CategoriesScreen(
            state = fakeState,
            actions = fakeActions,
            navController = navController,
            onFilter = {},
            CATEGORY_ORDER
        )
    }
}*/

@Composable
fun CategoriesScreen(
    state: CategoriesState,
    actions: CategoriesActions,
    navController: NavController,
    onFilter: (Category) -> Unit
) {
    LaunchedEffect(Unit) {
        actions.updateCategoriesDB()
    }

    val orderedCategories = state.categories.sortedBy {
        CATEGORY_ORDER.indexOf(it.name)
    }

    Scaffold(
        topBar = { TopBar(navController, "Categorie") },
        bottomBar = { BottomBar(navController, RecipeSwapperRoute.Categories) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(15.dp)
        ) {
            items(orderedCategories) { category ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onFilter(category) },
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 6.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(getCategoryIcon(category.icon)),
                            category.name,
                            modifier = Modifier.size(120.dp).padding(bottom = 2.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}