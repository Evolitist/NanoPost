package com.evolitist.nanopost.presentation.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.evolitist.nanopost.R
import com.evolitist.nanopost.domain.model.Likes
import com.evolitist.nanopost.domain.model.Post
import com.evolitist.nanopost.domain.model.ProfileCompact
import com.evolitist.nanopost.presentation.extensions.loadState
import com.evolitist.nanopost.presentation.navigation.DefaultLocalSharedTransitionParams
import com.evolitist.nanopost.presentation.navigation.sharedBounds
import com.evolitist.nanopost.presentation.ui.MainViewModel
import com.evolitist.nanopost.presentation.ui.create.CreateActions
import com.evolitist.nanopost.presentation.ui.view.Avatar
import com.evolitist.nanopost.presentation.ui.view.PostCard
import com.evolitist.nanopost.presentation.ui.view.PullRefreshBox
import com.evolitist.nanopost.presentation.ui.view.SmallFABLayout
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

@Serializable
data object FeedRoute

@Composable
fun FeedRoute(
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String?) -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val posts = viewModel.feedPagingDataFlow.collectAsLazyPagingItems()

    val activityViewModel = hiltViewModel<MainViewModel>(LocalContext.current as ViewModelStoreOwner)
    val profile by activityViewModel.profileFlow.collectAsStateWithLifecycle()

    FeedScreen(
        posts = posts,
        profile = profile,
        onCreatePostClick = onCreatePostClick,
        onCreateProfileClick = onCreateProfileClick,
        onPostClick = onPostClick,
        onProfileClick = onProfileClick,
        onImageClick = onImageClick,
        modifier = modifier,
    )
}

@Composable
fun FeedScreen(
    posts: LazyPagingItems<Post>,
    profile: ProfileCompact?,
    onCreatePostClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String?) -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun closeSearchBar() {
        focusManager.clearFocus()
        active = false
    }

    val topInset = WindowInsets.statusBars
        .only(WindowInsetsSides.Top)
        .asPaddingValues()
        .calculateTopPadding()
    val bottomInset = WindowInsets.systemBars
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()
        .calculateBottomPadding()
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        PullRefreshBox(
            data = posts,
            indicatorPadding = PaddingValues(top = topInset + SearchBarDefaults.InputFieldHeight),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = 16.dp + topInset + SearchBarDefaults.InputFieldHeight,
                    end = 8.dp,
                    bottom = 16.dp + bottomInset,
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("LazyColumn"),
            ) {
                items(
                    count = posts.itemCount,
                    key = posts.itemKey(Post::id),
                ) { index ->
                    posts[index]?.let { post ->
                        PostCard(
                            post = post,
                            onClick = onPostClick,
                            onImageClick = onImageClick,
                            onHeaderClick = onProfileClick,
                            modifier = Modifier.testTag("PostCard"),
                        )
                    }
                }

                loadState(posts.loadState.append, posts::retry)
            }
        }

        Column {
            val overlayColor = MaterialTheme.colorScheme.background
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topInset + SearchBarDefaults.InputFieldHeight.div(2))
                    .background(overlayColor),
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SearchBarDefaults.InputFieldHeight.div(2) + 16.dp)
                    .drawWithCache {
                        onDrawWithContent {
                            drawRect(
                                Brush.verticalGradient(
                                    0f to overlayColor,
                                    1f to Color.Transparent,
                                ),
                            )
                        }
                    },
            )
        }

        val searchBarWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp() - 8.dp.times(2)
        }
        fun onExpandedChange(expanded: Boolean): () -> Unit = {
            active = expanded
            if (!active) {
                focusManager.clearFocus()
            }
        }
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { closeSearchBar() },
                    expanded = active,
                    onExpandedChange = ::onExpandedChange,
                    placeholder = { Text(stringResource(R.string.app_name)) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        profile?.let { profile ->
                            IconButton(
                                content = {
                                    Avatar(
                                        profile = profile,
                                        monogramFontSize = 12.sp,
                                        modifier = Modifier.size(32.dp),
                                    )
                                },
                                onClick = dropUnlessStarted {
                                    onProfileClick(null)
                                },
                            )
                        }
                    },
                )
            },
            expanded = active,
            onExpandedChange = ::onExpandedChange,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(min = searchBarWidth),
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(4) { idx ->
                        val resultText = "Suggestion $idx"
                        ListItem(
                            headlineContent = { Text(resultText) },
                            leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                            modifier = Modifier.clickable {
                                text = resultText
                                closeSearchBar()
                            }
                        )
                        if (idx != 3) {
                            HorizontalDivider()
                        }
                    }
                }
            },
        )

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
        )
    }
}

@Preview
@Composable
private fun FeedScreen_Preview() {
    val mockData = List(10) {
        Post(
            id = it.toString(),
            owner = ProfileCompact("", "username", null, null, null, false),
            dateCreated = Clock.System.now().minus(7.days).epochSeconds,
            text = if (Random.nextBoolean()) "" else "some text here",
            images = persistentListOf(),
            likes = Likes(Random.nextBoolean(), Random.nextInt(100)),
        )
    }
    val pagingData = PagingData.from(mockData)
    val mockDataFlow = MutableStateFlow(pagingData)

    DefaultLocalSharedTransitionParams { modifier ->
        FeedScreen(
            posts = mockDataFlow.collectAsLazyPagingItems(),
            profile = ProfileCompact("", "username", null, null, null, false),
            onCreatePostClick = {},
            onCreateProfileClick = {},
            onPostClick = {},
            onProfileClick = {},
            onImageClick = {},
            modifier = modifier,
        )
    }
}
