package com.example.saferun.ui.map

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.saferun.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private const val ROUTE_GREEN = "#FF00E676"
private const val ROUTE_HALO = "#F2FFFFFF"

private class MapElements(
    val mapView: MapView,
    val halo: Polyline,
    val route: Polyline,
    val marker: Marker,
)

/**
 * Map preview embedded in a scrollable screen: it ignores every touch so the
 * parent column keeps handling the gesture. Interactive maps use a plain MapView.
 */
private class PreviewMapView(context: Context) : MapView(context) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean = false
}

/**
 * OpenStreetMap panel backed by osmdroid.
 *
 * The component is intentionally isolated: it only receives [RoutePoint]s, so the
 * app can switch to Google Maps later without touching the dashboard or the
 * telemetry layer.
 */
@Composable
fun RouteMap(
    points: List<RoutePoint>,
    modifier: Modifier = Modifier,
    currentPosition: RoutePoint? = null,
    interactive: Boolean = false,
    initialZoom: Double = 16.0,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val elements = remember(interactive) {
        val mapView = if (interactive) {
            MapView(context)
        } else {
            PreviewMapView(context)
        }.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(interactive)
            setTilesScaledToDpi(true)
            setUseDataConnection(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(initialZoom)
            setBackgroundColor(AndroidColor.TRANSPARENT)
        }

        val halo = Polyline(mapView).apply {
            outlinePaint.apply {
                color = ROUTE_HALO.toColorInt()
                strokeWidth = 11f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                style = Paint.Style.STROKE
                isAntiAlias = true
            }
            isGeodesic = true
        }
        val route = Polyline(mapView).apply {
            outlinePaint.apply {
                color = ROUTE_GREEN.toColorInt()
                strokeWidth = 6f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                style = Paint.Style.STROKE
                isAntiAlias = true
            }
            isGeodesic = true
        }
        val marker = Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(context, R.drawable.ic_map_marker)
            isEnabled = false
        }

        mapView.overlays.add(halo)
        mapView.overlays.add(route)
        mapView.overlays.add(marker)
        MapElements(mapView = mapView, halo = halo, route = route, marker = marker)
    }

    DisposableEffect(lifecycleOwner, elements) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> elements.mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> elements.mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            elements.mapView.onResume()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            elements.mapView.onPause()
            elements.mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { elements.mapView },
        update = { view ->
            val geoPoints = points.map { GeoPoint(it.latitude, it.longitude) }
            elements.halo.setPoints(geoPoints)
            elements.route.setPoints(geoPoints)

            val focus = currentPosition ?: points.lastOrNull()
            if (focus != null) {
                val geoFocus = GeoPoint(focus.latitude, focus.longitude)
                elements.marker.position = geoFocus
                elements.marker.isEnabled = true
                if (!interactive) {
                    view.controller.setCenter(geoFocus)
                }
            } else {
                elements.marker.isEnabled = false
            }
            view.invalidate()
        },
    )
}
