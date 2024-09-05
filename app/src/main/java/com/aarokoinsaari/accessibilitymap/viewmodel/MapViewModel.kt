/*
 * Copyright (c) 2024 Aaro Koinsaari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aarokoinsaari.accessibilitymap.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarokoinsaari.accessibilitymap.intent.MapIntent
import com.aarokoinsaari.accessibilitymap.model.PlaceClusterItem
import com.aarokoinsaari.accessibilitymap.repository.PlaceRepository
import com.aarokoinsaari.accessibilitymap.state.MapState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MapViewModel(private val placeRepository: PlaceRepository) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    private val moveIntents = MutableSharedFlow<MapIntent.Move>(extraBufferCapacity = 64)
    val state: StateFlow<MapState> = _state

    init {
        viewModelScope.launch {
            moveIntents
                .debounce(DEBOUNCE_VALUE)
                .collect { intent ->
                    handleMove(intent)
                }
        }
    }

    fun handleIntent(intent: MapIntent) {
        viewModelScope.launch {
            when (intent) {
                is MapIntent.Move -> moveIntents.emit(intent)
                is MapIntent.MarkerClick -> handleMarkerClick()
            }
        }
    }

    private suspend fun handleMove(intent: MapIntent.Move) {
        if (intent.zoomLevel < ZOOM_THRESHOLD) {
            handleClearMarkers()
            return
        }

        Log.d("MapViewModel", "Update view intent: $intent")
        _state.value = _state.value.copy(
            zoomLevel = intent.zoomLevel,
            center = intent.center,
            currentBounds = intent.bounds
        )
        val currentState = _state.value
        if (currentState.snapshotBounds == null ||
            centerIsOutOfBounds(intent.center, currentState.snapshotBounds)
        ) {
            val expandedBounds = calculateExpandedBounds(intent.bounds)
            _state.value = _state.value.copy(
                currentBounds = intent.bounds,
                snapshotBounds = expandedBounds
            )
            fetchAndSetPlaces(intent.bounds, expandedBounds)
        }
        Log.d("MapViewModel", "MapState after move: ${_state.value}")
    }

    private suspend fun fetchAndSetPlaces(
        currentBounds: LatLngBounds,
        expandedBounds: LatLngBounds
    ) {
        placeRepository.getPlaces(currentBounds, expandedBounds)
            .distinctUntilChanged()
            .collect { newPlaces ->
                val currentClusterItems = _state.value.clusterItems
                val combinedClusterItems =
                    (currentClusterItems + newPlaces.map {
                        PlaceClusterItem(it, 1f)
                    }).distinctBy { it.placeData.id }

                _state.value = _state.value.copy(
                    clusterItems = combinedClusterItems,
                    isLoading = false
                )
            }
    }

    private fun handleMarkerClick() {
        TODO("Not yet implemented")
    }

    private fun handleClearMarkers() {
        if (_state.value.markers.isNotEmpty()) {
            _state.value = _state.value.copy(
                markers = emptyList(),
                currentBounds = null,
                snapshotBounds = null
            )
            Log.d("MapViewModel", "Clear markers action, current state: ${_state.value}")
        }
    }

    private fun calculateExpandedBounds(bounds: LatLngBounds): LatLngBounds {
        val latDiff = bounds.northeast.latitude - bounds.southwest.latitude
        val lonDiff = bounds.northeast.longitude - bounds.southwest.longitude

        val expandedSouthwest = LatLng(
            bounds.southwest.latitude - latDiff * (EXPAND_FACTOR - 1) / 2,
            bounds.southwest.longitude - lonDiff * (EXPAND_FACTOR - 1) / 2
        )
        val expandedNortheast = LatLng(
            bounds.northeast.latitude + latDiff * (EXPAND_FACTOR - 1) / 2,
            bounds.northeast.longitude + lonDiff * (EXPAND_FACTOR - 1) / 2
        )

        return LatLngBounds(expandedSouthwest, expandedNortheast)
    }

    private fun centerIsOutOfBounds(center: LatLng, bounds: LatLngBounds): Boolean =
        !bounds.contains(center)

    companion object {
        private const val ZOOM_THRESHOLD = 12.0
        private const val EXPAND_FACTOR = 3.0
        private const val DEBOUNCE_VALUE = 200L
    }
}
