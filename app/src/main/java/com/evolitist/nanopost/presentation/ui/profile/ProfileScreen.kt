package com.evolitist.nanopost.presentation.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Image
import com.evolitist.nanopost.domain.model.ImageSize
import com.evolitist.nanopost.domain.model.Likes
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.domain.model.Profile
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.extensions.plus
import com.evolitist.nanopost.presentation.navigation.DefaultLocalSharedTransitionParams
import com.evolitist.nanopost.presentation.navigation.sharedBounds
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.logout.LogoutDialog
import com.evolitist.nanopost.presentation.ui.view.ImagesCard
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.ProfileButtonState
import com.evolitist.nanopost.presentation.ui.view.ProfileCard
import com.evolitist.nanopost.presentation.ui.view.PullRefreshBox
import com.evolitist.nanopost.presentation.ui.view.SmallFABLayout
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
data class ProfileRoute(val id: String? = null)

@Composable
fun ProfileRoute(
    profileId: String?,
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSubscribersClick: (String?) -> Unit,
    onImagesClick: (String?) -> Unit,
    onPostsClick: (String?) -> Unit,
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val profile = viewModel.profilePagingDataFlow.collectAsLazyPagingItems()

    ProfileScreen(
        profileId = profileId,
        profile = profile,
        onCloseClick = onCloseClick,
        onLogoutClick = onLogoutClick,
        onSubscribeClick = viewModel::subscribe,
        onUnsubscribeClick = viewModel::unsubscribe,
        onSubscribersClick = onSubscribersClick,
        onImagesClick = onImagesClick,
        onPostsClick = onPostsClick,
        onCreatePostClick = onCreatePostClick,
        onCreateProfileClick = onCreateProfileClick,
        onPostClick = onPostClick,
        onImageClick = onImageClick,
        onProfileClick = onProfileClick,
        modifier = modifier,
    )
}

