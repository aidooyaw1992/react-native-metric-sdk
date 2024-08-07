package com.metricafricaproject

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.content.pm.PackageManager
import android.graphics.Color
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

import com.metric.sdk.init.BasicMetricSettings
import com.metric.sdk.init.ClientAuthenticator
import com.metric.sdk.init.Environment
import com.metric.sdk.init.Metric
import com.metric.sdk.init.ThemeProvider
import com.metric.sdk.theme.AppLogo
import com.metric.sdk.theme.AppTheme
import com.metric.sdk.ui.sdklaucher.Reason
import com.metric.sdk.ui.sdklaucher.VerificationOutcome


class MetricAfricaModule(reactContext: ReactApplicationContext): ReactContextBaseJavaModule(reactContext)
{
    companion object {
        const val NAME = "MetricAfricaModule"
        private const val TAG = "MetricAfricaModule"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        private const val VERIFICATION_REQUEST_CODE = 2
    }

    private var verificationPromise: Promise? = null
    private var metricInitialized: Boolean = false
    override fun getName(): String {
       return NAME
    }


    private val activityEventListener = object : BaseActivityEventListener() {
        override fun onActivityResult(
            activity: Activity?,
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) {
            if (resultCode == Activity.RESULT_OK) {
                val result = Metric.getResultsFromIntent(data)
                result?.let { outcome ->
                    when(outcome){
                        is VerificationOutcome.Failed -> {
                            val errorText = when(outcome.reason){
                                Reason.LIVENESS_FAILED -> "Liveness check failed"
                                Reason.CANCELLED -> "Verification cancelled by user"
                                Reason.INVALID_TOKEN -> "Invalid verification token"
                                Reason.VERIFICATION_FAILED -> "Verification failed"
                                Reason.UNAUTHORISED -> "Unauthorized access"
                                Reason.UNKNOWN -> "Unknown error occurred"
                            }
                            verificationPromise?.reject(Throwable(outcome.reason.name,Throwable(errorText)))
                        }
                        is VerificationOutcome.Success -> {
                            verificationPromise?.resolve("Verification successful")
                        }
                    }
                }
            }
        }
    }

    init {
        reactContext.addActivityEventListener(activityEventListener)
    }
    @ReactMethod
    fun initializeMetricAfricaSdk(clientId:String, clientSecret:String, promise: Promise){
        try {
            if (!metricInitialized) {
                Log.d(
                    TAG,
                    "initializeMetric: im here => $clientId, $clientSecret"
                )

                Metric.init(
                    metricSettings = BasicMetricSettings(
                        applicationContext = reactApplicationContext,
                        authenticator = {
                            ClientAuthenticator(
                                clientKey = clientId,
                                secretKey = clientSecret
                            )
                        },
                        environment = Environment.Prod,
                        themeProvider = ThemeProvider(
                            appTheme = {
                                AppTheme(
                                    appName = "",
                                    logo = AppLogo.NetworkImage("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Google_2015_logo.svg/1280px-Google_2015_logo.svg.png"),
                                    primaryColor = Color.RED,
                                )
                            }
                        ),
                    )
                )
                metricInitialized = true
                promise.resolve("Metric SDK initialized successfully")
            } else {
                promise.resolve("Metric SDK already initialized")
            }
        } catch (e: Exception) {
            promise.reject("ERROR", e.message)
        }
    }

    @ReactMethod
    fun initializeVerification(token: String, promise: Promise){
        verificationPromise = promise
        val intent = Metric.createStartIntent(token)
        currentActivity?.startActivityForResult(intent, VERIFICATION_REQUEST_CODE)
    }

}