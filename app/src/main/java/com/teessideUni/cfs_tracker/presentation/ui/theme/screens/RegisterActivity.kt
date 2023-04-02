package com.teessideUni.cfs_tracker.ui.theme.screens

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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.domain.util.keyboardAsState
import com.teessideUni.cfs_tracker.ui.theme.primaryColor
import com.teessideUni.cfs_tracker.ui.theme.whiteBackground
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterScene(navController: NavController) {

    val nameValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val phoneValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    val confirmPasswordValue = remember { mutableStateOf("") }

    val passwordVisibility = remember { mutableStateOf(false) }
    val confirmPasswordVisibility = remember { mutableStateOf(false) }

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
                tween(durationMillis = 300)
            }
        }, label = "LoginImage"
    ) { if (it) 150.dp else 300.dp }


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
                    Spacer(modifier = Modifier.padding(20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = nameValue.value,
                            onValueChange = { nameValue.value = it },
                            label = { Text(text = "Name") },
                            placeholder = { Text(text = "Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onFocusEvent { event ->
                                if(event.isFocused){
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
                            value = emailValue.value,
                            onValueChange = { emailValue.value = it },
                            label = { Text(text = "Email Address") },
                            placeholder = { Text(text = "Email Address") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onFocusEvent { event ->
                                    if(event.isFocused){
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
                            value = phoneValue.value,
                            onValueChange = { phoneValue.value = it },
                            label = { Text(text = "Phone Number") },
                            placeholder = { Text(text = "Phone Number") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onFocusEvent { event ->
                                    if(event.isFocused){
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                        )

                        OutlinedTextField(
                            value = passwordValue.value,
                            onValueChange = { passwordValue.value = it },
                            label = { Text(text = "Password") },
                            placeholder = { Text(text = "Password") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onFocusEvent { event ->
                                    if(event.isFocused){
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
                            visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                            else PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = confirmPasswordValue.value,
                            onValueChange = { confirmPasswordValue.value = it },
                            label = { Text(text = "Confirm Password") },
                            placeholder = { Text(text = "Confirm Password") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onFocusEvent { event ->
                                    if(event.isFocused){
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
                            visualTransformation = if (confirmPasswordVisibility.value) VisualTransformation.None
                            else PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = { }, modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp)
                        ) {
                            Text(text = "Sign Up", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                        Text(
                            text = "Login Instead!",
                            modifier = Modifier.clickable(onClick = {
                                navController.navigate("login_screen") {
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





















