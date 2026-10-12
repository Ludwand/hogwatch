package com.example.hogwatch.backend

import com.example.hogwatch.shared.SightingSummary
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
         println("Have you ran the setup script schema.sql?")
         exitProcess(2)
      }
   }

   data class Sighting(
      val id: Int,
      val userid: String,
      val lat: Double,
      val long: Double,
      val time: Long,
      val image: String?
   )

   fun insertSighting(
      userid: String?,
      lat: Double,
      long: Double,
      time: Long,
      image: String?
   ): Int? {
      try {
         conn.prepareStatement(
            "INSERT INTO Sightings (userid, lat, long, time, image) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "RETURNING id"
         ).use<PreparedStatement, Unit>
         { st ->
            st.setString(1, userid)
            st.setDouble(2, lat)
            st.setDouble(3, long)
            st.setLong(4, time)
            st.setString(5, image)
            val rs: ResultSet = st.executeQuery()
            if (rs.next()) return rs.getInt(1)
         }
      } catch (e: SQLException) {
         println(e)
      }
      return null
   }

   fun getSighting(id: Int): Sighting? {
      try {
         conn.prepareStatement("SELECT userid, lat, long, time, image FROM Sightings WHERE id = ?")
            .use<PreparedStatement, Unit> { st ->
               st.setInt(1, id)
               val rs: ResultSet = st.executeQuery()
               if (rs.next()) return Sighting(
                  id,
                  rs.getString(1),
                  rs.getDouble(2),
                  rs.getDouble(3),
                  rs.getLong(4),
                  rs.getString(5)
               )
            }
      } catch (e: SQLException) {
         println(e)
      }
      return null
   }

   fun getSightingSummary(id: Int): SightingSummary? {
      try {
         conn.prepareStatement("SELECT lat, long, image IS NOT NULL FROM Sightings WHERE id = ?")
            .use<PreparedStatement, Unit> { st ->
               st.setInt(1, id)
               val rs: ResultSet = st.executeQuery()
               if (rs.next()) return SightingSummary(
                  id,
                  rs.getDouble(1),
                  rs.getDouble(2),
                  rs.getBoolean(3)
               )
            }
      } catch (e: SQLException) {
         println(e)
      }
      return null
   }

   fun getSightingImage(id: Int): String? {
      try {
         conn.prepareStatement("SELECT image FROM Sightings WHERE id = ?")
            .use<PreparedStatement, Unit> { st ->
               st.setInt(1, id)
               val rs: ResultSet = st.executeQuery()
               if (rs.next()) return rs.getString(1)
            }
      } catch (e: SQLException) {
         println(e)
      }
      return null
   }

   fun getSightingSummaries(userid: String): ArrayList<SightingSummary> {
      val ret = ArrayList<SightingSummary>()
      try {
         conn.prepareStatement("SELECT id, lat, long, image IS NOT NULL FROM Sightings WHERE userid = ? ORDER BY id")
            .use<PreparedStatement, Unit> { st ->
               st.setString(1, userid)
               val rs: ResultSet = st.executeQuery()
               while (rs.next()) ret.add(
                  SightingSummary(
                     rs.getInt(1),
                     rs.getDouble(2),
                     rs.getDouble(3),
                     rs.getBoolean(4)
                  )
               )
            }
      } catch (e: SQLException) {
         println(e)
      }
      return ret
   }

   fun getSightingSummaries(minLat: Double, maxLat: Double, minLong: Double, maxLong: Double): ArrayList<SightingSummary> {
      val ret = ArrayList<SightingSummary>()
      try {
         conn.prepareStatement("SELECT id, lat, long, image IS NOT NULL FROM Sightings WHERE lat >= ? AND lat <= ? AND long >= ? AND long <= ? ORDER BY id")
            .use<PreparedStatement, Unit> { st ->
               st.setDouble(1, minLat)
               st.setDouble(2, maxLat)
               st.setDouble(3, minLong)
               st.setDouble(4, maxLong)
               val rs: ResultSet = st.executeQuery()
               while (rs.next()) ret.add(
                  SightingSummary(
                     rs.getInt(1),
                     rs.getDouble(2),
                     rs.getDouble(3),
                     rs.getBoolean(4)
                  )
               )
            }
      } catch (e: SQLException) {
         println(e)
      }
      return ret
   }
}
