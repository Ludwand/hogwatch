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
         println("Have you ran the setup script schema.sql?")
         exitProcess(2)
      }
   }

   public data class Sighting(
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

   // In the future this should be replaced by a method that filters to an area and groups nearby sightings
   fun getSightings(): Iterable<Sighting> {
      val ret = ArrayList<Sighting>()
      try {
         conn.prepareStatement("SELECT id, userid, lat, long, time, image FROM Sightings ORDER BY id")
            .use<PreparedStatement, Unit> { st ->
               val rs: ResultSet = st.executeQuery()
               while (rs.next()) ret.add(
                  Sighting(
                     rs.getInt(1),
                     rs.getString(2),
                     rs.getDouble(3),
                     rs.getDouble(4),
                     rs.getLong(5),
                     rs.getString(6)
                  )
               )
            }
      } catch (e: SQLException) {
         println(e)
      }
      return ret
   }
}
