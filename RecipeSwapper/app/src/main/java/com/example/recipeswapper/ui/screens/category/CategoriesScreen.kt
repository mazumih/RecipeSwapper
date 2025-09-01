package com.example.recipeswapper.ui.screens.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.data.models.CATEGORY_ORDER
import com.example.recipeswapper.data.models.Category
import com.example.recipeswapper.data.models.getCategoryIcon
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.TopBar

@Composable
fun CategoriesScreen(
    state: CategoriesState,
    navController: NavController,
    onFilter: (Category) -> Unit
) {

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