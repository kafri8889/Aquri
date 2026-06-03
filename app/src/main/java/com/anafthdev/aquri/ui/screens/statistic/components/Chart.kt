package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.ui.components.AquriSelector
import com.anafthdev.aquri.ui.components.AquriSelectorOption
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.theme.AquriTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LayeredComponent
import com.patrykandpatrick.vico.compose.common.MarkerCornerBasedShape
import com.patrykandpatrick.vico.compose.common.Position
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlinx.coroutines.runBlocking

@Composable
private fun CoreChart(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ClayCard(
        modifier = modifier,
        containerColor = AquriTheme.clay.surfaceStrong
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun DailyMainBarChart(
    chartYData: List<Float>,
    chartXData: List<String>,
    peakActivityHour: Int?,
    modifier: Modifier = Modifier
) {

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(chartYData) {
        modelProducer.runTransaction {
            extras {
                it[BottomAxisLabelKey] = chartXData
            }

            columnSeries {
                series(chartYData)
            }
        }
    }

    if (LocalInspectionMode.current) {
        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    series(chartYData)
                }
            }
        }
    }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val is24Hour = android.text.format.DateFormat.is24HourFormat(context)

    CoreChart(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Start)
        ) {
            Text(
                text = "Hourly Intake",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            val hourStr = remember(configuration, peakActivityHour, is24Hour) {
                if (peakActivityHour == null) return@remember "-"

                val locale = configuration.locales[0]

                val formatHour: (Int) -> String = { h ->
                    if (is24Hour) {
                        String.format(locale, "%02d:00", h)
                    } else {
                        when {
                            h == 0 -> "12 AM"
                            h < 12 -> "$h AM"
                            h == 12 -> "12 PM"
                            else -> "${h - 12} PM"
                        }
                    }
                }

                val start = formatHour(peakActivityHour)
                val next = (peakActivityHour + 1) % 24
                val end = formatHour(next)
                "$start - $end"
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AquriTheme.colorScheme.lightText
                        ).toSpanStyle()
                    ) {
                        append("Peak activity at")
                        append(" ")
                    }

                    withStyle(
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ).toSpanStyle()
                    ) {
                        append(hourStr)
                    }
                }
            )
        }

        CartesianChartHost(
            modelProducer = modelProducer,
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = StartAxisValueFormatter
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = BottomAxisValueFormatter,
                    guideline = null
                ),
                layerPadding = {
                    CartesianLayerPadding(
                        scalableStart = 8.dp,
                        scalableEnd = 8.dp
                    )
                },
                marker = rememberMarker(WaterVolumeValueFormatterBarChart)
            ),
            modifier = Modifier
                .height(216.dp)
        )
    }
}

@Composable
fun WeeklyMainBarChart(
    chartYData: List<Float>,
    chartXData: List<String>,
    modifier: Modifier = Modifier
) {

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(chartYData) {
        modelProducer.runTransaction {
            extras {
                it[BottomAxisLabelKey] = chartXData
            }

            columnSeries {
                series(chartYData)
            }
        }
    }

    if (LocalInspectionMode.current) {
        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    series(chartYData)
                }
            }
        }
    }

    CoreChart(
        modifier = modifier
    ) {
        CartesianChartHost(
            modelProducer = modelProducer,
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = StartAxisValueFormatter
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = BottomAxisValueFormatter,
                    guideline = null
                ),
                layerPadding = {
                    CartesianLayerPadding(
                        scalableStart = 8.dp,
                        scalableEnd = 8.dp
                    )
                },
                marker = rememberMarker(WaterVolumeValueFormatterBarChart)
            ),
            modifier = Modifier
                .height(216.dp)
        )
    }
}

