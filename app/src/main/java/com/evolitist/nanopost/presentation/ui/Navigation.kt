package com.evolitist.nanopost.presentation.ui

private object NanoPostScreens {
    const val Auth = "auth"
    const val Feed = "feed"
    const val Search = "search"
    const val Profile = "profile"
    const val CreatePost = "create_post"
    const val CreateProfile = "create_profile"
    const val Post = "post"
    const val Image = "image"
}

object NanoPostDestinationArgs {
    const val ProfileId = "profileId"
    const val ImageId = "imageId"
    const val PostId = "postId"
}

object NanoPostDestinations {
    const val AuthRoute = NanoPostScreens.Auth
    const val FeedRoute = NanoPostScreens.Feed
    const val SearchRoute = NanoPostScreens.Search
    const val ProfileRoute = NanoPostScreens.Profile
    const val CreatePostRoute = NanoPostScreens.CreatePost
    const val CreateProfileRoute = NanoPostScreens.CreateProfile
    const val PostRoute = NanoPostScreens.Post
    const val ImageRoute = NanoPostScreens.Image
}
