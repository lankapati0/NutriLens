package com.akhilav.nutrilens.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
enum class AppToastType {
    SUCCESS,ERROR,WARNING
}

data class AppToastData(
    val message: String,
    val type: AppToastType,
    val id: Long
)

class AppToastController {
    var toast by mutableStateOf<AppToastData?>(null)

    private var nextId = 0L

    fun dismiss(){
        toast = null
    }
    fun show(
        message: String,
        type: AppToastType = AppToastType.SUCCESS
    ) {
        toast = AppToastData(message, type, ++nextId)
    }
}

val LocalAppToast = compositionLocalOf<AppToastController> {
    error("AppToastHost is not provided")
}


@Composable
fun AppToastHost(
    navigationKey:String,
    content: @Composable () -> Unit
) {
    val controller = remember { AppToastController() }
    val toast = controller.toast

    LaunchedEffect(navigationKey) {
        controller.dismiss()
    }

    LaunchedEffect(toast?.id) {
        if (toast != null) {
            delay(3000.milliseconds)
            if (controller.toast?.id == toast.id) {
                controller.toast = null
            }
        }
    }

    CompositionLocalProvider(LocalAppToast provides controller) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }

        if (toast != null) {
            Dialog(
                onDismissRequest = {
                    controller.dismiss()
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AnimatedVisibility(
                        visible = toast != null,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        val accentColor: Color = when (toast?.type) {
                            AppToastType.SUCCESS -> Color(0xFF65D6B0)
                            AppToastType.ERROR -> Color(0xFFFF7777)
                            AppToastType.WARNING -> Color(0xFFFFC66D)
                            null -> Color.Gray
                        }

                        val icon = when (toast?.type) {
                            AppToastType.SUCCESS -> "✓"
                            AppToastType.ERROR -> "✕"
                            AppToastType.WARNING -> "!"
                            null -> ""
                            else -> ""
                        }

                        val title = when (toast?.type) {
                            AppToastType.SUCCESS -> "Success"
                            AppToastType.ERROR -> "Something went wrong"
                            AppToastType.WARNING -> "Attention"
                            null -> ""
                            else -> ""
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .background(
                                    color = Color(0xFF193139),
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .padding(1.dp)
                                .background(
                                    color = Color(0xFF203B42),
                                    shape = RoundedCornerShape(21.dp)
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        accentColor.copy(alpha = 0.15f),
                                        RoundedCornerShape(15.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = icon,
                                    color = accentColor,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = toast?.message.orEmpty(),
                                    color = Color(0xFFD0E0DC),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(42.dp)
                                    .background(
                                        accentColor,
                                        RoundedCornerShape(50)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

