package com.atheer.feature.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atheer.core.designsystem.AccentCyan
import com.atheer.core.designsystem.ActiveGreen
import com.atheer.core.designsystem.DarkBg
import com.atheer.core.designsystem.DarkCard
import com.atheer.core.designsystem.GreyText
import com.atheer.core.designsystem.Red60
import com.atheer.core.model.VideoCategory

@Composable
fun CreateScreen(
    onPublished: () -> Unit,
    viewModel: CreateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "بوابة المبدعين - نشر فيديو جديد",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        TypeSelector(isShort = uiState.isShort, onTypeChange = viewModel::onTypeChange)

        OutlinedTextField(
            value = uiState.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text("عنوان الفيديو", color = GreyText) },
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("وصف محتوى الفيديو", color = GreyText) },
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Text("اختر التصنيف المتاح", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                VideoCategory.ENTERTAINMENT,
                VideoCategory.DOCUMENTARY,
                VideoCategory.EDUCATION,
                VideoCategory.GAMING,
            ).forEach { category ->
                Button(
                    onClick = { viewModel.onCategoryChange(category) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category == uiState.category) AccentCyan else DarkCard,
                    ),
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                ) {
                    Text(category.label, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        OutlinedTextField(
            value = uiState.videoUrl,
            onValueChange = viewModel::onVideoUrlChange,
            label = { Text("رابط البث للفيديو (رابط MP4 خارجي متاح)", color = GreyText) },
            placeholder = { Text("اختياري - سيتم ملء رابط تلقائي", color = Color.Gray) },
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = uiState.thumbnailUrl,
            onValueChange = viewModel::onThumbnailUrlChange,
            label = { Text("رابط الصورة المصغرة (Thumbnail URL)", color = GreyText) },
            placeholder = { Text("اكتب رابط صورة للغلاف", color = Color.Gray) },
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.size(12.dp))

        Button(
            onClick = { viewModel.publish(onSuccess = onPublished) },
            enabled = uiState.canPublish && !uiState.isPublished,
            colors = ButtonDefaults.buttonColors(containerColor = Red60),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("نشر ومشاركة الفيديو الآن 🎬", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        if (uiState.isPublished) {
            Card(
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFF0F2D24)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(DarkBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, ActiveGreen),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(Icons.Default.CheckCircle, "نجاح", tint = ActiveGreen, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        "تهانينا! تم رفع ونشر مقطع الفيديو الخاص بك بنجاح على أثير.",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeSelector(isShort: Boolean, onTypeChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(4.dp),
    ) {
        Button(
            onClick = { onTypeChange(false) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isShort) Red60 else Color.Transparent,
            ),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text("فيديو طويل (يوتيوب)", color = Color.White, fontSize = 13.sp)
        }
        Button(
            onClick = { onTypeChange(true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isShort) Red60 else Color.Transparent,
            ),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text("مقطع قصير (Shorts)", color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Red60,
    unfocusedBorderColor = Color(0x33FFFFFF),
    focusedContainerColor = DarkCard,
    unfocusedContainerColor = DarkCard,
)
