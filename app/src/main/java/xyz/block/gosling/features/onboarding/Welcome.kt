package xyz.block.gosling.features.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeStep(
    isAccessibilityEnabled: Boolean,
    onEnableAccessibility: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var isAssistantEnabled by remember { mutableStateOf(false) }
    var isDefaultLauncher by remember { mutableStateOf(false) }

    fun checkAssistantStatus() {
        val settingSecure = Settings.Secure.getString(
            context.contentResolver,
            "assistant"
        )
        isAssistantEnabled = settingSecure?.contains(context.packageName) == true
    }

    fun checkLauncherStatus() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = context.packageManager.resolveActivity(intent, 0)
        isDefaultLauncher = resolveInfo?.activityInfo?.packageName == context.packageName
    }

    LaunchedEffect(Unit) {
        checkAssistantStatus()
        checkLauncherStatus()
    }

    DisposableEffect(Unit) {
        val activity = context as? android.app.Activity
        val lifecycleObserver = object : android.app.Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: android.app.Activity) {
                if (activity == context) {
                    checkAssistantStatus()
                    checkLauncherStatus()
                }
            }

            override fun onActivityPaused(activity: android.app.Activity) {}
            override fun onActivityStarted(activity: android.app.Activity) {}
            override fun onActivityDestroyed(activity: android.app.Activity) {}
            override fun onActivitySaveInstanceState(
                activity: android.app.Activity,
                outState: android.os.Bundle
            ) {
            }

            override fun onActivityStopped(activity: android.app.Activity) {}
            override fun onActivityCreated(
                activity: android.app.Activity,
                savedInstanceState: android.os.Bundle?
            ) {
            }
        }

        activity?.application?.registerActivityLifecycleCallbacks(lifecycleObserver)

        onDispose {
            activity?.application?.unregisterActivityLifecycleCallbacks(lifecycleObserver)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Accessibility Permissions",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Goose Mobile needs accessibility permissions to interact with other apps and help you with tasks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onEnableAccessibility,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAccessibilityEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAccessibilityEnabled)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isAccessibilityEnabled) "Accessibility Enabled" else "Enable Accessibility")
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Set Default Assistant",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Make Goose Mobile your default assistant app to get the most out of its features.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAssistantEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAssistantEnabled)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isAssistantEnabled) "Assistant Enabled" else "Set as Default Assistant")
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Set Default Home App",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Make Goose Mobile your default home screen to access its features with a single tap.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isDefaultLauncher,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDefaultLauncher)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isDefaultLauncher) "Home App Enabled" else "Set as Default Home")
                }
            }
        }
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .navigationBarsPadding()
                .imePadding(),
        ) {
            Text("Next")
        }
    }
}
