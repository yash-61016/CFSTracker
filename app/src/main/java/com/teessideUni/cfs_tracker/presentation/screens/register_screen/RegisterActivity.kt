package com.teessideUni.cfs_tracker.presentation.screens.register_screen

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.domain.repository.ValidationUtils.Companion.isValidCurrentPassword
import com.teessideUni.cfs_tracker.domain.repository.ValidationUtils.Companion.isValidEmail
import com.teessideUni.cfs_tracker.domain.repository.ValidationUtils.Companion.isValidName
import com.teessideUni.cfs_tracker.domain.repository.ValidationUtils.Companion.isValidPassword
import com.teessideUni.cfs_tracker.domain.repository.ValidationUtils.Companion.isValidPhoneNumber
import com.teessideUni.cfs_tracker.domain.util.keyboardAsState
import com.teessideUni.cfs_tracker.ui.theme.InputBoxShape
import com.teessideUni.cfs_tracker.ui.theme.SecondaryColor
import com.teessideUni.cfs_tracker.ui.theme.primaryColor
import com.teessideUni.cfs_tracker.ui.theme.whiteBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterScene(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {

    // value
    val nameValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val phoneValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    val confirmPasswordValue = remember { mutableStateOf("") }

    // error State
    val nameError = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    val phoneError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val confirmPwdNotMatch = remember { mutableStateOf(false) }

    // configuration
    val passwordVisibility = remember { mutableStateOf(false) }
    val confirmPasswordVisibility = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()
    val isKeyboardVisible = keyboardAsState() // Keyboard.Opened or Keyboard.Closed
    val context = LocalContext.current
    val state = viewModel.registerState.collectAsState(initial = null)

    // Animation
    val transition = updateTransition(targetState = isKeyboardVisible, label = "transition")
    val imageSize by transition.animateDp(
        transitionSpec = {
            if (false isTransitioningTo true) {
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                tween(durationMillis = 300)
            }
        }, label = "LoginImage"
    ) { if (it) 150.dp else 300.dp }

    // This function clears all the mutable state values
    fun clearAllValues() {
        nameValue.value = ""
        emailValue.value = ""
        phoneValue.value = ""
        passwordValue.value = ""
        confirmPasswordValue.value = ""
    }

    // UI Design
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_register_img),
                contentDescription = "Login_Page_Image",
                modifier = Modifier
                    .size(imageSize)
                    .padding(top = 30.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.70f)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(whiteBackground)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Sign Up", fontSize = 30.sp,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = nameValue.value,
                            onValueChange = {
                                nameValue.value = it
                                nameError.value = !isValidName(it)
                            },
                            label = { Text(text = "Name") },
                            placeholder = { Text(text = "Name") },
                            singleLine = true,
                            shape = InputBoxShape.medium,
                            leadingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.user_icon),
                                        contentDescription = "",
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(6.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(primaryColor)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            isError = nameError.value,
                            colors = if (nameError.value) TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Red,
                                errorBorderColor = Color.Red
                            ) else TextFieldDefaults.outlinedTextFieldColors()
                        )

                        OutlinedTextField(
                            value = emailValue.value,
                            onValueChange = {
                                emailValue.value = it
                                emailError.value = !isValidEmail(it)
                            },
                            label = { Text(text = "Email Address") },
                            placeholder = { Text(text = "Email Address") },
                            singleLine = true,
                            shape = InputBoxShape.medium,
                            leadingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_email_outline),
                                        contentDescription = "",
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(6.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(primaryColor)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            isError = emailError.value,
                            colors = if (emailError.value) TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Red,
                                errorBorderColor = Color.Red
                            ) else TextFieldDefaults.outlinedTextFieldColors()
                        )

                        OutlinedTextField(
                            value = phoneValue.value,
                            onValueChange = {
                                phoneValue.value = it
                                phoneError.value = !isValidPhoneNumber(it)
                            },
                            label = { Text(text = "Phone Number") },
                            placeholder = { Text(text = "Phone Number (Prefix(Code: +44)") },
                            shape = InputBoxShape.medium,
                            leadingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.phone_icon),
                                        contentDescription = "",
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(6.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(primaryColor)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            isError = phoneError.value,
                            colors = if (phoneError.value) TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Red,
                                errorBorderColor = Color.Red
                            ) else TextFieldDefaults.outlinedTextFieldColors()
                        )

                        OutlinedTextField(
                            value = passwordValue.value,
                            onValueChange = {
                                passwordValue.value = it
                                passwordError.value = !isValidPassword(it)
                            },
                            label = { Text(text = "Password") },
                            placeholder = { Text(text = "Password") },
                            shape = InputBoxShape.medium,
                            leadingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_lock),
                                        contentDescription = "",
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(6.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(primaryColor)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility.value = !passwordVisibility.value
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.password_eye),
                                        contentDescription = "EyeImage",
                                        tint = if (passwordVisibility.value) primaryColor else Color.Gray
                                    )
                                }
                            },
                            isError = passwordError.value,
                            visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            colors = if (passwordError.value) TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Red,
                                errorBorderColor = Color.Red
                            ) else TextFieldDefaults.outlinedTextFieldColors()
                        )

                        OutlinedTextField(
                            value = confirmPasswordValue.value,
                            onValueChange = {
                                confirmPasswordValue.value = it
                                confirmPwdNotMatch.value = !isValidCurrentPassword(
                                    confirmPasswordValue.value,
                                    passwordValue.value
                                )
                            },
                            label = { Text(text = "Confirm Password") },
                            placeholder = { Text(text = "Confirm Password") },
                            shape = InputBoxShape.medium,
                            leadingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_lock),
                                        contentDescription = "",
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(6.dp)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(primaryColor)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    confirmPasswordVisibility.value =
                                        !confirmPasswordVisibility.value
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.password_eye),
                                        contentDescription = "EyeImage",
                                        tint = if (confirmPasswordVisibility.value) primaryColor else Color.Gray
                                    )
                                }
                            },
                            isError = confirmPwdNotMatch.value,
                            visualTransformation = if (confirmPasswordVisibility.value) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            colors = if (passwordError.value) TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.Red,
                                errorBorderColor = Color.Red
                            ) else TextFieldDefaults.outlinedTextFieldColors()
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.registerUser(
                                        emailValue.value,
                                        passwordValue.value,
                                        nameValue.value,
                                        phoneValue.value
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Sign Up", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.padding(5.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (state.value?.isLoading == true) {
                                CircularProgressIndicator()
                            }
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        Text(
                            text = "Already Have an account? Sign In.",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = {
                                navController.navigate("login_page") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            })
                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = state.value?.isSuccess) {
        if (isValidName(nameValue.value) && isValidEmail(emailValue.value) && isValidPhoneNumber(
                phoneValue.value
            ) && isValidPassword(passwordValue.value) && isValidCurrentPassword(
                confirmPasswordValue.value,
                passwordValue.value
            )
        ) {
            coroutineScope.launch {
                if (state.value?.isSuccess?.isNotEmpty() == true) {
                    val success = state.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    navController.navigate("login_page") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    // Clear all values here
                    clearAllValues()
                }
            }
        }
    }
    LaunchedEffect(key1 = state.value?.isError) {
        coroutineScope.launch {
            if (state.value?.isError?.isNotBlank() == true) {
                val error = state.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(nameValue.value) {
        delay(2000) // Wait for 1 second after the user finishes typing
        if (nameError.value) {
            Toast.makeText(context, "Invalid name", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(emailValue.value) {
        delay(2000) // Wait for 1 second after the user finishes typing
        if (emailError.value) {
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(phoneValue.value) {
        delay(2000) // Wait for 1 second after the user finishes typing
        if (phoneError.value) {
            Toast.makeText(context, "Invalid Phone number! Apply Country code e.g +4476.. and No spaces", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(passwordValue.value) {
        delay(2000) // Wait for 1 second after the user finishes typing
        if (passwordError.value) {
            Toast.makeText(context, "Password should be min 8 character long. Should have min 1 Uppercase, 1 Lowercase and 1 Special Character.", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(confirmPasswordValue.value) {
        delay(2000) // Wait for 1 second after the user finishes typing
        if (confirmPwdNotMatch.value) {
            Toast.makeText(context, "Passwords don't Match.", Toast.LENGTH_SHORT).show()
        }
    }
}





















