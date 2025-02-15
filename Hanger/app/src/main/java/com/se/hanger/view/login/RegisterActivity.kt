package com.se.hanger.view.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.se.hanger.R
import com.se.hanger.setStatusBarTransparent
import com.se.hanger.view.ui.theme.*

class RegisterActivity : ComponentActivity() {
    val viewModel: RegisterViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarTransparent()

        setContent {
            HangerTheme {
                // A surface container using the 'background' color from the theme
                RegisterView(onClickFinish = {
                    finish()
                })
            }
        }
    }
}


@Composable
fun RegisterView(onClickFinish: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Welcome!") },
                backgroundColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = onClickFinish) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_login),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RegisterCardView()
            }
        }
    }
}

@Composable
fun RegisterCardView() {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(topEnd = 40.dp, topStart = 40.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = "Get Started",
                style = TextStyle(
                    color = Brown700,
                    fontFamily = suncheonFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)
            )

            TextFieldContainer()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        color = Brown700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                RegisterButton(onClick = {
                    Toast.makeText(context, "회원가입 버튼", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

@Composable
fun RegisterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(shape = CircleShape)
            .background(Brown700)
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_right_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center),
            tint = Color.White
        )
    }
}

@Composable
fun TextFieldContainer() {
    var userNameInput by remember {
        mutableStateOf(TextFieldValue())
    }
    var passwordInput by remember {
        mutableStateOf(TextFieldValue())
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomTextField(
            userNameInput,
            "Username",
            ImageVector.vectorResource(id = R.drawable.ic_login_profile),
            onTextChanged = { newValue -> userNameInput = newValue }
        )
        CustomTextField(
            passwordInput,
            "Password",
            ImageVector.vectorResource(id = R.drawable.ic_password),
            onTextChanged = { newValue -> passwordInput = newValue }
        )
//        InputTextView(userNameInput, "Username", onTextChanged = { newValue ->
//            userNameInput = newValue
//        })
//        InputTextView(passwordInput, "Password", onTextChanged = { newValue ->
//            passwordInput = newValue
//        })
    }

}

//@Composable
//private fun InputTextView(
//    value: TextFieldValue,
//    hint: String,
//    onTextChanged: (TextFieldValue) -> Unit
//) {
//    OutlinedTextField(
//        modifier = Modifier.fillMaxWidth(),
//        value = value,
//        singleLine = true,
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//        onValueChange = onTextChanged,
//        label = { Text(hint) },
//        placeholder = { Text(text = hint) },
//    )
//
//}

@Composable
fun CustomTextField(
    value: TextFieldValue,
    placeHolderMsg: String,
    iconRes: ImageVector,
    onTextChanged: (TextFieldValue) -> Unit
) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .border(1.dp, Gray400, RoundedCornerShape(8.dp))
            .background(Gray200),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box() {
            Icon(
                modifier = Modifier
                    .padding(10.dp),
                imageVector = iconRes,
                contentDescription = iconRes.toString(),
                tint = Gray400
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        TextField(
            value = value,
            onValueChange = onTextChanged,
            shape = RoundedCornerShape(4.dp),
            placeholder = { Text(placeHolderMsg) },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Brown700,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.LightGray,
                placeholderColor = Gray400
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HangerTheme {
        RegisterView(onClickFinish = {

        })
    }
}