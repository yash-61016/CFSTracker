package com.teessideUni.cfs_tracker.presentation.ui.reportpage

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.lightColors
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.ui.Scaffold
import com.opencsv.CSVWriter
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.data.model.AverageHeartRateData
import com.teessideUni.cfs_tracker.data.model.AverageRespiratoryRateData
import com.teessideUni.cfs_tracker.data.model.HeartRateData
import com.teessideUni.cfs_tracker.data.model.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.presentation.ui.reportpage.RespiratoryRateReportVM.RespiratoryRateReportViewModel
import com.teessideUni.cfs_tracker.presentation.ui.reportpage.heartRateReportVM.HeartRateReportViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReportComponent(navController: NavController) {

    val viewModel: HeartRateReportViewModel = hiltViewModel()
    val respiratoryRateViewModel: RespiratoryRateReportViewModel = hiltViewModel()

    val stateHR = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val stateRR = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val selectState = remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    var context = LocalContext.current

    var heartRateDataList = remember { mutableStateListOf<HeartRateData>() }
    val averageHeartRateDataState =
        remember { mutableStateOf<List<AverageHeartRateData>>(emptyList()) }
    val averageHeartRateChangePercentage = remember { mutableStateOf("") }
    val isAverageHeartRateIncreased = remember { mutableStateOf(false) }

    var respiratoryRateDataList = remember { mutableStateListOf<RespiratoryRateDataValues>() }
    val averageRespiratoryRateDataState =
        remember { mutableStateOf<List<AverageRespiratoryRateData>>(emptyList()) }
    val averageRespiratoryRateChangePercentage = remember { mutableStateOf("") }
    val isAverageRespiratoryRateIncreased = remember { mutableStateOf(false) }

    val heartRateLoadingState = remember { mutableStateOf(true) }
    val respiratoryRateLoadingState = remember { mutableStateOf(true) }


    if (heartRateDataList.isEmpty()) {
        viewModel.getHeartRateDataList().let { data ->
            if (data.isNotEmpty()) heartRateDataList = data
        }
    }
    var arrayList: ArrayList<HeartRateData> = ArrayList()
    heartRateDataList.forEach { arrayList.add(it) }

    if (respiratoryRateDataList.isEmpty()) {
        respiratoryRateViewModel.getRespiratoryRateDataList().let { data ->
            if (data.isNotEmpty()) respiratoryRateDataList = data
        }
    }
    var arrayListRespiratoryRate: ArrayList<RespiratoryRateDataValues> = ArrayList()
    respiratoryRateDataList.forEach { arrayListRespiratoryRate.add(it) }


    LaunchedEffect(Unit) {
        heartRateLoadingState.value = true
        // Fetch the average heart rate data from the ViewModel
        val averageHeartRateData = viewModel.getAverageHeartRateDataForSixMonths()
        heartRateLoadingState.value = false
        averageHeartRateDataState.value = averageHeartRateData

        //get average % increase or decrease in heart rate compared current month with previous month.
        averageHeartRateChangePercentage.value =
            viewModel.calculateAverageHeartRateChangePercentage(averageHeartRateDataState.value)
        isAverageHeartRateIncreased.value =
            viewModel.isAverageHeartRateIncreased(averageHeartRateDataState.value)
    }

    LaunchedEffect(Unit) {
        respiratoryRateLoadingState.value = true
        // Fetch the average heart rate data from the ViewModel
        val averageRespiratoryRateData = respiratoryRateViewModel.getAverageRespiratoryRateDataForSixMonths()
        respiratoryRateLoadingState.value = false
        averageRespiratoryRateDataState.value = averageRespiratoryRateData

        //get average % increase or decrease in heart rate compared current month with previous month.
        averageRespiratoryRateChangePercentage.value =
            respiratoryRateViewModel.calculateAverageRespiratoryRateChangePercentage(averageRespiratoryRateDataState.value)
        isAverageRespiratoryRateIncreased.value =
            respiratoryRateViewModel.isAverageRespiratoryRateIncreased(averageRespiratoryRateDataState.value)
    }


    ModalBottomSheetLayout(
        sheetState = if( selectState.value == 1 ) stateHR else stateRR,
        sheetContent = {
            if( selectState.value == 1) {
                HeartRateReportContent(
                    viewModel = viewModel,
                    heartRateDataList = heartRateDataList,
                    onListUpdated = { arrayList = it as ArrayList<HeartRateData> },
                    context = context
                )
            } else {
                RespiratoryRateReportContent(
                    respiratoryRateViewModel = respiratoryRateViewModel,
                    respiratoryRateDataList = respiratoryRateDataList,
                    onRespiratoryRateListUpdated = { arrayListRespiratoryRate = it as ArrayList<RespiratoryRateDataValues> },
                    context = context
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        androidx.compose.material3.Text("Reports")
                    }
                )
            },
        ) {
            it
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            shape = MaterialTheme.shapes.small,
                            elevation = CardDefaults.cardElevation()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            )
                            {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Heart Rate Report",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(start = 5.dp, end = 15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            selectState.value = 1
                                            scope.launch {
                                                stateRR.hide()
                                                stateHR.show()
                                            }
                                        },
                                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top=8.dp)
                                            .height(40.dp),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        )
                                    ) {
                                        Text(
                                            text = "Download",
                                            modifier = Modifier.padding(start = 4.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 12.dp)
                                ) {
                                    val icon = if (isAverageHeartRateIncreased.value) {
                                        Icons.Default.KeyboardArrowUp
                                    } else {
                                        if (!heartRateLoadingState.value) {
                                            Icons.Default.KeyboardArrowDown
                                        } else {
                                            return@Row
                                        }
                                    }
                                    val valueColor = if (isAverageHeartRateIncreased.value) {
                                        Color(0, 178, 0) // Dark green
                                    } else {
                                        Color.Red
                                    }
                                    Text(
                                        text = averageHeartRateChangePercentage.value,
                                        color = valueColor,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = valueColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                MonthlyHeartRateComparisonGraph(
                                    data = averageHeartRateDataState.value,
                                    modifier = Modifier.fillMaxSize(),
                                    isLoading = heartRateLoadingState.value
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.padding(8.dp),
                            shape = MaterialTheme.shapes.small,
                            elevation = CardDefaults.cardElevation()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            )  {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    androidx.compose.material.Text(
                                        text = "Respiratory Rate Report",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.width(200.dp).padding(start = 5.dp, end = 15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            selectState.value = 2
                                            scope.launch {
                                                stateRR.show()
                                                stateHR.hide()
                                            }
                                        },
                                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top=8.dp)
                                            .height(40.dp),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        )
                                    ) {
                                        androidx.compose.material.Text(
                                            text = "Download",
                                            modifier = Modifier.padding(start = 4.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 12.dp)
                                ) {
                                    val icon = if (isAverageRespiratoryRateIncreased.value) {
                                        Icons.Default.KeyboardArrowUp
                                    } else {
                                        if (!respiratoryRateLoadingState.value) {
                                            Icons.Default.KeyboardArrowDown
                                        } else {
                                            return@Row
                                        }
                                    }
                                    val valueColor = if (isAverageRespiratoryRateIncreased.value) {
                                        Color(0, 178, 0) // Dark green
                                    } else {
                                        Color.Red
                                    }
                                    androidx.compose.material.Text(
                                        text = averageRespiratoryRateChangePercentage.value,
                                        color = valueColor,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = valueColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                MonthlyRespiratoryRateComparisonGraph(
                                    data = averageRespiratoryRateDataState.value,
                                    modifier = Modifier.fillMaxSize(),
                                    isLoading = respiratoryRateLoadingState.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun HeartRateReportContent(
    viewModel: HeartRateReportViewModel,
    heartRateDataList: List<HeartRateData>,
    onListUpdated: (List<HeartRateData>) -> Unit,
    context: Context
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material.MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Heart Rate Report Download",
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 20.dp, start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Button(
                onClick = {
                    writeHeartRateDataToCsv(
                        heartRateDataList = heartRateDataList,
                        context = context
                    )
                },
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp)
                    .height(30.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.download_csv),
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }

        Text(
            text = "Download your weekly heart rate report.",
            color = androidx.compose.material.MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 24.dp, end = 10.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(Modifier.padding(14.dp)) {
            Text(
                text = "Select the date",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 4.dp, end = 4.dp)
            ) {
                Icon(
                    Icons.Filled.Info, contentDescription = "Info Icon", modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )
                Text(
                    text = "Please note that upon selecting a date, the system will display all the relevant data for the entire week in which the selected date falls",
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily(Font(R.font.reemkufi)),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            HeartRateStyledDatePicker(
                viewModel = viewModel,
                heartRateDataList = heartRateDataList,
                onListUpdated = onListUpdated
            )
        }
    }
}

@Composable
fun RespiratoryRateReportContent(
    respiratoryRateViewModel: RespiratoryRateReportViewModel,
    respiratoryRateDataList: List<RespiratoryRateDataValues>,
    onRespiratoryRateListUpdated: (List<RespiratoryRateDataValues>) -> Unit,
    context: Context
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material.MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.material.Text(
                text = "Respiratory Rate Report Download",
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 18.sp
            )
            Button(
                onClick = {
                    writeRespiratoryRateDataToCsv(
                        respiratoryRateDataList = respiratoryRateDataList,
                        context = context
                    )
                },
                modifier = Modifier
                    .padding(top = 10.dp, start = 16.dp)
                    .height(30.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.download_csv),
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }

        androidx.compose.material.Text(
            text = "Download your weekly heart rate report.",
            color = androidx.compose.material.MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 24.dp, end = 10.dp),
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.reemkufi)),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(Modifier.padding(14.dp)) {
            androidx.compose.material.Text(
                text = "Select the date",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 20.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 4.dp, end = 4.dp)
            ) {
                Icon(
                    Icons.Filled.Info, contentDescription = "Info Icon", modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )
                androidx.compose.material.Text(
                    text = "Please note that upon selecting a date, the system will display all the relevant data for the entire week in which the selected date falls",
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily(Font(R.font.reemkufi)),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            RespiratoryRateStyledDatePicker(
                respiratoryRateViewModel = respiratoryRateViewModel,
                respiratoryRateDataList = respiratoryRateDataList,
                onListUpdated = onRespiratoryRateListUpdated
            )
        }
    }
}


@Composable
fun HeartRateStyledDatePicker(
    viewModel: HeartRateReportViewModel,
    heartRateDataList: List<HeartRateData>,
    onListUpdated: (List<HeartRateData>) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    val colors = if (isSystemInDarkTheme()) {
        darkColors()
    } else {
        lightColors()
    }

    val (isDialogOpen, setIsDialogOpen) = remember { mutableStateOf(false) }
    val (text, setText) = remember {
        mutableStateOf(
            TextFieldValue(
                selectedDate?.format(
                    DateTimeFormatter.ISO_LOCAL_DATE
                ) ?: ""
            )
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedWeekNumber by remember { mutableStateOf(currentWeekNumber) }


    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(2.dp))
        Icon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = "Calendar icon",
            modifier = Modifier.size(35.dp),
            tint = colors.onSurface
        )
        Spacer(Modifier.width(16.dp))
        Box(
            Modifier
                .clickable { setIsDialogOpen(true) }
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text.text,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (selectedDate == null) 0.5f else 1f)
                )
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = {
                    coroutineScope.launch {
                        viewModel.getHeartRateData(selectedYear, selectedWeekNumber)
                            .collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        val updatedList = result.data ?: emptyList()
                                        onListUpdated(updatedList)
                                    }

                                    else -> {}
                                }
                            }
                    }
                }) {
                    Icon(
                        Icons.Filled.Done,
                        null,
                        Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
    if (isDialogOpen) {
        DatePickerWrapper(
            onDismissRequest = { setIsDialogOpen(false) },
            onDateSet = { date ->
                date?.let {
                    selectedDate = it
                    selectedYear = it.year
                    selectedWeekNumber =
                        it.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
                    setText(TextFieldValue(it.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                }
            },
            initialDate = selectedDate,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Display list of HeartRateData using LazyColumn and Box
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(heartRateDataList.size) { index ->
            val heartRateData = heartRateDataList[index]
            HeartRateDataCard(heartRateData = heartRateData)
        }
    }
}

@Composable
fun RespiratoryRateStyledDatePicker(
    respiratoryRateViewModel: RespiratoryRateReportViewModel,
    respiratoryRateDataList: List<RespiratoryRateDataValues>,
    onListUpdated: (List<RespiratoryRateDataValues>) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    val colors = if (isSystemInDarkTheme()) {
        darkColors()
    } else {
        lightColors()
    }

    val (isDialogOpen, setIsDialogOpen) = remember { mutableStateOf(false) }
    val (text, setText) = remember {
        mutableStateOf(
            TextFieldValue(
                selectedDate?.format(
                    DateTimeFormatter.ISO_LOCAL_DATE
                ) ?: ""
            )
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedWeekNumber by remember { mutableStateOf(currentWeekNumber) }


    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(2.dp))
        Icon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = "Calendar icon",
            modifier = Modifier.size(35.dp),
            tint = colors.onSurface
        )
        Spacer(Modifier.width(16.dp))
        Box(
            Modifier
                .clickable { setIsDialogOpen(true) }
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material.Text(
                    text.text,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (selectedDate == null) 0.5f else 1f)
                )
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = {
                    coroutineScope.launch {
                        respiratoryRateViewModel.getRespiratoryRateData(selectedYear, selectedWeekNumber)
                            .collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        val updatedList = result.data ?: emptyList()
                                        onListUpdated(updatedList)
                                    }

                                    else -> {}
                                }
                            }
                    }
                }) {
                    Icon(
                        Icons.Filled.Done,
                        null,
                        Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
    if (isDialogOpen) {
        DatePickerWrapper(
            onDismissRequest = { setIsDialogOpen(false) },
            onDateSet = { date ->
                date?.let {
                    selectedDate = it
                    selectedYear = it.year
                    selectedWeekNumber =
                        it.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
                    setText(TextFieldValue(it.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                }
            },
            initialDate = selectedDate,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Display list of HeartRateData using LazyColumn and Box
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(respiratoryRateDataList.size) { index ->
            val RespiratoryRateData = respiratoryRateDataList[index]
            RespiratoryRateDataCard(respiratoryRateData = RespiratoryRateData)
        }
    }
}


private fun writeHeartRateDataToCsv(heartRateDataList: List<HeartRateData>, context: Context) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    val fileName = "heart_rate_data_${currentYear}_Week${currentWeekNumber}_$currentTime.csv"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { outputStream ->
                val csvWriter = CSVWriter(OutputStreamWriter(outputStream))
                csvWriter.writeNext(arrayOf("Timestamp", "Heart rate"))
                heartRateDataList.forEach { data ->
                    csvWriter.writeNext(
                        arrayOf(
                            data.timestamp.toString(),
                            data.heartRate.toString()
                        )
                    )
                }
                csvWriter.close()
            }
            Toast.makeText(
                context,
                "Downloaded Complete. Check your device downloads folder",
                Toast.LENGTH_LONG
            ).show()
        }
    } else {
        val csvFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        val csvWriter = CSVWriter(FileWriter(csvFile))
        csvWriter.writeNext(arrayOf("Timestamp", "Heart rate"))
        heartRateDataList.forEach { data ->
            csvWriter.writeNext(arrayOf(data.timestamp.toString(), data.heartRate.toString()))
        }
        csvWriter.close()
        Toast.makeText(
            context,
            "Downloaded Complete. Check your device downloads folder",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun writeRespiratoryRateDataToCsv(respiratoryRateDataList: List<RespiratoryRateDataValues>, context: Context) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    val fileName = "respiratory_rate_data_${currentYear}_Week${currentWeekNumber}_$currentTime.csv"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { outputStream ->
                val csvWriter = CSVWriter(OutputStreamWriter(outputStream))
                csvWriter.writeNext(arrayOf("Timestamp", "Heart rate"))
                respiratoryRateDataList.forEach { data ->
                    csvWriter.writeNext(
                        arrayOf(
                            data.timestamp.toString(),
                            data.rateValue.toString()
                        )
                    )
                }
                csvWriter.close()
            }
            Toast.makeText(
                context,
                "Downloaded Complete. Check your device downloads folder",
                Toast.LENGTH_LONG
            ).show()
        }
    } else {
        val csvFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        val csvWriter = CSVWriter(FileWriter(csvFile))
        csvWriter.writeNext(arrayOf("Timestamp", "Respiratory rate"))
        respiratoryRateDataList.forEach { data ->
            csvWriter.writeNext(arrayOf(data.timestamp.toString(), data.rateValue.toString()))
        }
        csvWriter.close()
        Toast.makeText(
            context,
            "Downloaded Complete. Check your device downloads folder",
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun HeartRateDataCard(heartRateData: HeartRateData) {

    val formattedHeartRate = String.format("%.1f", heartRateData.heartRate)
    val dateTimeString = SimpleDateFormat("EEE MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(heartRateData.timestamp)

   Card(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .padding(end = 8.dp),
            )
            Column {
                Text(
                    text = "Heart Rate: $formattedHeartRate bpm",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Timestamp: $dateTimeString",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun RespiratoryRateDataCard(respiratoryRateData: RespiratoryRateDataValues) {

    val formattedRespiratoryRate = String.format("%.1f", respiratoryRateData.rateValue)
    val dateTimeString = SimpleDateFormat("EEE MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(respiratoryRateData.timestamp)

    Card(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.lungs_svgrepo_com),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .padding(end = 8.dp),
            )
            Column {
                Text(
                    text = "Respiratory Rate: $formattedRespiratoryRate",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Timestamp: $dateTimeString",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}


@Composable
fun DatePickerWrapper(
    onDismissRequest: () -> Unit,
    onDateSet: (LocalDate?) -> Unit,
    initialDate: LocalDate?
) {
    val dialog = remember { mutableStateOf(true) }
    val context = LocalContext.current

    if (dialog.value) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSet(date)
            },
            initialDate?.year ?: LocalDate.now().year,
            initialDate?.monthValue?.minus(1) ?: LocalDate.now().monthValue.minus(1),
            initialDate?.dayOfMonth ?: LocalDate.now().dayOfMonth
        ).apply {
            setOnDismissListener {
                onDismissRequest()
                dialog.value = false
            }
            show()
        }
    }
}

@SuppressLint("InternalInsetResource")
@Composable
fun getStatusBarSize(): Dp {
    val resourceId =
        LocalContext.current.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) LocalContext.current.resources.getDimensionPixelSize(resourceId).dp else 0.dp
}


