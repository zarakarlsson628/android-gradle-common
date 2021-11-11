package io.homeassistant.companion.android.home.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import io.homeassistant.companion.android.R
import io.homeassistant.companion.android.common.data.integration.Entity
import io.homeassistant.companion.android.util.RotaryEventDispatcher
import io.homeassistant.companion.android.util.RotaryEventState
import io.homeassistant.companion.android.util.setChipDefaults

@ExperimentalWearMaterialApi
@Composable
fun MainView(
    entities: List<Entity<*>>,
    favoriteEntityIds: List<String>,
    onEntityClicked: (String) -> Unit,
    onSettingsClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()

    var expandedFavorites: Boolean by rememberSaveable { mutableStateOf(true) }
    var expandedInputBooleans: Boolean by rememberSaveable { mutableStateOf(true) }
    var expandedLights: Boolean by rememberSaveable { mutableStateOf(true) }
    var expandedScenes: Boolean by rememberSaveable { mutableStateOf(true) }
    var expandedScripts: Boolean by rememberSaveable { mutableStateOf(true) }
    var expandedSwitches: Boolean by rememberSaveable { mutableStateOf(true) }

    val entityMap: Map<String, Entity<*>> = entities.map { it.entityId to it }.toMap()

    val scenes = entities.filter { it.entityId.split(".")[0] == "scene" }
    val scripts = entities.filter { it.entityId.split(".")[0] == "script" }
    val lights = entities.filter { it.entityId.split(".")[0] == "light" }
    val inputBooleans = entities.filter { it.entityId.split(".")[0] == "input_boolean" }
    val switches = entities.filter { it.entityId.split(".")[0] == "switch" }

    RotaryEventDispatcher(scalingLazyListState)
    RotaryEventState(scrollState = scalingLazyListState)

    Scaffold(
        positionIndicator = {
            if (scalingLazyListState.isScrollInProgress)
                PositionIndicator(scalingLazyListState = scalingLazyListState)
        },
        timeText = {
            if (!scalingLazyListState.isScrollInProgress)
                TimeText()
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = 10.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 40.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scalingLazyListState
        ) {
            if (favoriteEntityIds.isNotEmpty()) {
                item {
                    ListHeader(
                        stringId = R.string.favorites,
                        expanded = expandedFavorites,
                        onExpandChanged = { expandedFavorites = it }
                    )
                }
                if (expandedFavorites) {
                    items(favoriteEntityIds.size) { index ->
                        val favoriteEntityID = favoriteEntityIds[index].split(",")[0]
                        if (entities.isNullOrEmpty()) {
                            // Use a normal chip when we don't have the state of the entity
                            Chip(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 0.dp),
                                icon = {
                                    Image(
                                        asset = CommunityMaterial.Icon.cmd_cellphone
                                    )
                                },
                                label = {
                                    Text(
                                        text = favoriteEntityID,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = { onEntityClicked(favoriteEntityID) },
                                colors = ChipDefaults.primaryChipColors(
                                    backgroundColor = colorResource(id = R.color.colorAccent),
                                    contentColor = Color.Black
                                )
                            )
                        } else {
                            EntityUi(
                                entityMap[favoriteEntityID]!!,
                                onEntityClicked
                            )
                        }
                    }
                }
            }
            if (entities.isNullOrEmpty()) {
                item {
                    Column {
                        ListHeader(id = R.string.loading)
                        Chip(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    start = 10.dp,
                                    end = 10.dp
                                ),
                            label = {
                                Text(
                                    text = stringResource(R.string.loading_entities),
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = { /* No op */ },
                            colors = setChipDefaults()
                        )
                    }
                }
            }
            if (inputBooleans.isNotEmpty()) {
                item {
                    ListHeader(
                        stringId = R.string.input_booleans,
                        expanded = expandedInputBooleans,
                        onExpandChanged = { expandedInputBooleans = it }
                    )
                }
                if (expandedInputBooleans) {
                    items(inputBooleans.size) { index ->
                        EntityUi(inputBooleans[index], onEntityClicked)
                    }
                }
            }
            if (lights.isNotEmpty()) {
                item {
                    ListHeader(
                        stringId = R.string.lights,
                        expanded = expandedLights,
                        onExpandChanged = { expandedLights = it }
                    )
                }
                if (expandedLights) {
                    items(lights.size) { index ->
                        EntityUi(lights[index], onEntityClicked)
                    }
                }
            }
            if (scenes.isNotEmpty()) {
                item {
                    ListHeader(
                        stringId = R.string.scenes,
                        expanded = expandedScenes,
                        onExpandChanged = { expandedScenes = it }
                    )
                }
                if (expandedScenes) {
                    items(scenes.size) { index ->
                        EntityUi(scenes[index], onEntityClicked)
                    }
                }
            }
            if (scripts.isNotEmpty()) {
                item {
                    ListHeader(
                        stringId = R.string.scripts,
                        expanded = expandedScripts,
                        onExpandChanged = { expandedScripts = it }
                    )
                }
                if (expandedScripts) {
                    items(scripts.size) { index ->
                        EntityUi(scripts[index], onEntityClicked)
                    }
                }
            }
            if (switches.isNotEmpty()) {
                item {
                    ListHeader(
                        stringId = R.string.switches,
                        expanded = expandedSwitches,
                        onExpandChanged = { expandedSwitches = it }
                    )
                }
                if (expandedSwitches) {
                    items(switches.size) { index ->
                        EntityUi(switches[index], onEntityClicked)
                    }
                }
            }
            item {
                OtherSection(
                    onSettingsClicked = onSettingsClicked,
                    onLogoutClicked = onLogoutClicked
                )
            }
        }
    }
}
