package ru.my.cryptotracker.ui.screens.cryptoDashboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest.Builder
import coil3.request.crossfade
import ru.my.cryptotracker.R
import ru.my.cryptotracker.core.designsystem.theme.CryptoTrackerTheme

@Composable
fun CoinImageFromURL(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val request = remember(imageUrl) {
        Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build()
    }

    SubcomposeAsyncImage(
        modifier = modifier,
        model = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        },
        error = {
            Image(
                painter = painterResource(R.drawable.coin_digital),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CocktailImageFromURLPreview() {
    CryptoTrackerTheme {
        Surface {
            CoinImageFromURL(
                imageUrl = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
            )
        }
    }
}