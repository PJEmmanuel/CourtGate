package com.example.courtgate.ui.presentation.match.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.courtgate.R
import com.example.courtgate.ui.presentation.core.asStringRes
import com.example.courtgate.ui.presentation.match.MatchSheetState
import com.example.courtgate.ui.presentation.match.ResultFormError
import com.example.courtgate.ui.presentation.match.Team

@Composable
fun ResultFormSheetContent(
    state: MatchSheetState.Editing,
    onScoreChange: (setIndex: Int, team: Team, value: String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(
                if (state.form.isNew) R.string.match_form_title_new else R.string.match_form_title_edit
            ),
            style = MaterialTheme.typography.titleLarge
        )

        //TODO: Mirar ese if else. es mejorable o correcto aquí?
        state.form.sets.forEachIndexed { index, set ->
            val setNumber = index + 1
            // Solo el último set es opcional
            val label = if (setNumber == state.form.sets.size) {
                stringResource(R.string.match_set_optional_label, setNumber)
            } else {
                stringResource(R.string.match_set_label, setNumber)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                ScoreField(
                    value = set.player1,
                    label = R.string.match_team_1,
                    enabled = !state.isSaving,
                    onValueChange = { onScoreChange(index, Team.First, it) },
                    modifier = Modifier.weight(1f)
                )
                Text("-", modifier = Modifier.padding(horizontal = 8.dp))
                ScoreField(
                    value = set.player2,
                    label = R.string.match_team_2,
                    enabled = !state.isSaving,
                    onValueChange = { onScoreChange(index, Team.Second, it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val errorMessage = state.formError?.asStringRes() ?: state.saveError?.asStringRes()
        if (errorMessage != null) {
            Text(
                text = stringResource(errorMessage),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss, enabled = !state.isSaving) {
                Text(stringResource(R.string.match_action_cancel))
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onSave, enabled = !state.isSaving) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.match_action_save))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ScoreField(
    value: String,
    @StringRes label: Int,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label), maxLines = 1) },
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

//TODO: Esto es correcto aquí?
@StringRes
private fun ResultFormError.asStringRes(): Int = when (this) {
    ResultFormError.MissingSets -> R.string.match_error_missing_sets
    ResultFormError.InvalidGames -> R.string.match_error_invalid_games
    ResultFormError.TiedSet -> R.string.match_error_tied_set
    ResultFormError.ThirdSetNotNeeded -> R.string.match_error_third_set
}
