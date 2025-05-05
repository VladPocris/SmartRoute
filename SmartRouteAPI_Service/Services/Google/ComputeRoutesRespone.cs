using System.Text.Json.Serialization;

namespace TripsApi.Services.Google
{
    public class ComputeRoutesResponse
    {
        [JsonPropertyName("status")]
        public string Status { get; set; } = "";

        [JsonPropertyName("waypoint_order")]
        public int[] WaypointOrder { get; set; } = new int[0];

        [JsonPropertyName("routes")]
        public Route[] Routes { get; set; } = new Route[0];
    }

    public class Route
    {
        [JsonPropertyName("overview_polyline")]
        public Polyline OverviewPolyline { get; set; } = new Polyline();

        [JsonPropertyName("legs")]
        public Leg[] Legs { get; set; } = new Leg[0];
    }

    public class Leg
    {
        [JsonPropertyName("start_address")]
        public string StartAddress { get; set; } = "";

        [JsonPropertyName("end_address")]
        public string EndAddress { get; set; } = "";

        [JsonPropertyName("start_location")]
        public Location StartLocation { get; set; } = new Location();

        [JsonPropertyName("end_location")]
        public Location EndLocation { get; set; } = new Location();

        [JsonPropertyName("distance")]
        public Distance Distance { get; set; } = new Distance();

        [JsonPropertyName("duration")]
        public Duration Duration { get; set; } = new Duration();

        [JsonPropertyName("steps")]
        public Step[] Steps { get; set; } = new Step[0];
    }

    public class Step
    {
        [JsonPropertyName("polyline")]
        public Polyline Polyline { get; set; } = new Polyline();
    }

    public class Distance
    {
        [JsonPropertyName("value")]
        public int Value { get; set; }
    }

    public class Duration
    {
        [JsonPropertyName("value")]
        public int Value { get; set; }
    }

    public class Location
    {
        [JsonPropertyName("lat")]
        public double Lat { get; set; }

        [JsonPropertyName("lng")]
        public double Lng { get; set; }
    }

    public class Polyline
    {
        [JsonPropertyName("points")]
        public string Points { get; set; } = "";
    }
}