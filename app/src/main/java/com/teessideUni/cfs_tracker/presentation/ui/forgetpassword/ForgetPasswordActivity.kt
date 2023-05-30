package com.teessideUni.cfs_tracker.presentation.ui.forgetpassword

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.ui.forgetpassword.forgetPasswordVM.ForgetPasswordViewModel
import com.teessideUni.cfs_tracker.domain.util.keyboardAsState
import com.teessideUni.cfs_tracker.ui.theme.InputBoxShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgetPasswordViewModel = hiltViewModel()
) {
    val emailValue = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()
    val isKeyboardVisible = keyboardAsState()
    val context = LocalContext.current
    val state = viewModel.forgetPwdState.collectAsState(initial = null)
    // This function clears all the mutable state values
    fun clearAllValues() {
        emailValue.value = ""
    }

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
    ) { if (it) 230.dp else 400.dp }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_forgot_password_illustration),
                contentDescription = "forget_password_image",
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
                .fillMaxHeight(0.58f)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .padding(10.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "FORGOT YOUR PASSWORD?",
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        fontSize = 20.sp
                    )
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp, // Specify the desired elevation value here
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(vertical = 20.dp)
                        ) {
                            Text(
                                text = "Enter your registered email below to receive password reset instruction",
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            OutlinedTextField(
                                value = emailValue.value, onValueChange = { emailValue.value = it },
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 20.dp)
                                    .onFocusEvent { event ->
                                        if (event.isFocused) {
                                            coroutineScope.launch {
                                                bringIntoViewRequester.bringIntoView()
                                            }
                                        }
                                    },
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor =  MaterialTheme.colorScheme.onBackground,
                                    cursorColor =  MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = InputBoxShape.medium,
                                singleLine = true,
                                leadingIcon = {
                                    Row(
                                        modifier = Modifier.padding(start = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_email_outline),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary,
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
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                },
                                placeholder = {
                                    Text(text = "Email Address", color = Color.Gray)
                                },
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                    }
                                ),
                            )

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.forgetPassword(emailValue.value)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 20.dp),
                                contentPadding = PaddingValues(vertical = 14.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 2.dp
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = "Send Reset Link",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (state.value?.isLoading == true) {
                            CircularProgressIndicator()
                        }
                    }

                    TextButton(
                        onClick = {
                            navController.navigate("login_page") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(
                            text = "Return to Login!",
                            color =  Color.DarkGray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
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
                navController.navigate("forget_password_page") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
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