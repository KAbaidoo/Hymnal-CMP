package com.kobby.hymnal.presentation.screens.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kobby.hymnal.presentation.components.ScreenBackground
import com.kobby.hymnal.theme.DarkTextColor
import com.kobby.hymnal.theme.HymnalAppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.anglican_hymnal
import hymnal_cmp.composeapp.generated.resources.book_open
import hymnal_cmp.composeapp.generated.resources.anglican_hymnal_multiline
import hymnal_cmp.composeapp.generated.resources.arrow_left_s_line
import hymnal_cmp.composeapp.generated.resources.book_leaf
import hymnal_cmp.composeapp.generated.resources.cd_back
import hymnal_cmp.composeapp.generated.resources.cd_home
import hymnal_cmp.composeapp.generated.resources.cd_search
import hymnal_cmp.composeapp.generated.resources.cd_settings
import hymnal_cmp.composeapp.generated.resources.home_3_line
import hymnal_cmp.composeapp.generated.resources.menu_2_line
import hymnal_cmp.composeapp.generated.resources.search_line
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Simple plan model
enum class PayPlan { Monthly, Yearly }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayWallContent(
    modifier: Modifier = Modifier,
    onPurchase: (PayPlan) -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onTerms: () -> Unit = {}
) {
    var selectedPlan by remember { mutableStateOf(PayPlan.Yearly) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val topAppBarElementColor = MaterialTheme.colorScheme.secondary


    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = {

                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = vectorResource(Res.drawable.arrow_left_s_line),
                                contentDescription = stringResource(Res.string.cd_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onHomeClick) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = vectorResource(Res.drawable.home_3_line),
                                contentDescription = stringResource(Res.string.cd_home)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = topAppBarElementColor,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor= topAppBarElementColor,
                    ),
                    scrollBehavior = scrollBehavior
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(250.dp),
                    painter = painterResource(Res.drawable.book_leaf),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

            }

        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .imePadding()
    ) { paddingValues ->
        // Hero / Header area with background
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {

            PaywallHeader()
            // Content card
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlanToggle(selectedPlan = selectedPlan, onSelect = { selectedPlan = it })

                        PriceRow(selectedPlan = selectedPlan)


                        if (errorMsg != null) {
                            Text(
                                text = errorMsg!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        PrimaryCTA(
                            text = if (isLoading) "Processing…" else "Continue",
                            enabled = !isLoading,
                            onClick = { onPurchase(selectedPlan) }
                        )


                        FooterLinks(onPrivacy = onPrivacy, onTerms = onTerms)
                    }
                }
            }
        }




    }

}

@Composable
private fun PaywallHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Unlock the Full\nAnglican Hymnal",
            style =  MaterialTheme.typography.headlineLarge,
            color = DarkTextColor,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "One small payment. A lifetime\nof worship.",
            style = MaterialTheme.typography.titleMedium,
            color = DarkTextColor.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlanToggle(selectedPlan: PayPlan, onSelect: (PayPlan) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ToggleChip(
            label = "Monthly",
            selected = selectedPlan == PayPlan.Monthly,
            onClick = { onSelect(PayPlan.Monthly) }
        )
        ToggleChip(
            label = "Yearly",
            selected = selectedPlan == PayPlan.Yearly,
            onClick = { onSelect(PayPlan.Yearly) }
        )
    }
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
//            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(vertical = 10.dp)
            .let { base ->
                // Use button to provide accessibility role; visually it's a chip
                Modifier
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = fg)
        // Click handler via Box clickable would require foundation.clickable; keeping simple here
    }
}

@Composable
private fun PriceRow(selectedPlan: PayPlan) {
    val priceText = when (selectedPlan) {
        PayPlan.Monthly -> "$2.99 / month"
        PayPlan.Yearly -> "$19.99 / year"
    }
    val savingsText = if (selectedPlan == PayPlan.Yearly) "Save 44%" else null

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = priceText,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (savingsText != null) {
                Text(
                    text = savingsText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}


@Composable
private fun PrimaryCTA(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SecondaryCTAs(onRestore: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onRestore,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Restore Purchases")
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = { /* Maybe later: gift */ },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Maybe Later")
        }
    }
}

@Composable
private fun FooterLinks(onPrivacy: () -> Unit, onTerms: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Privacy Policy",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = "  •  ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Text(
            text = "Terms of Use",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}


@Preview(name = "PayWall Light", showBackground = true)
@Composable
fun PayWallContentPreviewLight() {
    HymnalAppTheme {
        PayWallContent()
    }
}
