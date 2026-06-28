package com.example.devfolio

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devfolio.ui.theme.JetBizCardTheme
import org.json.JSONArray
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay
import org.json.JSONObject

data class ProjectData(val name :String, val githubUrl: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    // your real app content / NavHost
                    JetBizCardTheme {
                        CreateBizCard()
                    }
                }
            }
        }
    }

    @Composable
    fun SplashScreen(onTimeout: () -> Unit) {
        LaunchedEffect(Unit) {
            delay(1200) // adjust to taste
            onTimeout()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_play_store_512),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "DevFolio",
                color = Color(0xFF22D3EE),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )
        }
    }

    @Composable
    fun CreateBizCard() {
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current

        val projectStorage = remember(context) { ProjectStorage(context) }

        val projects = remember { mutableStateListOf<ProjectData>() }

        val buttonClickedState = remember { mutableStateOf(false) }

        val gradientColors = listOf(
            Color(0xFF172554),
            Color(0xFF0891B2),
            Color(0xFFE0F2FE)
        )

        LaunchedEffect(Unit) {
            projects.clear()
            projects.addAll(projectStorage.loadProjects())
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .background(brush = Brush.horizontalGradient(colors = gradientColors)),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(corner = CornerSize(28.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CreateImageProfile()
                        Spacer(modifier = Modifier.height(14.dp))
                        ProfileName()
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 18.dp),
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0)
                        )
                        ButtonPortfolio(buttonClickedState = buttonClickedState)

                        if (buttonClickedState.value) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Content(
                                projects = projects,
                                onAddProject = { projectName ->
                                    projects.add(
                                        ProjectData(
                                            projectName,
                                            "https://github.com/aaravkumar52"
                                        )
                                    )
                                    projectStorage.saveProjects(projects)
                                },
                                onRenameProject = { index, newName, newLink ->
                                    projects[index] = ProjectData(newName, newLink)
                                    projectStorage.saveProjects(projects)
                                },
                                onDeleteProject = { index ->
                                    projects.removeAt(index)
                                    projectStorage.saveProjects(projects)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CreateImageProfile(modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier.size(148.dp),
            shape = CircleShape,
            border = BorderStroke(5.dp, Color.White),
            shadowElevation = 8.dp,
            color = Color(0xFFE0F2FE)
        ) {
            Image(
                painter = painterResource(id = R.drawable.project_image_3),
                contentDescription = "profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
    }

    @Composable
    private fun ButtonPortfolio(buttonClickedState: MutableState<Boolean>) {
        Button(
            onClick = {
                buttonClickedState.value = !buttonClickedState.value
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (buttonClickedState.value) Color(0xFF0F172A) else Color(
                    0xFF0284C7
                )
            )
        ) {
            Text(
                text = if (buttonClickedState.value) "Hide Portfolio" else "View Portfolio",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    fun Content(
        projects: List<ProjectData>,
        onAddProject: (String) -> Unit,
        onRenameProject: (Int, String, String) -> Unit,
        onDeleteProject: (Int) -> Unit
    ) {
        val newProjectName = remember {
            mutableStateOf("")
        }
        val editingIndex = remember {
            mutableStateOf<Int?>(null)
        }
        val editedProjectName = remember {
            mutableStateOf("")
        }
        val editGithubLink = remember { mutableStateOf("") }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp),
            shape = RoundedCornerShape(corner = CornerSize(18.dp)),
            color = Color.White,
            border = BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0)),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newProjectName.value,
                        onValueChange = { newProjectName.value = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Project name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0284C7),   // Color when you are typing
                            unfocusedTextColor = Color(0xFF0F172A),
                        )
                    )
                    IconButton(
                        onClick = {
                            val projectName = newProjectName.value.trim()
                            if (projectName.isNotEmpty()) {
                                onAddProject(projectName)
                                newProjectName.value = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add project",
                            tint = Color(0xFF0284C7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Portfolio(
                    projects = projects,
                    onRenameClick = { index, projectName, currentGithub ->
                        editingIndex.value = index
                        editedProjectName.value = projectName
                        editGithubLink.value = currentGithub
                    },
                    onDeleteClick = onDeleteProject
                )
            }
        }

        val selectedIndex = editingIndex.value
        if (selectedIndex != null) {
            AlertDialog(
                onDismissRequest = { editingIndex.value = null },
                title = {
                    Text(
                        "Edit Project Info",
                        modifier = Modifier.padding(4.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editedProjectName.value,
                            onValueChange = { editedProjectName.value = it },
                            label = { Text("Project name") }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editGithubLink.value,
                            onValueChange = { editGithubLink.value = it },
                            label = {
                                Text(
                                    text = "Github link (https://github.com/...)"
                                )
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val projectName = editedProjectName.value.trim()
                            if (projectName.isNotEmpty()) {
                                onRenameProject(
                                    selectedIndex,
                                    editedProjectName.value,
                                    editGithubLink.value
                                )
                                editingIndex.value = null
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingIndex.value = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    @Composable
    fun Portfolio(
        projects: List<ProjectData>,
        onRenameClick: (Int, String, String) -> Unit,
        onDeleteClick: (Int) -> Unit,
        uriHandler: UriHandler = LocalUriHandler.current
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(projects) { index, project ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(1.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(corner = CornerSize(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, top = 10.dp, end = 6.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Project ${index + 1}",
                                fontSize = 13.sp,
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.name,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = {
                                    uriHandler.openUri(project.githubUrl)
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "View on GitHub",
                                    color = Color(0xFF0284C7),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = {
                            onRenameClick(
                                index,
                                project.name,
                                project.githubUrl
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rename project",
                                tint = Color(0xFF0284C7)
                            )
                        }

                        IconButton(onClick = { onDeleteClick(index) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete project",
                                tint = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ProfileName() {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aarav Kumar",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Android Compose Programmer",
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF475569),
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "@aaravkumar52",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                color = Color(0xFF0284C7),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                IconBadge {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF0284C7)
                    )
                }
                IconBadge {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "GitHub",
                        tint = Color(0xFF0284C7)
                    )
                }
            }
        }
    }

    @Composable
    private fun IconBadge(content: @Composable () -> Unit) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = Color(0xFFE0F2FE)
        ) {
            Box(contentAlignment = Alignment.Center) {
                content()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        JetBizCardTheme {
            CreateBizCard()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ContentPreview() {
        JetBizCardTheme {
            // Create a mock list of projects to see how it looks in the preview
            val mockProjects = listOf(
                ProjectData("Project 1", "https://github.com/aaravkumar52"),
                ProjectData("Project 2", "https://github.com/aaravkumar52"),
                ProjectData("Project 3", "https://github.com/aaravkumar52")
            )

            // Wrap in a Surface/Box so the layout looks correct in the preview pane
            Surface(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Content(
                    projects = mockProjects,
                    onAddProject = {},    // Empty lambda for preview
                    onRenameProject = { _, _, _ -> }, // Empty lambda for preview
                    onDeleteProject = {}  // Empty lambda for preview
                )
            }
        }
    }


    private class ProjectStorage(context: Context) {
        private val preferences =
            context.getSharedPreferences("portfolio_projects", MODE_PRIVATE)

        fun loadProjects(): List<ProjectData> {
            val savedProjects = preferences.getString(PROJECTS_KEY, null) ?: return defaultProjects

            return runCatching {
                val jsonArray = JSONArray(savedProjects)
                List(jsonArray.length()) { index ->
                    val obj = jsonArray.getJSONObject(index)
                    ProjectData(
                        name = obj.getString("name"),
                        githubUrl = obj.getString("link")
                    )
                }
            }.getOrDefault(defaultProjects)
        }

        fun saveProjects(projects: List<ProjectData>) {
            val jsonArray = JSONArray()
            projects.forEach { project ->
                val jsonObject = JSONObject()
                jsonObject.put("name", project.name)
                jsonObject.put("link", project.githubUrl)
                jsonArray.put(jsonObject)
            }

            preferences.edit()
                .putString(PROJECTS_KEY, jsonArray.toString())
                .apply()
        }

        private companion object {
            const val PROJECTS_KEY = "projects_V2"

            val defaultProjects = listOf(
                ProjectData("Unit Converter App", "https://github.com/aaravkumar52"),
                ProjectData("Random String Generator App", "https://github.com/aaravkumar52")
            )
        }
    }
}
