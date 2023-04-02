
package com.teessideUni.cfs_tracker.ui.theme.screens

import android.view.KeyEvent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.domain.util.keyboardAsState
import com.teessideUni.cfs_tracker.ui.theme.primaryColor
import com.teessideUni.cfs_tracker.ui.theme.whiteBackground
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController) {

    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = BringIntoViewRequester()

    val isKeyboardVisible = keyboardAsState() // Keyboard.Opened or Keyboard.Closed

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
                .background(whiteBackground)
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
                                        tint = if (passwordVisibility.value) primaryColor else Color.Gray,
                                        contentDescription = "EyeIcon"
                                    )
                                }
                            },
                            label = { Text("Password") },
                            placeholder = { Text(text = "Password") },
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
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = "Sign In", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.padding(20.dp))
                        //To do register navigation:
                        Text(
                            text = "Create An Account",
                            modifier = Modifier.clickable(onClick = {
                                navController.navigate("register_screen") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            })
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "Forget Password",
                            modifier = Modifier.clickable(onClick = {
                                navController.navigate("forget_password_screen") {
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
}
