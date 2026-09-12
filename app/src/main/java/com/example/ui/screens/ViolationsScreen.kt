package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DateRangeType
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.data.model.ViolationRecord
import com.example.ui.components.ViolationCard
import com.example.ui.theme.BlinkingUnderscoreCursor
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.Sp2Rose
import com.example.ui.theme.Sp2RoseBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ViolationsScreen(
    records: List<ViolationRecord>,
    categories: List<ViolationCategory>,
    masters: List<ViolationMaster>,
    searchQuery: String,
    selectedCategory: String,
    selectedDateRange: DateRangeType,
    onSearchQueryChange: (String) -> Unit,
    onCategoryFilterChange: (String) -> Unit,
    onDateRangeChange: (DateRangeType) -> Unit,
    onRecordViolationClick: () -> Unit,
    onNotifyParentForRecord: (ViolationRecord) -> Unit,
    onDeleteRecord: (ViolationRecord) -> Unit,
    onAddCustomMasterClick: () -> Unit,
    canModifyPoints: Boolean = true,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            if (canModifyPoints) {
                FloatingActionButton(
                    onClick = {
                        if (selectedTabIndex == 0) onRecordViolationClick()
                        else onAddCustomMasterClick()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_violations_action")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (selectedTabIndex == 0) Icons.Default.AddAlert else Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "+ Catat Pelanggaran" else "+ Aturan Baru",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Header (Riwayat vs Master Katalog)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Riwayat Masuk (${records.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Katalog Poin & Aturan (${masters.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (selectedTabIndex == 0) {
                // TAB 0: RIWAYAT PELANGGARAN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Search Box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Cari siswa, kasus, kelas...")
                                BlinkingUnderscoreCursor(
                                    color = PrimaryBlueLight,
                                    fontSize = 14.sp
                                )
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Date Range Filters
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(DateRangeType.values()) { range ->
                            FilterChip(
                                selected = selectedDateRange == range,
                                onClick = { onDateRangeChange(range) },
                                label = { Text(range.label, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Category Filters
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == "Semua Kategori",
                                onClick = { onCategoryFilterChange("Semua Kategori") },
                                label = { Text("Semua Kategori", fontSize = 11.sp) }
                            )
                        }
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.name,
                                onClick = { onCategoryFilterChange(cat.name) },
                                label = { Text(cat.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    // List of Recorded Violations
                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SentimentDissatisfied, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Tidak ada riwayat pelanggaran pada filter ini", fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                        ) {
                            items(records, key = { it.id }) { rec ->
                                ViolationCard(
                                    record = rec,
                                    onNotifyParent = { onNotifyParentForRecord(rec) },
                                    onDeleteRecord = { onDeleteRecord(rec) },
                                    canModifyPoints = canModifyPoints
                                )
                            }
                        }
                    }
                }
            } else {
                // TAB 1: MASTER ATURAN & BOBOT SKOR
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == "Semua Kategori",
                                onClick = { onCategoryFilterChange("Semua Kategori") },
                                label = { Text("Semua Kategori", fontSize = 12.sp) }
                            )
                        }
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat.name,
                                onClick = { onCategoryFilterChange(cat.name) },
                                label = { Text(cat.name, fontSize = 12.sp) }
                            )
                        }
                    }

                    val filteredMasters = if (selectedCategory == "Semua Kategori") masters
                    else masters.filter { it.categoryName == selectedCategory }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredMasters, key = { it.id }) { master ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = master.code,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = master.categoryName,
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = master.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Sanksi Standar: ${master.defaultSanction}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Sp2RoseBg)
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "+${master.points} Poin",
                                            fontWeight = FontWeight.Black,
                                            color = Sp2Rose,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
