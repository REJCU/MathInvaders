
package com.example.thismathinvaders.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.thismathinvaders.repository.ApodResponse

private val imageHeight = 160.dp
@Composable
fun ApodImage(
    apod: ApodResponse?,
    modifier: Modifier = Modifier
) {
    if (apod == null) return

    Column(modifier = modifier) {
        // TODO - click on image to expand it
        AsyncImage(
            model = apod.url,
            contentDescription = apod.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
 
