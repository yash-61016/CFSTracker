package com.teessideUni.cfs_tracker.presentation.screens.login_screen

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.domain.util.keyboardAsState
import com.teessideUni.cfs_tracker.ui.theme.InputBoxShape
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    // value
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }

    // configuration
    val passwordVisibility = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()
    val isKeyboardVisible = keyboardAsState() // Keyboard.Opened or Keyboard.Closed
    val context = LocalContext.current
    val state = viewModel.loginState.collectAsState(initial = null)

    // animation
    val transition = updateTransition(targetState = isKeyboardVisible, label = "transition")
    val imageSize by transition.animateDp(
        transitionSpec = {
            if (false isTransitioningTo true) {
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                tween(durationMillis = 100)
            }
        }, label = "LoginImage"
    ) { if (it) 250.dp else 400.dp }

    // This function clears all the mutable state values
    fun clearAllValues() {
        emailValue.value = ""
        passwordValue.value = ""
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
                painter = painterResource(id = R.drawable.login_image),
                contentDescription = "Login_Page_Image",
                modifier = Modifier
                    .size(imageSize)
                    .padding(top = 30.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(MaterialTheme.colors.background)
                .padding(10.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Sign In",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        fontSize = 30.sp
                    )
                    Spacer(modifier = Modifier.padding(20.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f) // set equal weight for both text fields
                    ) {
                        OutlinedTextField(
                            value = emailValue.value,
                            onValueChange = { emailValue.value = it },
                            label = { Text(text = "Email Address") },
                            placeholder = { Text(text = "Email Address") },
                            shape = InputBoxShape.medium,
                            leadingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_email_outline),
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.primary,
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
                                            .background(MaterialTheme.colors.primary,)
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
                        )

                        OutlinedTextField(
                            value = passwordValue.value,
                            onValueChange = { passwordValue.value = it },
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility.value = !passwordVisibility.value
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.password_eye),
                                        tint = if (passwordVisibility.value) MaterialTheme.colors.primary else Color.Gray,
                                        contentDescription = "EyeIcon"
                                    )
                                }
                            },
                            label = { Text("Password") },
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
                                        tint = MaterialTheme.colors.primary,
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
                                            .background(MaterialTheme.colors.primary,)
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Text,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                },
                            ),
                            visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusEvent { event ->
                                    if (event.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                }

                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.loginUser(emailValue.value, passwordValue.value)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Sign In", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.padding(20.dp))

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
                            text = "Don't have an Account? Sign Up.",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = {
                                navController.navigate("register_page") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            })
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Text(
                            text = "Forget Password",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = {
                                navController.navigate("forget_password_page") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            })
                        )
                        Spacer(modifier = Modifier.padding(20.dp))
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = state.value?.isSuccess) {
        coroutineScope.launch {
            if (state.value?.isSuccess?.isNotEmpty() == true) {
                val success = state.value?.isSuccess
                Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                navController.navigate("home_page") {
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
    LaunchedEffect(key1 = state.value?.isError) {
        coroutineScope.launch {
            if (state.value?.isError?.isNotBlank() == true) {
                val error = state.value?.isError
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
            }
        }
    }
}