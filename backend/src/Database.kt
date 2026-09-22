package backend

import java.sql.*
import java.util.Properties

import kotlin.collections.List
import kotlin.collections.ArrayList

object Database {
    private var conn: Connection by lazy {
        try {
            Class.forName("org.postgresql.Driver")
            val props: Properties = Properties()
            props.setProperty("user", "dat257")
            props.setProperty("password", "dat257")
            DriverManager.getConnection("jdbc:postgresql://localhost/dat257", props)
        } catch (e: ClassNotFoundException) {
            System.out.println("Failed to find database driver class: " + e)
            System.exit(1)
        } catch (e: SQLException) {
            System.out.println("Failed to connect to database: " + e)
            System.exit(2)
        }
    }

    public data class Sighting(val id: Int, val userid: Int, val lat: Int, val long: Int, val time: Int)

    fun insertSighting(userid: Int, lat: Int, long: Int): Int? {
        try {
            Database.conn.prepareStatement("INSERT INTO Sightings (userid, lat, long) VALUES (?, ?, ?) RETURNING id")
                .use { st ->
                    st.setInteger(1, userid)
                    st.setInteger(2, lat)
                    st.setInteger(3, long)
                    val rs: ResultSet = st.executeQuery()
                    if (rs.next()) return rs.getInt(1)
                }
        } catch (e: SQLException) {
            System.out.println(e)
        }
        return null
    }

    // In the future this should be replaced by a method that filters to an area and groups nearby sightings
    fun getSightings(): Iterable<Sighting> {
        val ret: List<Sighting> = ArrayList()
        try {
            Database.conn.prepareStatement("SELECT id, userid, lat, long, time FROM Sightings ORDER BY id")
                .use { st ->
                    val rs: ResultSet = st.executeQuery()
                    while (rs.next()) ret.add(Sighting(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)))
                }
        } catch (e: SQLException) {
            System.out.println(e)
        }
        return ret
    }
}
