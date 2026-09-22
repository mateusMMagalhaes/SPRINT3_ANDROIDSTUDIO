package com.example.simv.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.simv.R
import com.example.simv.ui.theme.SimvBorder
import com.example.simv.ui.theme.SimvCyan
import com.example.simv.ui.theme.SimvDarkBg
import com.example.simv.ui.theme.SimvError
import com.example.simv.ui.theme.SimvInputFieldBg
import com.example.simv.ui.theme.SimvTextPrimary
import com.example.simv.ui.theme.SimvTextSecondary
import com.example.simv.ui.viewmodel.SetupFormState

@Composable
fun SetupRunScreen(
    formState: SetupFormState,
    onProjectNameChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onOperatorIdChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val fetchLocation = {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager != null) {
            try {
                val gpsLoc: Location? = if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } else null

                val netLoc: Location? = if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                } else null

                val bestLoc: Location? = gpsLoc ?: netLoc
                if (bestLoc != null) {
                    val formatted = String.format(java.util.Locale.US, "%.4f, %.4f", bestLoc.latitude, bestLoc.longitude)
                    onLocationChanged(formatted)
                    Toast.makeText(context, "Localização GPS obtida com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    onLocationChanged("-23.5505, -46.6333")
                    Toast.makeText(context, "Localização GPS obtida (padrão).", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Permissão de localização necessária.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchLocation()
        } else {
            Toast.makeText(context, "Permissão de localização é necessária para obter GPS.", Toast.LENGTH_LONG).show()
        }
    }

    val requestLocationPermission = {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SimvDarkBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.setup_run),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SimvTextPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Project Name Field
            SimvInputField(
                label = stringResource(R.string.project_name_label),
                placeholder = stringResource(R.string.project_name_hint),
                value = formState.projectName,
                onValueChange = onProjectNameChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Location Field with GPS Button
            SimvLocationInputField(
                label = stringResource(R.string.location_label),
                placeholder = stringResource(R.string.location_hint),
                value = formState.location,
                onValueChange = onLocationChanged,
                onGetLocationClick = requestLocationPermission
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Operator ID Field
            SimvInputField(
                label = stringResource(R.string.operator_id_label),
                placeholder = stringResource(R.string.operator_id_hint),
                value = formState.operatorId,
                onValueChange = onOperatorIdChanged
            )

            formState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = SimvError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Button(
            onClick = onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SimvCyan,
                contentColor = SimvDarkBg
            )
        ) {
            Text(
                text = stringResource(R.string.next),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun SimvInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = SimvCyan,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = SimvTextSecondary
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SimvInputFieldBg,
                unfocusedContainerColor = SimvInputFieldBg,
                focusedBorderColor = SimvCyan,
                unfocusedBorderColor = SimvBorder,
                focusedTextColor = SimvTextPrimary,
                unfocusedTextColor = SimvTextPrimary,
                cursorColor = SimvCyan
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun SimvLocationInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    onGetLocationClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = SimvCyan,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = SimvTextSecondary
                    )
                },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SimvInputFieldBg,
                    unfocusedContainerColor = SimvInputFieldBg,
                    focusedBorderColor = SimvCyan,
                    unfocusedBorderColor = SimvBorder,
                    focusedTextColor = SimvTextPrimary,
                    unfocusedTextColor = SimvTextPrimary,
                    cursorColor = SimvCyan
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onGetLocationClick,
                modifier = Modifier.height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SimvCyan
                )
            ) {
                Text(
                    text = "GPS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
