package com.ksra_handygo

object AuthConfig {
    // Replace with your actual values
    const val REGION = "us-east-2"
    const val USER_POOL_ID = "us-east-2_vafrricx7"
    const val CLIENT_ID = "7luss1jt3rqv467pqlrobur2o8" // your app client id
    // custom URI scheme (must match manifest and Cognito callback)
    const val REDIRECT_URI = "ksrafisherman://callback"
    const val LOGOUT_REDIRECT_URI = "ksrafisherman://logout"

    // Issuer URL (used for discovery)
    const val ISSUER = "https://cognito-idp.$REGION.amazonaws.com/$USER_POOL_ID"

    // If you have a hosted domain and prefer hosted login pages:
    // const val COGNITO_DOMAIN = "https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com"
}
