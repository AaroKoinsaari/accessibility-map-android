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

package com.aarokoinsaari.accessibilitymap.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.aarokoinsaari.accessibilitymap.utils.PlaceCategory

@Entity(tableName = "places")
data class Place(
    @PrimaryKey val id: Long,
    val name: String,
    val category: PlaceCategory,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>?,
    val accessibility: AccessibilityInfo
)

@Entity(tableName = "places_fts")
@Fts4(contentEntity = Place::class)
data class PlaceFts(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Long,
    val name: String
)