@Composable
private fun ProfileScreen(
    profileId: String?,
    profile: LazyPagingItems<ProfileElement>,
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSubscribeClick: (() -> Unit) -> Unit,
    onUnsubscribeClick: (() -> Unit) -> Unit,
    onSubscribersClick: (String?) -> Unit,
    onImagesClick: (String?) -> Unit,
    onPostsClick: (String?) -> Unit,
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var logoutDialogShown by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.action_back),
                                )
                            },
                            onClick = dropUnlessStarted { onCloseClick() },
                        )
                    },
                    title = { Text(stringResource(R.string.profile)) },
                    actions = {
                        if (profileId == null) {
                            IconButton(
                                content = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                                        contentDescription = stringResource(R.string.logout),
                                    )
                                },
                                onClick = { logoutDialogShown = true },
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { padding ->
            PullRefreshBox(
                data = profile,
                indicatorPadding = padding,
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                    contentPadding = padding + PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        count = profile.itemCount,
                        key = profile.itemKey { it.id ?: "${it.type}_${profileId}" },
                        contentType = profile.itemContentType { it.type },
                    ) { index ->
                        when (val element = profile[index]) {
                            is ProfileElement.ProfileItem -> ProfileCard(
                                profile = element.profile,
                                buttonState = if (profileId == null) {
                                    ProfileButtonState.Edit
                                } else if (element.profile.subscribed) {
                                    ProfileButtonState.Unsubscribe
                                } else {
                                    ProfileButtonState.Subscribe
                                },
                                onSubscribersClick = dropUnlessStarted { onSubscribersClick(profileId) },
                                onImagesClick = dropUnlessStarted { onImagesClick(profileId) },
                                onPostsClick = dropUnlessStarted { onPostsClick(profileId) },
                                onButtonClick = {
                                    if (profileId == null) {
                                        // TODO edit profile
                                    } else if (element.profile.subscribed) {
                                        onUnsubscribeClick(profile::refresh)
                                    } else {
                                        onSubscribeClick(profile::refresh)
                                    }
                                },
                            )

                            is ProfileElement.ImagesItem -> ImagesCard(
                                images = element.images,
                                onCardClick = dropUnlessStarted { onImagesClick(profileId) },
                                onImageClick = onImageClick,
                            )

                            is ProfileElement.PostItem -> PostCard(
                                post = element.post,
                                onClick = onPostClick,
                                onImageClick = onImageClick,
                                onHeaderClick = {
                                    if (profileId != it && profileId != null) {
                                        onProfileClick(it)
                                    }
                                },
                            )

                            else -> Unit
                        }
                    }

                    loadState(profile.loadState.append, profile::retry)
                }
            }
        }
        if (profileId == null) {
            SmallFABLayout(
                items = persistentListOf(CreateActions.Profile, CreateActions.Post),
                label = {
                    when (it) {
                        CreateActions.Post -> Text(stringResource(R.string.post))
                        CreateActions.Profile -> Text(stringResource(R.string.profile))
                    }
                },
                button = {
                    when (it) {
                        CreateActions.Post -> SmallFloatingActionButton(
                            onClick = dropUnlessStarted { onCreatePostClick() },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            content = {
                                Icon(
                                    imageVector = Icons.Rounded.PostAdd,
                                    contentDescription = stringResource(R.string.post),
                                )
                            },
                            modifier = Modifier.sharedBounds(CreateActions.Post),
                        )

                        CreateActions.Profile -> SmallFloatingActionButton(
                            onClick = dropUnlessStarted { onCreateProfileClick() },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            content = {
                                Icon(
                                    imageVector = Icons.Rounded.GroupAdd,
                                    contentDescription = stringResource(R.string.profile),
                                )
                            },
                            modifier = Modifier.sharedBounds(CreateActions.Profile),
                        )
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }

    if (logoutDialogShown) {
        LogoutDialog(
            onConfirm = dropUnlessStarted {
                logoutDialogShown = false
                onLogoutClick()
            },
            onDismiss = { logoutDialogShown = false },
        )
    }
}

@Preview
@Composable
private fun ProfileScreen_Preview() {
    val mockImages = List(4) {
        Image(
            id = it.toString(),
            owner = ProfileCompact("", "username", null, null, null, false),
            dateCreated = Clock.System.now().minus(7.days).toEpochMilliseconds(),
            contentDescription = null,
            blurHash = null,
            sizes = persistentListOf(
                ImageSize(200, 200, "https://cataas.com/cat?type=square")
            ),
        )
    }.toImmutableList()
    val mockData = listOf(
        ProfileElement.profile(
            Profile(
                id = "",
                username = "username",
                displayName = null,
                bio = "someone somewhere",
                avatarId = null,
                avatarLarge = null,
                avatarSmall = null,
                avatarBlurHash = null,
                subscribed = false,
                subscribersCount = 17,
                postsCount = 25,
                imagesCount = mockImages.size,
                images = mockImages,
            )
        ),
        ProfileElement.images(mockImages),
        ProfileElement.post(
            Post(
                id = "",
                owner = ProfileCompact("", "username", null, null, null, false),
                dateCreated = 0L,
                text = "",
                images = persistentListOf(),
                likes = Likes(false, 9),
            )
        ),
    )
    val pagingData = PagingData.from(mockData)
    val mockDataFlow = MutableStateFlow(pagingData)

    DefaultLocalSharedTransitionParams { modifier ->
        ProfileScreen(
            profileId = null,
            profile = mockDataFlow.collectAsLazyPagingItems(),
            onCloseClick = {},
            onLogoutClick = {},
            onSubscribeClick = {},
            onUnsubscribeClick = {},
            onSubscribersClick = {},
            onImagesClick = {},
            onPostsClick = {},
            onCreatePostClick = {},
            onCreateProfileClick = {},
            onPostClick = {},
            onImageClick = {},
            onProfileClick = {},
            modifier = modifier,
        )
    }
}
