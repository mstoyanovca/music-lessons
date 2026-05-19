package com.mstoyanov.myapplication.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mstoyanov.myapplication.dao.LessonViewModel
import com.mstoyanov.myapplication.entity.Lesson

@Composable
fun DeleteLessonAlertDialog(
    lesson: Lesson,
    onExpandedChange: (Boolean) -> Unit,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    lessonViewModel: LessonViewModel = viewModel()
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onShowDialogChange(false) },
            text = {
                Text(
                    text = "Are you sure you want to delete the lesson with ${lesson.student.firstName} ${lesson.student.lastName}?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onShowDialogChange(false)
                        onExpandedChange(false)
                        lessonViewModel.delete(lesson)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onShowDialogChange(false) }
                ) {
                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}
