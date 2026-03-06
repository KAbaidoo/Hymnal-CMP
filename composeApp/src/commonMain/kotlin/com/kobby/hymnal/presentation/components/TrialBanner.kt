package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.theme.DarkTextColor
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.trial_upgrade_cta
import org.jetbrains.compose.resources.stringResource

/**
 * A banner component that displays trial period information and upgrade CTA.
 *
 * @param daysRemaining Number of days remaining in the trial period
 * @param onUpgradeClick Callback when the user clicks the upgrade button
 */
@Composable
fun TrialBanner(
    daysRemaining: Int,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            .clickable(onClick = onUpgradeClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$daysRemaining days left in trial",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkTextColor.copy(alpha = 0.8f)
        )
        Text(
            text = stringResource(Res.string.trial_upgrade_cta),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

