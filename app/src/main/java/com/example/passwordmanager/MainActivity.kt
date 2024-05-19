@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.example.passwordmanager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import com.example.passwordmanager.ui.theme.PasswordRepository
import com.example.passwordmanager.ui.theme.model.PasswordModel
import com.example.passwordmanager.ui.theme.roomdb.PasswordDB
import com.example.passwordmanager.ui.theme.utils.Constants
import com.example.passwordmanager.ui.theme.utils.EncryptionHelper
import com.example.passwordmanager.ui.theme.viewmodel.ItemViewModelFactory
import com.example.passwordmanager.ui.theme.viewmodel.PasswordViewModel

class MainActivity : ComponentActivity() {


    //private val passwordViewModel: PasswordViewModel by viewModels()

    private val passwordViewModel: PasswordViewModel by viewModels {
        ItemViewModelFactory(PasswordRepository(PasswordDB.getDatabase(this).passwordDao()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            PasswordManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    //PasswordListView()
                    ScaffoldWithFab(this,passwordViewModel)

                }
            }
        }
    }
}


@Composable
fun PasswordListView(items: MutableList<PasswordModel>, onItemClicked: (PasswordModel) -> Unit) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { index ->
            PasswordCard(items[index],onItemClicked)
        }
    }


}

fun addItem(item: PasswordModel, currentItems: MutableList<PasswordModel>) {
    currentItems.add(item)
}

