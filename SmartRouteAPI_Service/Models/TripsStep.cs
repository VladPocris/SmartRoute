using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace TripsApi.Models
{
    public class TripStep
    {
        [Key]
        public int Id { get; set; }

        [ForeignKey("Trip")]
        public int TripId { get; set; }
        public Trip? Trip { get; set; }

        [Required]
        public string From { get; set; } = "";

        [Required]
        public string To { get; set; } = "";

        public double DistanceKm { get; set; }
        public int DurationMinutes { get; set; }
        public int Order { get; set; }

        public double StartLat { get; set; }
        public double StartLng { get; set; }
        public double EndLat { get; set; }
        public double EndLng { get; set; }
        public string? EncodedPolyline { get; set; }
    }
}
