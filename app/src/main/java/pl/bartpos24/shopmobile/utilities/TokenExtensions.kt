package pl.bartpos24.shopmobile.utilities

import com.auth0.android.jwt.JWT

private fun decodeToken(token: String) = JWT(token)
fun createAuthorizationHeader(accessToken: String) = "$bearerAuthorizationHeaderTemplate $accessToken"
fun validateAccessToken(token: String?) = validateToken(token, accessTokenSecret, tokenIssuer, tokenLeeway)
fun validateRefreshToken(token: String?) = validateToken(token, refreshTokenSecret, tokenIssuer, tokenLeeway)

private fun validateToken(token: String?, secret: String, issuer: String, leeway: Long): Boolean {
    if (token.isNullOrEmpty())
        return false
    return try {
        with(decodeToken(token)) {
            this.issuer == issuer && !isExpired(leeway)
        }
    } catch (e: Throwable) {
        false
    }
}