@Composable
fun PasswordCard(passwordModel: PasswordModel,onItemClicked: (PasswordModel) -> Unit) {

    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable { onItemClicked(passwordModel) }
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {

            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = passwordModel.accountName,
                    style = TextStyle(
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "*".repeat(10),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                    ))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScaffoldWithFab(activity: MainActivity, passwordViewModel: PasswordViewModel) {

    val sheetState = rememberModalBottomSheetState()

    //state of bottom sheet
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    //variable to identify if user is adding or editing Account details
    var isEditorOpen by rememberSaveable {
        mutableStateOf(false)
    }

    //variable for active account (selected by user from list)
    var selectedAccount by remember { mutableStateOf<PasswordModel?>(null) }

    val items: MutableList<PasswordModel> by passwordViewModel.passwords.observeAsState(arrayListOf<PasswordModel>())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                //show bottomsheet here
                selectedAccount = null
                isSheetOpen = true
                isEditorOpen = false
            }, modifier = Modifier.padding(end = 20.dp, bottom = 20.dp))
            {
                Text("+",
                    style =  TextStyle(
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    ))
            }
        }
    ) { innerPadding ->
        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            PasswordListView(items,onItemClicked = { password ->
                //show edit/delete bottom sheet on item clicked
                println("Clicked account: ${password.accountName}")
                isEditorOpen = true
                isSheetOpen = true
                selectedAccount = password
                //EditBottomSheetView(password)
            })
        }
    }


    if(isSheetOpen){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }) {


            // State variables to hold the text of each TextField
            var accountName by rememberSaveable { mutableStateOf(selectedAccount?.accountName.orEmpty())}
            var userName by rememberSaveable { mutableStateOf(selectedAccount?.userName.orEmpty()) }
            var password by rememberSaveable { mutableStateOf(DecryptedPassword(password = selectedAccount?.password.orEmpty())) }


   /*         // State variables to hold the text of each TextField
            var accountName by rememberSaveable { mutableStateOf("") }
            var userName by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }



            //when user edit/view the password set data to the textfield
            if(isEditorOpen){
                    accountName = selectedAccount!!.accountName
                    userName = selectedAccount!!.userName
                    password = DecryptedPassword(password = selectedAccount!!.password)
            }*/

            // Validation states for each TextField
            var isAccountnameValid by remember { mutableStateOf(true) }
            var isUsernameValid by remember { mutableStateOf(true) }
            var isPasswordValid by remember { mutableStateOf(true) }

            val submitValues: () -> Unit = {

                // Validate each TextField
                isAccountnameValid = accountName.isNotEmpty()
                isUsernameValid = userName.isNotEmpty()
                isPasswordValid = password.isNotEmpty()

                if(isAccountnameValid && isUsernameValid && isPasswordValid){
                    val model = PasswordModel(0,accountName = accountName, userName = userName, password = password)
//                    addItem( model,items)
                    if(passwordViewModel.insertPassword(model)){
                        isSheetOpen = false
                        Toast.makeText(activity,Constants.SUCCESS_INSERT_MESSAGE,Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(activity,Constants.ERROR_MESSAGE,Toast.LENGTH_SHORT).show()

                    }

                }
            }

            // A column to arrange the TextFields and Button vertically
            Box(modifier = Modifier.fillMaxSize()){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if(isEditorOpen){
                        Column {
                            Text(
                                text = Constants.ACCOUNT_DETAILS,
                                color = Color.Blue,
                                style =  TextStyle(
                                    color = Color.Blue,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    Column {
                        TextField(
                            value = accountName,
                            onValueChange = { newValue -> accountName = newValue },
                            label = { Text("Account Name") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isAccountnameValid
                        )
                        if (!isAccountnameValid) {
                            Text(
                                text = Constants.ACCOUNT_NAME_WARNING,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    Spacer(modifier = Modifier.height(5.dp))
                    }
                    Column {
                        TextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Username/Email") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isUsernameValid
                        )
                        if (!isUsernameValid) {
                            Text(
                                text = Constants.USER_NAME_WARNING,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    Spacer(modifier = Modifier.height(5.dp))
                    }
                    Column {

                        // Creating a variable to store toggle state
                        var isPasswordVisible by remember { mutableStateOf(false) }
                        val visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                        Row(modifier = Modifier.fillMaxWidth()){
                            TextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = !isPasswordValid,
                                visualTransformation = visualTransformation,
                                trailingIcon = {
                                    val image = if(isPasswordVisible){
                                        ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
                                    }
                                    else
                                    {
                                        ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24)
                                    }

                                    // Localized description for accessibility services
                                    val description = if (isPasswordVisible) "Hide password" else "Show password"

                                    // Toggle button to hide or display password
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible}){
                                        Icon(imageVector  = image, description)
                                    }
                                }
                            )

                        }
                        if (!isPasswordValid) {
                            Text(
                                text = Constants.PASSWORD_WARNING,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if(isEditorOpen){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    selectedAccount?.let {
                                        if(passwordViewModel.deletePassword(it)){
                                            isSheetOpen= false
                                            Toast.makeText(activity,Constants.SUCCESS_DELETE_MESSAGE,Toast.LENGTH_SHORT).show()
                                        }
                                        else{
                                            Toast.makeText(activity,Constants.ERROR_MESSAGE,Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(150.dp) // Set the fixed width here
                                    .height(50.dp)
                                    .padding(4.dp)
                            ) {
                                Text(text = "Delete", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    selectedAccount?.let {
                                        val updatedData = PasswordModel(selectedAccount!!.id,accountName, userName, password)
                                        if(passwordViewModel.updatePassword(updatedData)){
                                            isSheetOpen= false
                                            Toast.makeText(activity,Constants.SUCCESS_UPDATE_MESSAGE,Toast.LENGTH_SHORT).show()
                                        }
                                        else{
                                            Toast.makeText(activity,Constants.ERROR_MESSAGE,Toast.LENGTH_SHORT).show()

                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .width(150.dp) // Set the fixed width here
                                    .height(50.dp)
                                    .padding(4.dp)
                            ) {
                                Text(text = "Update", color = Color.White)
                            }
                        }
                    }else{
                        // Add account Button
                        Button(
                            onClick = submitValues,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Add Account",color = Color.White)
                        }
                    }

                }

            }
        }
    }

}

@Composable
fun EditableTextField(
    accountName: MutableState<String>,
    isAccountNameValid: Boolean,
) {
    var textFieldValue by remember(accountName) { mutableStateOf(accountName.value) }

    TextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            accountName.value = newValue
        },
        label = { Text("Account Name") },
        modifier = Modifier.fillMaxWidth(),
        isError = !isAccountNameValid
    )
}


fun DecryptedPassword(password: String): String {
    if(password.isNullOrEmpty()){
        return ""
    }
    else{
        return EncryptionHelper.decrypt(password)
    }
}
@Composable
fun EditBottomSheetView(password: PasswordModel) {
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    if(isSheetOpen){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }) {
            // State variables to hold the text of each TextField
            var accountName by remember { mutableStateOf(password.accountName) }
            var userName by remember { mutableStateOf(password.userName) }
            var password by remember { mutableStateOf(password.password) }


            // Validation states for each TextField
            var isAccountnameValid by remember { mutableStateOf(true) }
            var isUsernameValid by remember { mutableStateOf(true) }
            var isPasswordValid by remember { mutableStateOf(true) }

            // Log all values when submit button is clicked
            val submitValues: () -> Unit = {
                // Validate each TextField
                isAccountnameValid = accountName.isNotEmpty()
                isUsernameValid = userName.isNotEmpty()
                isPasswordValid = password.isNotEmpty()

            /*    if(isAccountnameValid && isUsernameValid && isPasswordValid){
                    val model = PasswordModel(0,accountName = accountName, userName = userName, password = password)
                    addItem( model,items)
                    isSheetOpen = false
                    passwordViewModel.insertPassword(model)
                }*/
            }

            // A column to arrange the TextFields and Button vertically
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    TextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Account Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = !isAccountnameValid
                    )
                    if (!isAccountnameValid) {
                        Text(
                            text = "please enter account name",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
                Column {
                    TextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Username/Email") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = !isUsernameValid
                    )
                    if (!isUsernameValid) {
                        Text(
                            text = "please enter user name",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
                Column {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth() ,
                        isError = !isPasswordValid
                    )
                    if (!isPasswordValid) {
                        Text(
                            text = "please enter password",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /* Handle Red Button Click */ },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text(text = "Delete", color = Color.White)
                    }

                    Button(
                        onClick = { /* Handle Black Button Click */ },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text(text = "Edit", color = Color.White)
                    }
                }
                /*// Add account Button
                Button(
                    onClick = submitValues,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Add Account")
                }*/
            }
        }
    }

}

 @Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PasswordManagerTheme {
        Greeting("Android")
    }
}