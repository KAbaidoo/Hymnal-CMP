package com.kobby.hymnal.presentation.screens.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.theme.DarkTextColor
import com.kobby.hymnal.theme.HymnalAppTheme
import com.kobby.hymnal.theme.YellowAccent
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.ai_generate
import hymnal_cmp.composeapp.generated.resources.arrow_left_s_line
import hymnal_cmp.composeapp.generated.resources.book_leaf
import hymnal_cmp.composeapp.generated.resources.bookmark_line
import hymnal_cmp.composeapp.generated.resources.cd_back
import hymnal_cmp.composeapp.generated.resources.cd_home
import hymnal_cmp.composeapp.generated.resources.close_circle_line
import hymnal_cmp.composeapp.generated.resources.home_3_line
import hymnal_cmp.composeapp.generated.resources.music_2_line
import hymnal_cmp.composeapp.generated.resources.wifi_off_line
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Simple plan model
enum class PayPlan { Yearly, OneTime }

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
    var selectedPlan by remember { mutableStateOf(PayPlan.OneTime) }
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


                        // Radio cards for plan selection
                        PurchaseOptions(
                            selected = selectedPlan,
                            onSelected = { selectedPlan = it }
                        )

                        if (errorMsg != null) {
                            Text(
                                text = errorMsg!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        // Features card
                        FeaturesCard()



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
            style = MaterialTheme.typography.bodySmall,
            color = YellowAccent.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PurchaseOptions(selected: PayPlan, onSelected: (PayPlan) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RadioPlanCard(
            title = "USD 1.99 / year",
            subtitle = "Renews automatically",
            badge = null,
            selected = selected == PayPlan.Yearly,
            onClick = { onSelected(PayPlan.Yearly) }
        )
        RadioPlanCard(
            title = "USD 3.99 / One-time payment",
            subtitle = "Pay once. Own it forever.",
            badge = "Best Value",
            selected = selected == PayPlan.OneTime,
            onClick = { onSelected(PayPlan.OneTime) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RadioPlanCard(
    title: String,
    subtitle: String,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.58f)
    val borderWidth = if (selected) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(onClick = onClick)
            .then(Modifier.border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(16.dp)))
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio indicator
        RadioButton(
            selected = selected,
            onClick = onClick
        )


        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically){

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
                if (badge != null) {
                    Spacer(Modifier.width(48.dp))
                    BadgePill(badge)
                }
            }
        }
    }
}

@Composable
private fun BadgePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFCAC0FF))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun PrimaryCTA(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
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
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.clickable { onPrivacy() }
        )
        Text(
            text = "  •  ",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Text(
            text = "Terms of Use",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.clickable { onTerms() }
        )
    }
}

@Composable
private fun FeaturesCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Includes",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Everything you need for worship",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FeatureRow(vectorResource(Res.drawable.music_2_line),"Full hymn library")
        FeatureRow(vectorResource(Res.drawable.wifi_off_line),"Offline access")
        FeatureRow(vectorResource(Res.drawable.bookmark_line),"Bookmarks & favorites")
        FeatureRow(vectorResource(Res.drawable.close_circle_line),"No ads")
        FeatureRow(vectorResource(Res.drawable.ai_generate),"Future updates included")
        // Shared ministry info card (beneath FeaturesCard)
        SharedMinistryCard()
    }
}

@Composable
private fun SharedMinistryCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "A shared ministry",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "This contribution supports hosting, updates, and improvements to keep the Hymnal app accessible and ad-free.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Simple dot indicator to avoid external icon deps
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = icon,
            contentDescription = stringResource(Res.string.cd_home)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
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
@Preview(name = "PayWall Dark")
@Composable
fun PayWallContentPreviewDark() {
    HymnalAppTheme(darkTheme = true) {
        PayWallContent()
    }
}
