package com.chojikun.logit.feature.auth.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BackgroundCream = Color(0xFFF2EDE4)
private val FieldBorder = Color(0xFFDDD8D0)
private val LabelGray = Color(0xFF4A4540)
private val PlaceholderGray = Color(0xFFADA8A0)
private val FieldTextColor = Color(0xFF1A1A1A)

val LogitGreen = Color(0xFF1A6B4A)

@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onSigningInTapped: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect { onNavigateToHome() }
    }

    LaunchedEffect(viewModel.authError) {
        val error = viewModel.authError ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Long)
        viewModel.clearAuthError()
    }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Track the scroll container's root position so we can calculate
    // each field's offset within the scrollable content.
    var containerRootY by remember { mutableFloatStateOf(0f) }
    var emailRootY by remember { mutableFloatStateOf(0f) }
    var passwordRootY by remember { mutableFloatStateOf(0f) }
    var confirmRootY by remember { mutableFloatStateOf(0f) }

    // scrolls so the field appears ~24dp below the visible top
    fun scrollToField(fieldRootY: Float) {
        scope.launch {
            delay(300) // wait for keyboard to finish animating
            val contentY = (fieldRootY - containerRootY + scrollState.value - 24f)
                .coerceAtLeast(0f).toInt()
            scrollState.animateScrollTo(contentY)
        }
    }

    LaunchedEffect(Unit) { emailFocusRequester.requestFocus() }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundCream,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundCream)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .onGloballyPositioned { coords: LayoutCoordinates ->
                    containerRootY = coords.positionInRoot().y
                },
        ) {
            AuthHeader(
                title = "Create your account",
                subtitle = "Free forever. No bank login. No ads.",
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCBC6BE)),
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "About you",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                )
                Spacer(modifier = Modifier.height(20.dp))

                FieldLabel("EMAIL")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = { Text("username@example.com", color = PlaceholderGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PlaceholderGray)
                    },
                    isError = viewModel.emailError != null,
                    supportingText = { viewModel.emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() },
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(emailFocusRequester)
                        .onGloballyPositioned { emailRootY = it.positionInRoot().y }
                        .onFocusChanged { if (it.isFocused) scrollToField(emailRootY) },
                )
                Spacer(modifier = Modifier.height(12.dp))

                FieldLabel("PASSWORD")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = { Text("Min. 6 alphanumeric chars", color = PlaceholderGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PlaceholderGray)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide" else "Show",
                                tint = PlaceholderGray,
                            )
                        }
                    },
                    isError = viewModel.passwordError != null,
                    supportingText = { viewModel.passwordError?.let { Text(it) } },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { confirmFocusRequester.requestFocus() },
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester)
                        .onGloballyPositioned { passwordRootY = it.positionInRoot().y }
                        .onFocusChanged { if (it.isFocused) scrollToField(passwordRootY) },
                )
                Spacer(modifier = Modifier.height(12.dp))

                FieldLabel("CONFIRM PASSWORD")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    placeholder = { Text("Re-enter your password", color = PlaceholderGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PlaceholderGray)
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Hide" else "Show",
                                tint = PlaceholderGray,
                            )
                        }
                    },
                    isError = viewModel.confirmPasswordError != null,
                    supportingText = { viewModel.confirmPasswordError?.let { Text(it) } },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmFocusRequester)
                        .onGloballyPositioned { confirmRootY = it.positionInRoot().y }
                        .onFocusChanged { if (it.isFocused) scrollToField(confirmRootY) },
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::onRegisterClick,
                    enabled = !viewModel.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogitGreen,
                        disabledContainerColor = LogitGreen.copy(alpha = 0.6f),
                    ),
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "Create account →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text("Already have an account? ", color = LabelGray, fontSize = 14.sp)
                    Text(
                        text = "Sign in",
                        color = LogitGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(enabled = !viewModel.isLoading) {
                            onSigningInTapped()
                        },
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    } // end Scaffold content
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedTextColor = FieldTextColor,
    focusedTextColor = FieldTextColor,
    errorTextColor = FieldTextColor,
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    unfocusedBorderColor = FieldBorder,
    focusedBorderColor = LogitGreen,
    unfocusedLeadingIconColor = PlaceholderGray,
    focusedLeadingIconColor = LogitGreen,
)

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        color = LabelGray,
    )
}

@Composable
fun AuthHeaderCircles(
    modifier: Modifier = Modifier,
    ringColor: Color = Color.White,
) {
    val transition = rememberInfiniteTransition(label = "rings")

    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    val alpha by transition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )

    Canvas(modifier = modifier) {
        val cx = size.width
        val cy = 0f
        val strokePx = 1.5.dp.toPx()

        drawCircle(
            color = ringColor.copy(alpha = alpha * 0.55f),
            radius = 120.dp.toPx() * scale,
            center = Offset(cx, cy),
            style = Stroke(strokePx),
        )
        drawCircle(
            color = ringColor.copy(alpha = alpha * 0.75f),
            radius = 86.dp.toPx() * scale,
            center = Offset(cx, cy),
            style = Stroke(strokePx),
        )
        drawCircle(
            color = ringColor.copy(alpha = alpha),
            radius = 54.dp.toPx() * scale,
            center = Offset(cx, cy),
            style = Stroke(strokePx),
        )
    }
}

@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LogitGreen)
            .clipToBounds()
    ) {
        AuthHeaderCircles(
            modifier = Modifier
                .matchParentSize()
                .statusBarsPadding()
                .clipToBounds()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 20.dp)
        ) {
            Text(
                text = "Logit",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                lineHeight = 30.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
            )
        }
    }
}


@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect { onNavigateToHome() }
    }

    LaunchedEffect(viewModel.authError) {
        val error = viewModel.authError ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Long)
        viewModel.clearAuthError()
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundCream,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundCream)
                .padding(innerPadding)
        ) {
            AuthHeader(
                title = "Welcome back",
                subtitle = "Sign in to your Logit account"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCBC6BE)),
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Sign in",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                )
                Spacer(modifier = Modifier.height(20.dp))
                FieldLabel("EMAIL")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = { Text("username@example.com", color = PlaceholderGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PlaceholderGray)
                    },
                    isError = viewModel.emailError != null,
                    supportingText = { viewModel.emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() },
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(emailFocusRequester)
                )
                Spacer(modifier = Modifier.height(12.dp))

                FieldLabel("PASSWORD")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = { Text("Min. 6 alphanumeric chars", color = PlaceholderGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PlaceholderGray)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide" else "Show",
                                tint = PlaceholderGray,
                            )
                        }
                    },
                    isError = viewModel.passwordError != null,
                    supportingText = { viewModel.passwordError?.let { Text(it) } },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = viewModel::onLoginClick,
                    enabled = !viewModel.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogitGreen,
                        disabledContainerColor = LogitGreen.copy(alpha = 0.6f),
                    ),
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "Sign in →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }

}