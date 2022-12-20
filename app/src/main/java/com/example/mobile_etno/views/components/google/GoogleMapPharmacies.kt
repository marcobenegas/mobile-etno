package com.example.mobile_etno.views.components.google

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mobile_etno.models.Pharmacy
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun GoogleMapPharmacies(
    listPharmacies: MutableList<Pharmacy>
){
    val currentContext = LocalContext.current
    val properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val uiSettings by remember { mutableStateOf(MapUiSettings()) }

    GoogleMap(
        uiSettings = uiSettings,
        properties = properties,
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        cameraPositionState = rememberCameraPositionState{ position = CameraPosition.fromLatLngZoom(LatLng(42.13202335670619,-0.40816585603218675), 14f) }
    ){
        listPharmacies.forEach {
            pharmacy ->
            Marker(
                icon = if (pharmacy.type == "Normal") bitmapDescriptor(currentContext, com.example.mobile_etno.R.drawable.blue_pharmacy) else bitmapDescriptor(currentContext, com.example.mobile_etno.R.drawable.red_pharmacy),
                state = MarkerState(
                    position = LatLng(
                        pharmacy.latitude!!.toDouble(),
                        pharmacy.longitude!!.toDouble()
                    )
                ),
                title = pharmacy.name,
                snippet = "Horario: ${pharmacy.schedule}",
            )
        }
    }
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, 120, 120)

    val bm = Bitmap.createBitmap(
        120,
        120,
        Bitmap.Config.ARGB_8888
    )
    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}