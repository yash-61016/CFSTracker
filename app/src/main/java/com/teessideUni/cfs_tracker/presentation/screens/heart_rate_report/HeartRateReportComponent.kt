package com.teessideUni.cfs_tracker.presentation.screens.heart_rate_report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material.ModalBottomSheetState
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
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
import com.opencsv.CSVWriter
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.data.local.AverageHeartRateData
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.use_cases.view_models.heartRateReportVM.HeartRateReportViewModel
import kotlinx.coroutines.CoroutineScope
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


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HeartRateReportComponent(navController: NavController) {

    val viewModel: HeartRateReportViewModel = hiltViewModel()
    var heartRateDataList = remember { mutableStateListOf<HeartRateData>() }
    val averageHeartRateDataState = remember { mutableStateOf<List<AverageHeartRateData>>(emptyList()) }
    val averageHeartRateChangePercentage = remember { mutableStateOf(" ") }
    val isAverageHeartRateIncreased = remember { mutableStateOf(false) }

    val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var context = LocalContext.current
    val statusBarHeight = getStatusBarSize()
    val loadingState = remember { mutableStateOf(true) }

    if (heartRateDataList.isEmpty()) {
        viewModel.getHeartRateDataList().let { data ->
            if (data.isNotEmpty()) heartRateDataList = data
        }
    }
    var arrayList: ArrayList<HeartRateData> = ArrayList()
    heartRateDataList.forEach { arrayList.add(it) }



    LaunchedEffect(Unit) {
        loadingState.value = true
        // Fetch the average heart rate data from the ViewModel
        val averageHeartRateData = viewModel.getAverageHeartRateDataForThreeMonths()
        loadingState.value = false
        averageHeartRateDataState.value = averageHeartRateData

        //get average % increase or decrease in heart rate compared current month with previous month.
        averageHeartRateChangePercentage.value = viewModel.calculateAverageHeartRateChangePercentage(averageHeartRateDataState.value)
        isAverageHeartRateIncreased.value = viewModel.isAverageHeartRateIncreased(averageHeartRateDataState.value)
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            HeartRateReportContent(
                viewModel = viewModel,
                heartRateDataList = heartRateDataList,
                onListUpdated = { arrayList = it as ArrayList<HeartRateData> },
                context = context
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(top = statusBarHeight - 80.dp, start = 10.dp, end = 10.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                    )
                    {
                        ComponentHeading(
                            navController = navController,
                            heartRateDataList = heartRateDataList,
                            context = context,
                            state = state,
                            scope = scope
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            val icon = if (isAverageHeartRateIncreased.value) {
                                Icons.Default.KeyboardArrowUp
                            } else {
                                if (!loadingState.value) {
                                    Icons.Default.KeyboardArrowDown
                                }else{
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
                                textAlign = TextAlign.Start,
                                color = valueColor,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = valueColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        MonthlyHeartRateComparisonGraph(
                            data = averageHeartRateDataState.value,
                            modifier = Modifier.fillMaxSize(),
                            isLoading =  loadingState.value
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComponentHeading(navController: NavController, heartRateDataList: List<HeartRateData>, context: Context, state: ModalBottomSheetState, scope: CoroutineScope) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Heart Rate Report",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 5.dp,end = 15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                scope.launch { state.show() }
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(start = 10.dp,end = 10.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Download",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@SuppressLint("InternalInsetResource")
@Composable
fun getStatusBarSize(): Dp {
    val resourceId = LocalContext.current.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) LocalContext.current.resources.getDimensionPixelSize(resourceId).dp else 0.dp
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
                    csvWriter.writeNext(arrayOf(data.timestamp.toString(), data.heartRate.toString()))
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
        val csvFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeartRateReportContent(viewModel: HeartRateReportViewModel, heartRateDataList: List<HeartRateData>, onListUpdated: (List<HeartRateData>) -> Unit, context: Context)
{

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
                text = "HeartRate Report Download",
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
                    writeHeartRateDataToCsv(heartRateDataList = heartRateDataList, context = context)
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

        Text(
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
            Text(text = "Select the date",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 20.sp
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 4.dp, end = 4.dp)) {
                Icon(Icons.Filled.Info, contentDescription = "Info Icon", modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp))
                Text(
                    text = "Please note that upon selecting a date, the system will display all the relevant data for the entire week in which the selected date falls",
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily(Font(R.font.reemkufi)),
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))

            StyledDatePicker(viewModel = viewModel, heartRateDataList = heartRateDataList, onListUpdated = onListUpdated)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StyledDatePicker(
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
    val (text, setText) = remember { mutableStateOf(
        TextFieldValue(selectedDate?.format(
            DateTimeFormatter.ISO_LOCAL_DATE) ?: "")
    ) }
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
                    Icon(Icons.Filled.Done, null,
                        Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp), tint = MaterialTheme.colorScheme.onPrimary)
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
                    selectedWeekNumber = it.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
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
fun HeartRateDataCard(heartRateData: HeartRateData) {

    val formattedHeartRate = String.format("%.1f", heartRateData.heartRate)
    val dateTimeString = SimpleDateFormat("EEE MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(heartRateData.timestamp)

    androidx.compose.material.Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = androidx.compose.material.MaterialTheme.shapes.large,
        elevation = 5.dp,
        backgroundColor = androidx.compose.material.MaterialTheme.colors.surface
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
                    style = androidx.compose.material.MaterialTheme.typography.body1,
                    color = androidx.compose.material.MaterialTheme.colors.onSurface,
                )
                Text(
                    text = "Timestamp: $dateTimeString",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                )
            }
        }
    }
}