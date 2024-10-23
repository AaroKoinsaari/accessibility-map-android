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

package com.aarokoinsaari.accessibilitymap.intent

import com.aarokoinsaari.accessibilitymap.model.Place

sealed class PlaceDetailIntent {
    data class MapClick(val place: Place) : PlaceDetailIntent()
    data class BackClick(val place: Place) : PlaceDetailIntent()
    data class FavouriteClick(val place: Place) : PlaceDetailIntent()
    data class OptionsClick(val place: Place) : PlaceDetailIntent()
}