@Composable
fun MonthlyMainBarChart(
    chartYData: List<Float>,
    chartXData: List<String>,
    modifier: Modifier = Modifier
) {

    val modelProducer = remember { CartesianChartModelProducer() }
    var selectedChartType by remember { mutableIntStateOf(1) } // 1 for Bar, 2 for Line

    LaunchedEffect(chartYData, selectedChartType) {
        modelProducer.runTransaction {
            extras {
                it[BottomAxisLabelKey] = chartXData
            }

            columnSeries {
                series(chartYData)
            }

            lineSeries {
                series(chartYData)
            }
        }
    }

    if (LocalInspectionMode.current) {
        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    series(chartYData)
                }
            }
        }
    }

    CoreChart(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Monthly Intake",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Visualize your hydration trends",
                style = MaterialTheme.typography.bodySmall,
                color = AquriTheme.colorScheme.lightText
            )
        }

        if (selectedChartType == 1) {
            CartesianChartHost(
                modelProducer = modelProducer,
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = StartAxisValueFormatter
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = BottomAxisValueFormatter,
                        guideline = null
                    ),
                    layerPadding = {
                        CartesianLayerPadding(
                            scalableStart = 8.dp,
                            scalableEnd = 8.dp
                        )
                    },
                    marker = rememberMarker(
                        WaterVolumeValueFormatterBarChart
                    )
                ),
                modifier = Modifier
                    .height(216.dp)
            )
        } else {
            CartesianChartHost(
                modelProducer = modelProducer,
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = StartAxisValueFormatter
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = BottomAxisValueFormatter,
                        guideline = null
                    ),
                    layerPadding = {
                        CartesianLayerPadding(
                            scalableStart = 8.dp,
                            scalableEnd = 8.dp
                        )
                    },
                    marker = rememberMarker(
                        WaterVolumeValueFormatterLineChart
                    )
                ),
                modifier = Modifier
                    .height(216.dp)
            )
        }

        AquriSelector(
            option1 = AquriSelectorOption("Bar", Icons.Default.BarChart),
            option2 = AquriSelectorOption("Line", Icons.AutoMirrored.Filled.ShowChart),
            selectedOption = selectedChartType,
            onOptionSelected = { selectedChartType = it }
        )
    }
}

@Composable
internal fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(),
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = MarkerCornerBasedShape(CircleShape)
    val labelBackground =
        rememberShapeComponent(
            fill = Fill(MaterialTheme.colorScheme.background),
            shape = labelBackgroundShape,
            strokeFill = Fill(MaterialTheme.colorScheme.outline),
            strokeThickness = 1.dp,
        )
    val label =
        rememberTextComponent(
            style =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                ),
            padding = Insets(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(Fill(MaterialTheme.colorScheme.surface), CircleShape)
    val guideline = rememberAxisGuidelineComponent()
    return rememberDefaultCartesianMarker(
        label = label,
        valueFormatter = valueFormatter,
        indicator =
            if (showIndicator) {
                { color ->
                    LayeredComponent(
                        back = ShapeComponent(Fill(color.copy(alpha = 0.15f)), CircleShape),
                        front =
                            LayeredComponent(
                                back = ShapeComponent(fill = Fill(color), shape = CircleShape),
                                front = indicatorFrontComponent,
                                padding = Insets(5.dp),
                            ),
                        padding = Insets(10.dp),
                    )
                }
            } else {
                null
            },
        indicatorSize = 36.dp,
        guideline = guideline,
    )
}

@Composable
private fun rememberHorizontalLine(
    color: Color = Color(0xfffdc8c4),
    label: String = "Goal: 1000ml",
): HorizontalLine {
    val fill = Fill(color)
    val line = rememberLineComponent(fill = fill, thickness = 2.dp)
    val labelComponent =
        rememberTextComponent(
            margins = Insets(start = 6.dp),
            padding = Insets(start = 8.dp, top = 2.dp, end = 8.dp, bottom = 4.dp),
            background =
                rememberShapeComponent(fill, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)),
        )

    return remember {
        HorizontalLine(
            y = { 0.0 },
            line = line,
            labelComponent = labelComponent,
            label = { label },
            verticalLabelPosition = Position.Vertical.Bottom,
        )
    }
}


private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()

private val StartAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
    "${value.toInt()}ml"
}

private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore.getOrNull(BottomAxisLabelKey)?.getOrNull(x.toInt()) ?: "-"
}

private val WaterVolumeValueFormatterBarChart =
    DefaultCartesianMarker.ValueFormatter { _, targets ->
        val column = (targets[0] as ColumnCartesianLayerMarkerTarget).columns[0]
        buildAnnotatedString {
            withStyle(SpanStyle(column.color)) {
                val value = column.entry.y.toInt().toString()
                append(value)
                append("ml")
            }
        }
    }
private val WaterVolumeValueFormatterLineChart =
    DefaultCartesianMarker.ValueFormatter { _, targets ->
        val column = (targets[0] as LineCartesianLayerMarkerTarget).points[0]
        buildAnnotatedString {
            withStyle(SpanStyle(column.color)) {
                val value = column.entry.y.toInt().toString()
                append(value)
                append("ml")
            }
        }
    }
