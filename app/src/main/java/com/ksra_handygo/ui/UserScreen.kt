package com.ksra_handygo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ksra_handygo.model.User
import com.ksra_handygo.viewmodel.UserViewModel

@Composable
fun UserScreen(
    viewModel: UserViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val users by viewModel.users.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Add New User",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.text.isNotBlank() && email.text.isNotBlank()) {
                    val newUser = User(
                        id = null,
                        name = name.text,
                        email = email.text,
                        passwordHash = "default",
                        phone = phone.text,
                        address = address.text,
                        role = User.Role.CUSTOMER
                    )
                    viewModel.addUser(newUser)
                    name = TextFieldValue("")
                    email = TextFieldValue("")
                    phone = TextFieldValue("")
                    address = TextFieldValue("")
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Create User")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Existing Users",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            error != null -> Text(
                "Error: $error",
                color = MaterialTheme.colorScheme.error
            )
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(users) { user ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${user.name}", style = MaterialTheme.typography.bodyLarge)
                            Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                            Text("Phone: ${user.phone}", style = MaterialTheme.typography.bodyMedium)
                            Text("Role: ${user.role}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
