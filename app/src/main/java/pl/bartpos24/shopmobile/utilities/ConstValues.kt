package pl.bartpos24.shopmobile.utilities

import android.Manifest
const val refreshTokenKey = "refresh_token"
const val accessTokenKey = "access_token"
const val bearerAuthorizationHeaderTemplate = "Bearer"
const val accessTokenSecret = ""
const val refreshTokenSecret = ""
const val tokenIssuer = "ShopApi"
const val tokenLeeway = 60L
const val apiBasePath = ""
const val apiBasePathDevelopment = ""
const val apiBasePathBeta = ""
const val logoutWorkerName = "LogoutTokenWorker"
const val refreshTokenWorkerName = "RefreshTokenWorker"
const val workerMaxRetryNumber = 3
const val workerBackoffDelay = 50L
val requiredPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET)
const val requestCodeRequiredPermissions = 10