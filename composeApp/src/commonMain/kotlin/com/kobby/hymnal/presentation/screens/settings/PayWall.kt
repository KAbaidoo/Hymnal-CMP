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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
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
import hymnal_cmp.composeapp.generated.resources.book_leaf
import hymnal_cmp.composeapp.generated.resources.bookmark_line
import hymnal_cmp.composeapp.generated.resources.heart_2_line
import hymnal_cmp.composeapp.generated.resources.music_2_line
import hymnal_cmp.composeapp.generated.resources.settings_action_privacy
import hymnal_cmp.composeapp.generated.resources.settings_action_terms
import hymnal_cmp.composeapp.generated.resources.settings_option_basic_subtitle
import hymnal_cmp.composeapp.generated.resources.settings_option_basic_title
import hymnal_cmp.composeapp.generated.resources.settings_option_generous_badge
import hymnal_cmp.composeapp.generated.resources.settings_option_generous_subtitle
import hymnal_cmp.composeapp.generated.resources.settings_option_generous_title
import hymnal_cmp.composeapp.generated.resources.settings_section_shared_ministry_body
import hymnal_cmp.composeapp.generated.resources.settings_section_shared_ministry_title
import hymnal_cmp.composeapp.generated.resources.settings_separator_bullet
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Support tier model - both tiers unlock the same features
enum class PayPlan {
    SupportBasic,   // GH₵ 15 / $0.99 - accessible tier
    SupportGenerous // GH₵ 20 / $1.99 - generous supporter tier
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayWallContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isRestoring: Boolean = false,
    errorMsg: String? = null,
    successMsg: String? = null,
    onPurchase: (PayPlan) -> Unit = {},
    onRestore: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onTerms: () -> Unit = {}
) {
    var selectedPlan by remember { mutableStateOf(PayPlan.SupportGenerous) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val topAppBarElementColor = MaterialTheme.colorScheme.secondary
    // Add a content scroll state for the main content so bottom content can scroll when space is limited
    val contentScrollState = rememberScrollState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {


                },
                navigationIcon = {

                },
                actions = {
                    // Close button (X) - always visible in freemium
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = topAppBarElementColor,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor= topAppBarElementColor,
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.height(220.dp)) {

                Image(
                    painter = painterResource(Res.drawable.book_leaf),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(250.dp)
                        .align(Alignment.TopEnd)
                )
                PaywallHeader()

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

            // Content card
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        // Make the inner column vertically scrollable and avoid fillMaxSize so it can scroll when content exceeds available space
                        modifier = Modifier
                            .verticalScroll(contentScrollState)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {


                        // Radio cards for plan selection
                        PurchaseOptions(
                            selected = selectedPlan,
                            onSelected = { selectedPlan = it }
                        )

                        if (errorMsg != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = errorMsg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        if (successMsg != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = successMsg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4CAF50) // Green for success
                            )
                        }
                        // Features card
                        FeaturesCard()
                        // Shared ministry card just beneath FeaturesCard
                        SharedMinistryCard()

                        PrimaryCTA(
                            text = if (isLoading) {
                                "Processing..."
                            } else {
                                "Support Development"
                            },
                            enabled = !isLoading && !isRestoring,
                            onClick = { onPurchase(selectedPlan) }
                        )


//                        // Restore purchases button
//                        OutlinedButton(
//                            modifier = Modifier.fillMaxWidth().height(48.dp),
//                            enabled = !isLoading && !isRestoring,
//                            onClick = onRestore,
//                            shape = RoundedCornerShape(12.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.background,
//                                contentColor = MaterialTheme.colorScheme.onBackground
//                            )
//                        ) {
//                            Text(
//                                text = if (isRestoring) "Restoring..." else "Restore Purchase",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                        }


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
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(60.dp))
        Text(
            text = "Thank you for using our app!",
            style =  MaterialTheme.typography.headlineLarge,
            color = DarkTextColor,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "All features are free forever. If you find this app helpful, consider supporting development.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.25f
                    ),
                    color = YellowAccent.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
    }
}

@Composable
private fun PurchaseOptions(selected: PayPlan, onSelected: (PayPlan) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RadioPlanCard(
            title = stringResource(Res.string.settings_option_basic_title),
            subtitle = stringResource(Res.string.settings_option_basic_subtitle),
            badge = null,
            selected = selected == PayPlan.SupportBasic,
            onClick = { onSelected(PayPlan.SupportBasic) }
        )
        RadioPlanCard(
            title = stringResource(Res.string.settings_option_generous_title),
            subtitle = stringResource(Res.string.settings_option_generous_subtitle),
            badge = stringResource(Res.string.settings_option_generous_badge),
            selected = selected == PayPlan.SupportGenerous,
            onClick = { onSelected(PayPlan.SupportGenerous) }
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
    val borderWidth = if (selected) 1.dp else 1.dp

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
            onClick = onClick,
            colors = RadioButtonColors(
                selectedColor = MaterialTheme.colorScheme.onSurface,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
                if (badge != null) {
                    Spacer(Modifier.width(20.dp))
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
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
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
            text = stringResource(Res.string.settings_action_privacy),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.clickable { onPrivacy() }
        )
        Text(
            text = stringResource(Res.string.settings_separator_bullet),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Text(
            text = stringResource(Res.string.settings_action_terms),
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
            text = "What Your Support Enables",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Help us keep this app free for everyone",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        FeatureRow(vectorResource(Res.drawable.heart_2_line), "Keep the app free for everyone")
        FeatureRow(vectorResource(Res.drawable.bookmark_line), "Add more hymns and features")
        FeatureRow(vectorResource(Res.drawable.music_2_line), "Maintain and improve the app")
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
            text = stringResource(Res.string.settings_section_shared_ministry_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(Res.string.settings_section_shared_ministry_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = icon,
            contentDescription = null
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
