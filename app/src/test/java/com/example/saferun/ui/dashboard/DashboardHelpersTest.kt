package com.example.saferun.ui.dashboard

import com.example.saferun.ui.components.cardinalName
import com.example.saferun.ui.map.RoutePoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Dashboard helpers")
class DashboardHelpersTest {

    @Nested
    @DisplayName("formatDuration")
    inner class FormatDuration {

        @Test
        fun `formats zero as mm ss`() {
            assertEquals("00:00", formatDuration(0))
        }

        @Test
        fun `formats under one hour as mm ss`() {
            assertEquals("01:05", formatDuration(65_000))
        }

        @Test
        fun `formats over one hour as h mm ss`() {
            assertEquals("1:02:05", formatDuration(3_725_000))
        }

        @Test
        fun `ignores negative values`() {
            assertEquals("00:00", formatDuration(-1_000))
        }
    }

    @Nested
    @DisplayName("polylineLengthKm")
    inner class PolylineLength {

        @Test
        fun `empty and single point polylines have no length`() {
            assertEquals(0.0, polylineLengthKm(emptyList()))
            assertEquals(0.0, polylineLengthKm(listOf(RoutePoint(0.0, 0.0))))
        }

        @Test
        fun `one degree of latitude is about 111 kilometres`() {
            val distance = polylineLengthKm(
                listOf(
                    RoutePoint(0.0, 0.0),
                    RoutePoint(1.0, 0.0),
                ),
            )
            assertTrue(distance in 110.0..112.0, "unexpected distance: $distance")
        }

        @Test
        fun `sums every segment`() {
            val points = listOf(
                RoutePoint(0.0, 0.0),
                RoutePoint(0.0, 0.01),
                RoutePoint(0.0, 0.02),
            )
            val single = polylineLengthKm(listOf(RoutePoint(0.0, 0.0), RoutePoint(0.0, 0.01)))
            assertEquals(single * 2, polylineLengthKm(points), 0.0001)
        }
    }

    @Nested
    @DisplayName("cardinalName")
    inner class CardinalName {

        @Test
        fun `maps the eight cardinal points`() {
            assertEquals("N", cardinalName(0f))
            assertEquals("NE", cardinalName(45f))
            assertEquals("E", cardinalName(90f))
            assertEquals("SE", cardinalName(135f))
            assertEquals("S", cardinalName(180f))
            assertEquals("SO", cardinalName(225f))
            assertEquals("O", cardinalName(270f))
            assertEquals("NO", cardinalName(315f))
        }

        @Test
        fun `normalizes negative and overflow headings`() {
            assertEquals("N", cardinalName(360f))
            assertEquals("N", cardinalName(-10f))
            assertEquals("O", cardinalName(-90f))
        }
    }

    @Nested
    @DisplayName("DemoRoute")
    inner class DemoRouteTest {

        @Test
        fun `route is a closed loop`() {
            val first = DemoRoute.points.first()
            val last = DemoRoute.points.last()
            assertEquals(first.latitude, last.latitude, 0.0001)
            assertEquals(first.longitude, last.longitude, 0.0001)
        }

        @Test
        fun `route length is realistic for the demo`() {
            assertTrue(DemoRoute.totalKm in 1.0..10.0, "unexpected length: ${DemoRoute.totalKm}")
        }

        @Test
        fun `reveal never returns fewer than two points`() {
            assertEquals(2, DemoRoute.reveal(0f).size)
            assertEquals(DemoRoute.points.size, DemoRoute.reveal(1f).size)
            assertEquals(DemoRoute.points.size, DemoRoute.reveal(5f).size)
        }

        @Test
        fun `revealed route starts at the same point`() {
            val revealed = DemoRoute.reveal(0.5f)
            assertEquals(DemoRoute.points.first(), revealed.first())
        }
    }
}
