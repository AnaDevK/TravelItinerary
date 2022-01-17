package com.app.mapyourway

import com.app.mapyourway.models.Place
import com.app.mapyourway.models.UserMap

fun generateSampleData(): MutableList<UserMap> {
    return mutableListOf(
        UserMap(0L,
            "Memories from University",
            mutableListOf(
                Place(0L, 0L,"Branner Hall", "Best dorm at Stanford", "google.com", 37.426, -122.163),
                Place(0L, 0L, "Gates CS building", "Many long nights in this basement", "google.com",37.430, -122.173),
                Place(0L, 0L, "Pinkberry", "First date with my wife", "google.com",37.444, -122.170)
            )
        ),
        UserMap(0L,"January vacation planning!",
            mutableListOf(
                Place(0L, 0L,"Tokyo", "Overnight layover", "google.com",35.67, 139.65),
                Place(0L, 0L,"Ranchi", "Family visit + wedding!", "google.com",23.34, 85.31),
                Place(0L, 0L, "Singapore", "Inspired by \"Crazy Rich Asians\"", "google.com",1.35, 103.82)
            )),
        UserMap(0L,"Singapore travel itinerary",
            mutableListOf(
                Place(0L, 0L, "Gardens by the Bay", "Amazing urban nature park", "google.com",1.282, 103.864),
                Place(0L, 0L, "Jurong Bird Park", "Family-friendly park with many varieties of birds", "google.com",1.319, 103.706),
                Place(0L, 0L, "Sentosa", "Island resort with panoramic views", "google.com",1.249, 103.830),
                Place(0L, 0L, "Botanic Gardens", "One of the world's greatest tropical gardens", "google.com",1.3138, 103.8159)
            )
        ),
        UserMap(0L,"My favorite places in the Midwest",
            mutableListOf(
                Place(0L, 0L, "Chicago", "Urban center of the midwest, the \"Windy City\"", "google.com",41.878, -87.630),
                Place(0L, 0L, "Rochester, Michigan", "The best of Detroit suburbia", "google.com",42.681, -83.134),
                Place(0L, 0L, "Mackinaw City", "The entrance into the Upper Peninsula", "google.com",45.777, -84.727),
                Place(0L, 0L, "Michigan State University", "Home to the Spartans", "google.com",42.701, -84.482),
                Place(0L, 0L, "University of Michigan", "Home to the Wolverines", "google.com",42.278, -83.738)
            )
        ),
        UserMap(0L,"Restaurants to try",
            mutableListOf(
                Place(0L, 0L, "Champ's Diner", "Retro diner in Brooklyn", "google.com",40.709, -73.941),
                Place(0L, 0L, "Althea", "Chicago upscale dining with an amazing view", "google.com",41.895, -87.625),
                Place(0L, 0L, "Shizen", "Elegant sushi in San Francisco", "google.com",37.768, -122.422),
                Place(0L, 0L, "Citizen Eatery", "Bright cafe in Austin with a pink rabbit", "google.com",30.322, -97.739),
                Place(0L, 0L, "Kati Thai", "Authentic Portland Thai food, served with love", "google.com",45.505, -122.635)
            )
        )
    )
}