package org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui.components.ShowCard
import org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui.components.TeiCountComponent
import org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui.components.Toolbar
import org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui.components.ToolbarActionState
import org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui.components.ToolbarHeaders

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RelatonshipScreen(){
        Scaffold(
            topBar = {
                Toolbar(
                    //headers = state.headers,
                    headers = ToolbarHeaders(
                         title = "2023-1244445",
                        subtitle ="Comunidade de Matembissa"
                    ),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF2C98F0),
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationAction = { "navigateBackAction.invoke()" },
                    disableNavigation = false,
                    actionState = ToolbarActionState(filterVisibility = false),
                    syncAction = {  },
                )
            },
            containerColor = Color(0xFF2C98F0),
            // Add a FloatingActionButton
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Handle click here
                    },
                    containerColor = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(Color.White, shape = CircleShape),
                    icon = {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = null, tint = Color(0xFF2C98F0))
                    },
                    text = {
                        Text(text = "Adicionar novo", color = Color(0xFF2C98F0))
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFF2C98F0))
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium
                                .copy(
                                    topStart = CornerSize(16.dp),
                                    topEnd = CornerSize(16.dp),
                                    bottomStart = CornerSize(0.dp),
                                    bottomEnd = CornerSize(0.dp)
                                )
                        )
                        .padding(vertical = 16.dp, horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text ="Membros do Agregado",
                        maxLines = 1,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(.8f),
                        modifier = Modifier.padding(16.dp)
                    )
                    ShowCard(
                        checked = true,
                        name = "Nelson Chadaly",
                        gender = "Masculino",
                        age = "10"
                    )
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TeiCountComponent(
                            teiCount = 10
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        /*LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(Utils.columnCount(configuration))
                        ) {
                            itemsIndexed(state.cycles) { index, cycle ->
                                CycleItem(
                                    cycle = cycle,
                                    label = "${index+1}"
                                ) {
                                    navigateToMembers.invoke()
                                }
                            }
                        }*/
                    }
                }
            }
        }
    }