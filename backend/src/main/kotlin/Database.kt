package com.example.hogwatch.backend

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Properties
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

object Database {
    private var conn: Connection

    init {
        try {
            val props = Properties()
            props.setProperty("user", "dat257")
            props.setProperty("password", "dat257")
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/dat257", props)
        } catch (e: SQLException) {
            println("Failed to connect to database: $e")
            exitProcess(2)
        }
    }

    public data class Sighting(val id: Int, val userid: Int, val lat: Float, val long: Float, val time: Int)

    fun insertSighting(userid: Int, lat: Float, long: Float, time: Int): Int? {
        try {
            conn.prepareStatement("INSERT INTO Sightings (userid, lat, long, time) VALUES (?, ?, ?, ?) RETURNING id").use<PreparedStatement, Unit> {
                st ->
                st.setInt(1, userid)
                st.setFloat(2, lat)
                st.setFloat(3, long)
                st.setInt(4, time)
                val rs: ResultSet = st.executeQuery()
                if (rs.next()) return rs.getInt(1)
            }
        } catch (e: SQLException) {
            println(e)
        }
        return null
    }

    // In the future this should be replaced by a method that filters to an area and groups nearby sightings
    fun getSightings(): Iterable<Sighting> {
        val ret = ArrayList<Sighting>()
        try {
            conn.prepareStatement("SELECT id, userid, lat, long, time FROM Sightings ORDER BY id")
                .use<PreparedStatement, Unit> { st ->
                    val rs: ResultSet = st.executeQuery()
                    while (rs.next()) ret.add(Sighting(rs.getInt(1), rs.getInt(2), rs.getFloat(3), rs.getFloat(4), rs.getInt(5)))
                }
        } catch (e: SQLException) {
            println(e)
        }
        return ret
    }
}
