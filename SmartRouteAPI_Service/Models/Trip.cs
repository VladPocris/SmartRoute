using System.ComponentModel.DataAnnotations;
using System.Collections.Generic;

namespace TripsApi.Models
{
    public class Trip
    {
        public int Id { get; set; }

        [Required, StringLength(6)]
        public string Code { get; set; } = "";

        public string Name { get; set; } = "";
        public string? OriginalQuery { get; set; }
        public string OriginalQueryKey { get; set; } = "";
        public string? OverviewPolyline { get; set; }
        public ICollection<TripStep> Steps { get; set; } = new List<TripStep>();
    }
}
