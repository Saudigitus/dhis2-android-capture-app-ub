package org.dhis2.usescases.teiDashboard.dashboardfragments.relationships.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dhis2.R


@Preview
@Composable
fun ItemCountrev(){
    TeiCountComponent(
        teiCount = 10
    )
}
@Preview
@Composable
fun CardItemPrev(){
    ShowCard(
        checked = true,
        name = "Nelson Chadaly",
        gender = "Masculino",
        age = "10"
    )
}

@Composable
fun TeiCountComponent(
    teiCount: Int = 0,
    imageVector: ImageVector = Icons.Outlined.Person
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xBFB0D1EC),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(CircleShape),

        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Image",
            tint = Color(0xFF2C98F0)
        )
        Text(
            text = "$teiCount Membros",
            color = Color(0xFF2C98F0),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun RoundedIcon(
    painter: Painter? = null,
    label: String? = null,
    colorResource: Int = R.color.colorPrimary
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(color = colorResource(colorResource))
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$label", color = Color.White)
    }
}

data class InfoCard(
    val grade: String = "",
    val section: String = "",
    val academicYear: String = "",
    val orgUnitName: String = "",
    val teiCount: Int = 0
)

@Composable
fun ShowCard(
    checked: Boolean = true,
    name: String? = null,
    gender: String? = null,
    age: String? = null,
){
    Card (
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)) {
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){

                Row {
                    RoundedIcon(label = "M")
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Row {
                            Text(
                                text = "Nome: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$name",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        Row {
                            Text(
                                text = "Sexo: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$gender",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        Row {
                            Text(
                                text = "Idade: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$age anos",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Column (
                        modifier = Modifier.fillMaxWidth(). height(40.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ){
                       Row {
                           if (checked) {
                               Icon(
                                   Icons.Rounded.CheckCircle,
                                   tint = Color.Green,
                                   contentDescription = "Icon"
                               )

                           }
                           Icon(
                               Icons.Rounded.DeleteForever,
                               tint = Color.Red,
                               contentDescription = "Icon"
                           )
                       }
                    }
                }
            }
        }
    }
}