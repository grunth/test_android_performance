package com.example.testjetpackrecyclerpeformance

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskItemView(task: Task) {
    Card(
        modifier = Modifier
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
            .fillMaxWidth()
            .clickable {
                Log.v("CLICK_TEST", "CLICKED")
            },
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(0.dp, color = colorResource(id = R.color.cardStrokeColor)),
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp, 16.dp, 0.dp, 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_task_card_active_task),
                    contentDescription = "start",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_activ_task_stop_24dp),
                    contentDescription = "stop",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_attach_file_24dp),
                    contentDescription = "attach",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_task_card_jeopardy),
                    contentDescription = "start",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_task_card_task_open_no_dependencies),
                    contentDescription = "start",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_task_card_task_reserved),
                    contentDescription = "start",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_assigned_tasks_menu_icon),
                    contentDescription = "start",
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(6.dp)
                )
            }

            Row(
                modifier = Modifier
                    .padding(5.dp)
            ) {
                Column {
                    itemsText()
                    itemsText()
                    itemsText()
                    itemsText()
                    itemsText()
                }
            }
        }

    }
}

@Composable
fun itemsText() {
    Text(
        modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp),
        text = "VZGrund",
        color = colorResource(id = R.color.primaryColor),
        fontSize = 14.sp
    )
    Text(
        modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp),
        text = stringResource(id = R.string.settings_fragment_rest_all_dialog_message),
        color = colorResource(id = R.color.primaryColor),
        fontSize = 18.sp
    )
    Divider(
        modifier = Modifier
            .padding(16.dp, 2.dp, 16.dp, 0.dp),
        thickness = 1.dp,
        color = colorResource(id = R.color.separatorStyleColor)
    )
}